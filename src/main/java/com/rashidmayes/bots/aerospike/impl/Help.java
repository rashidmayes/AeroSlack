package com.rashidmayes.bots.aerospike.impl;

import com.aerospike.client.AerospikeClient;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;

public class Help extends CommandHandler {
	
	@Override
	public void execute(SlackMessagePosted event) {
		
		try {			
			AerospikeClient client = mAgent.getClient();
			StringBuffer buffer = new StringBuffer();
			
			com.aerospike.client.util.Version version = com.aerospike.client.util.Version.getServerVersion(client, null);
			buffer.append("```").append(version)
				.append("```\n");
			
			this.mAgent.sendMessage(event.getSender(), buffer.toString());
		} catch (Exception e) {
			mLogger.severe(e.getMessage());
			this.mAgent.sendMessage(event.getSender(), e.getMessage());
		}
	}
}
