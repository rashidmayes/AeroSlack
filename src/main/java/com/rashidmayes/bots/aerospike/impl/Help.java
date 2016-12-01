package com.rashidmayes.bots.aerospike.impl;

import java.util.Map;

import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;

public class Help extends CommandHandler {
	
	@Override
	public void execute(SlackMessagePosted event) {
		
		try {			
			StringBuffer buffer = new StringBuffer("```");
			
			CommandHandler handler;
			Map<String, String> handlers = this.mAgent.getHandlers();
			for ( String name : handlers.keySet() ) {
				try {
					handler = (CommandHandler) Class.forName(handlers.get(name)).newInstance();
					buffer.append(String.format("%s - %s\n", name, handler.getDescription()));
				} catch (Exception e) {
					mLogger.warning(e.getMessage());
				}
			}
			buffer.append("```\n");
			
			this.mAgent.sendMessage(event.getSender(), buffer.toString());
		} catch (Exception e) {
			mLogger.severe(e.getMessage());
			this.mAgent.sendMessage(event.getSender(), e.getMessage());
		}
	}
	
	@Override
	public String getDescription() {
		return "Lists the configured commands";
	}
}
