package com.rashidmayes.bots.aerospike.impl;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Info;
import com.aerospike.client.cluster.Node;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;

public class Version extends CommandHandler {
	
	@Override
	public void execute(SlackMessagePosted event) {
		
		try {			
			AerospikeClient client = mAgent.getClient();
			StringBuffer buffer = new StringBuffer();
			
			String response;
			for (Node node : client.getNodes()) {
				 response = Info.request(null, node, "version");
				buffer.append("```")
				.append(node.getName())
				.append(node.getAddress())
				.append(" ")
				.append(response)
				.append("```\n");
			}

			this.mAgent.sendMessage(event.getSender(), buffer.toString());
		} catch (Exception e) {
			mLogger.severe(e.getMessage());
			this.mAgent.sendMessage(event.getSender(), e.getMessage());
		}
	}
}
