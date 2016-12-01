package com.rashidmayes.bots.aerospike.impl;

import java.util.logging.Logger;

import com.rashidmayes.bots.aerospike.AerospikeSlackAgent;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;

public abstract class CommandHandler {

	AerospikeSlackAgent mAgent;
	Logger mLogger = Logger.getLogger(getClass().getSimpleName());
	
	public CommandHandler() {

	}

	public void setSlackAgent(AerospikeSlackAgent agent) {
		this.mAgent = agent;
	}
	
	public abstract void execute(SlackMessagePosted event);
	public abstract String getDescription();
}
