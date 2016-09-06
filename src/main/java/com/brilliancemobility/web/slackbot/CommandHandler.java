package com.brilliancemobility.web.slackbot;

import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;

public abstract class CommandHandler {

	AerospikeSlackAgent mAgent;
	
	public CommandHandler() {

	}

	public void setSlackAgent(AerospikeSlackAgent agent) {
		this.mAgent = agent;
	}
	
	public abstract void execute(SlackMessagePosted event);
	
}
