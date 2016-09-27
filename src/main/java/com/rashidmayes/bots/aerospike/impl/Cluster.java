package com.rashidmayes.bots.aerospike.impl;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.cluster.Node;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;

public class Cluster extends CommandHandler {
	
	private ObjectMapper mObjectMapper = new ObjectMapper();
	private ObjectWriter mObjectWriter = mObjectMapper.writerWithDefaultPrettyPrinter();
	
	@Override
	public void execute(SlackMessagePosted event) {
		
		try {			
			AerospikeClient client = mAgent.getClient();
			StringBuffer buffer = new StringBuffer();
			
			for (Node node : client.getNodes() ) {
				buffer.append("*Node ")
				.append(node.getName())
				.append("*\n")
				.append("```").append(mObjectWriter.writeValueAsString(node))
				.append("```\n\n");
			}
			
			this.mAgent.sendMessage(event.getSender(), buffer.toString());
		} catch (Exception e) {
			mLogger.severe(e.getMessage());
			this.mAgent.sendMessage(event.getSender(), e.getMessage());
		}
	}
}
