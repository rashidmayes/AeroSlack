package com.rashidmayes.bots.aerospike.impl;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.cluster.Node;
import com.rashidmayes.bots.util.FileUtil;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;

public class Sets extends InfoCommandHandler {
	
	private static final String STAT_FORMAT = "%-40s: %-40s\n";
	private static final String LINE_FORMAT = "%-16s %-16s %16d %16s\n";
	private static final String HEADINGS = String.format("%-16s %-16s %16s %16s\n"
			,"Set", "Namespace", "Objects", "Bytes Memory");
	
	@Override
	public void execute(SlackMessagePosted event) {
		
		try {
			String[] args = event.getMessageContent().split("\\s");
			
			AerospikeClient client = mAgent.getClient();
			StringBuffer buffer = new StringBuffer();
			
			if ( args.length > 1 ) {
				String setFilter = args[1];

				List<NamespaceInfo> namespaces;
				List<SetInfo> sets;
				
				for (Node node : client.getNodes() ) {
					
					namespaces = getNamespaceInfo(node);
					buffer.append("```").append(node.getHost())
					.append("\n\n");
					
					buffer.append(HEADINGS);
							
					for (NamespaceInfo namespace : namespaces) {

						
						sets = getSetInfo(node,namespace.name);
						for (SetInfo setInfo : sets) {
							
							if ( setInfo.name.startsWith(setFilter) ) {
								buffer.append(String.format(
										LINE_FORMAT, 
										setInfo.name
										,namespace.name
										,setInfo.objectCount
										,FileUtil.getSizeString(setInfo.bytesMemory,Locale.US)
										));
								
								buffer.append("\n\n");
								for (Map.Entry<String, String> entry : setInfo.properties.entrySet()) {
									buffer.append(String.format(STAT_FORMAT, entry.getKey(), entry.getValue()));
									if ( buffer.length() + 1024 > 4000  ) {
										buffer.append("```");
										this.mAgent.sendMessage(event.getSender(), buffer.toString());
										buffer.setLength(0);
										buffer.append("```");
									}
								}
							}
						}
						
					}
					
					if (buffer.length() != 0) {
						buffer.append("```");
						this.mAgent.sendMessage(event.getSender(), buffer.toString());
					}
				}
				
			} else {
				List<NamespaceInfo> namespaces;
				List<SetInfo> sets;
				for (Node node : client.getNodes() ) {

					namespaces = getNamespaceInfo(node);
					buffer.append("```").append(node.getHost())
					.append("\n\n");
					
					buffer.append(HEADINGS);
							
					for (NamespaceInfo namespace : namespaces) {

						
						sets = getSetInfo(node,namespace.name);
						for (SetInfo setInfo : sets) {
	
							buffer.append(String.format(
									LINE_FORMAT, 
									setInfo.name
									,namespace.name
									,setInfo.objectCount
									,FileUtil.getSizeString(setInfo.bytesMemory,Locale.US)
									));
						}
						
					}
					buffer.append("```");
				}	
				this.mAgent.sendMessage(event.getSender(), buffer.toString());
			}
			
			
		} catch (Exception e) {
			mLogger.severe(e.getMessage());
			this.mAgent.sendMessage(event.getSender(), e.getMessage());
		}
	}
	
	@Override
	public String getDescription() {
		return "List all set info by node";
	}
}
