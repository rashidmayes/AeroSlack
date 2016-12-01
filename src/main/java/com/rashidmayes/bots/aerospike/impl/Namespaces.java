package com.rashidmayes.bots.aerospike.impl;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.cluster.Node;
import com.rashidmayes.bots.util.FileUtil;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;

public class Namespaces extends InfoCommandHandler {
	
	private static final String STAT_FORMAT = "%-40s: %40s\n";
	private static final String LINE_FORMAT = "%-10s %10s %2d %16d %16d %16d %16d %8s %8s %8d%% %8d%%\n";
	private static final String HEADINGS = String.format("%-10s %10s %2s %16s %16s %16s %16s %8s %8s %9s %9s\n"
			,"Name", "Type", "RF", "Master", "Prole", "Used Memory", "Used Disk", "Memory", "Disk", "Free Mem", "Free Disk");
	
	@Override
	public void execute(SlackMessagePosted event) {
		
		try {
			String[] args = event.getMessageContent().split("\\s");

			AerospikeClient client = mAgent.getClient();
			StringBuffer buffer = new StringBuffer();
			
			if ( args.length > 1 ) {
				String namespace = args[1];

				NamespaceInfo namespaceInfo;
				for (Node node : client.getNodes() ) {

					namespaceInfo = getNamespaceInfo(node, namespace);
					buffer.append("```").append(node.getHost())
					.append("\n\n");
					
					buffer.append(HEADINGS);
					buffer.append(String.format(
							LINE_FORMAT, 
							namespaceInfo.name
							,namespaceInfo.getType()
							,namespaceInfo.getReplicationFactor()
							
							,namespaceInfo.getMasterObjects()
							,namespaceInfo.getProleObjects()
							
							,namespaceInfo.getUsedBytesMemory()
							,namespaceInfo.getUsedBytesDisk()
							
							,FileUtil.getSizeString(namespaceInfo.getTotalBytesMemory(), Locale.US)
							,FileUtil.getSizeString(namespaceInfo.getTotalBytesDisk(), Locale.US)

							,namespaceInfo.getFreeMemoryPercent()
							,namespaceInfo.getFreeDiskPercent()
							));
					
					buffer.append("\n\n");
					for (Map.Entry<String, String> entry : namespaceInfo.properties.entrySet()) {
						buffer.append(String.format(STAT_FORMAT, entry.getKey(), entry.getValue()));
						if ( buffer.length() + 1024 > 4000  ) {
							buffer.append("```");
							this.mAgent.sendMessage(event.getSender(), buffer.toString());
							buffer.setLength(0);
							buffer.append("```");
						}
					}
					
					if (buffer.length() != 0) {
						buffer.append("```");
						this.mAgent.sendMessage(event.getSender(), buffer.toString());
					}
					
				}
				
			} else {
				List<NamespaceInfo> namespaces;
				for (Node node : client.getNodes() ) {

					namespaces = getNamespaceInfo(node);
					buffer.append("```").append(node.getHost())
					.append("\n\n");
					
					buffer.append(HEADINGS);
							
					for (NamespaceInfo namespace : namespaces) {
						buffer.append(String.format(
								LINE_FORMAT, 
								namespace.name
								,namespace.getType()
								,namespace.getReplicationFactor()
								
								,namespace.getMasterObjects()
								,namespace.getProleObjects()
								
								,namespace.getUsedBytesMemory()
								,namespace.getUsedBytesDisk()
								
								,FileUtil.getSizeString(namespace.getTotalBytesMemory(), Locale.US)
								,FileUtil.getSizeString(namespace.getTotalBytesDisk(), Locale.US)

								,namespace.getFreeMemoryPercent()
								,namespace.getFreeDiskPercent()
								));
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
		return "List all namespaces by node";
	}
}
