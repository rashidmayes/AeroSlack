package com.rashidmayes.bots.aerospike.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import com.aerospike.client.Info;
import com.aerospike.client.cluster.Node;
import com.rashidmayes.bots.aerospike.AerospikeSlackAgent;

public abstract class InfoCommandHandler extends CommandHandler {

	AerospikeSlackAgent mAgent;
	Logger mLogger = Logger.getLogger(getClass().getSimpleName());
	
	public InfoCommandHandler() {

	}

	public void setSlackAgent(AerospikeSlackAgent agent) {
		this.mAgent = agent;
	}
	

	public Map<String, String> map(String in, String delim) {
		Map<String, String> map = new HashMap<String, String>();
		String[] kvPair;
		for ( String pair : StringUtils.split(in,delim) ) {
			kvPair = pair.split("=");
			map.put(kvPair[0], kvPair[1]);
		}
		
		return map;
	}	
	
	public String getValue(Map<?,?> map, String... keys) {
		if ( map == null ) {
			return null;
		} else {
			Object value = null;
			for (String key : keys) {
				value = map.get(key);
				if ( value != null ) {
					break;
				}
			}
			
			return (value == null) ? null : value.toString();
		}
	}
	
	public NodeInfo getNodeInfo(Node node) {
		NodeInfo nodeInfo = new NodeInfo();
		
		Map<String,String> details =  Info.request(null,node);
		nodeInfo.id = details.get("node");
		nodeInfo.build = details.get("build");
		nodeInfo.edition = details.get("edition");
		nodeInfo.version = details.get("version");
		nodeInfo.statistics = map(details.get("statistics"), ";");
		
		return nodeInfo;
	}
		
	public List<NamespaceInfo> getNamespaceInfo(Node node) {	

		List<NamespaceInfo> namespaces = new ArrayList<NamespaceInfo>();
		NamespaceInfo namespaceInfo;

		for ( String namespace : StringUtils.split(Info.request(null, node, "namespaces"), ";") ) {			
			namespaceInfo = new NamespaceInfo();
			namespaceInfo.name = namespace;
			namespaceInfo.properties = map(Info.request(null, node, "namespace/" + namespace), ";");	
			namespaces.add(namespaceInfo);
		}
		
		return namespaces;
	}
	
	
	public NamespaceInfo getNamespaceInfo(Node node, String namespace) {	

		List<NamespaceInfo> namespaces = new ArrayList<NamespaceInfo>();
		NamespaceInfo namespaceInfo = new NamespaceInfo();
		namespaceInfo.name = namespace;
		namespaceInfo.properties = map(Info.request(null, node, "namespace/" + namespace), ";");	
		namespaces.add(namespaceInfo);
		
		return namespaceInfo;
	}
	
	public List<SetInfo> getSetInfo(Node node, String namespace) {
		
		List<SetInfo> sets = new ArrayList<SetInfo>();
		SetInfo setInfo;
		
		Map<String, String> map;
		for ( String set : StringUtils.split(Info.request(null, node, "sets/" + namespace), ";") ) {
			map = map(set, ":");
			setInfo = new SetInfo();
			setInfo.properties = map;
			setInfo.bytesMemory = getLong(map,0,"n-bytes-memory");
			setInfo.name = map.get("set_name");
			setInfo.namespace = map.get("ns_name");
			setInfo.objectCount = getLong(map,0,"n_objects");

			sets.add(setInfo);
		}
		
		return sets;
	}
	
	static class NodeInfo {
		public String id;
		public String build;
		public String edition;
		public String version;
		public Map<String,String> statistics;			
	}
	
	static class NamespaceInfo {
		public String name;
		public Map<String, String> properties  = new HashMap<String, String>();		
		public String[] bins;
		public SetInfo[] sets;
		
		public long getObjects() {
			return getLong(properties, 0, "objects");
		}
		
		public String getType() {
			return getString(properties, "", "type");
		}
		
		public long getProleObjects() {
			return getLong(properties, 0, "prole-objects");
		}
		
		public long getUsedBytesMemory() {
			return getLong(properties, 0, "used-bytes-memory");
		}
		
		public long getReplicationFactor() {
			return getLong(properties, 0, "repl-factor");
		}
		
		public long getUsedBytesDisk() {
			return getLong(properties, 0, "used-bytes-disk");
		}
		
		public long getMasterObjects() {
			return getLong(properties, 0, "master-objects");
		}
		
		public long getTotalBytesMemory() {
			return getLong(properties, 0, "total-bytes-memory");
		}
		
		public long getTotalBytesDisk() {
			return getLong(properties, 0, "total-bytes-disk");
		}
		
		public long getFreeMemoryPercent() {
			return getLong(properties, 0, "free-pct-memory");
		}
		
		public long getFreeDiskPercent() {
			return getLong(properties, 0, "free-pct-disk");
		}		
	}
	
	static class SetInfo {
		public String namespace;
		public String name;
		public long objectCount;
		public long bytesMemory;
		public Map<String, String> properties;
	}
	
	
	
	public static String getProperty(Map<String, String> properties, String... keys) {
		if ( properties != null ) {
			String value;
			for (String key : keys) {
				value = properties.get(key);
				if ( value != null) {
					return value;
				}
			}
		}

		return null;
	}
	
	
	public static String getString(Map<String, String> properties, String def, String... keys) {
		String value = getProperty(properties, keys);
		return ( value == null ) ? def : value;
	}
	
	public static long getLong(Map<String, String> properties, long def, String... keys) {
		String value = getProperty(properties, keys);
		if ( value != null ) {
			try {
				return Long.parseLong(value);
			} catch (NumberFormatException nfe) {
				
			}
		}
		
		return def;
	}
}