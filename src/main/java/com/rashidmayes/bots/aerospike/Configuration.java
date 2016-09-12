package com.rashidmayes.bots.aerospike;

import java.util.Map;

public class Configuration {

	public String slackSessionId;
	public String host;
	public int port;
	public String username;
	public String password;
	
	public String name = "AeroSlack";
	public int slackSessionRetryInterval = 10*1000;
	
	public String slackWatcher;
	
	public Map<String, String> handlers;
}
