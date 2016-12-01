package com.rashidmayes.bots.aerospike;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;

public class App 
{
	private static final Logger logger = Logger.getLogger(App.class.getSimpleName());
	
	public static boolean cancel = false;
	
	
    public static void main( String[] args )
    {
        SlackSession session = null;
        try {
        	
        	Configuration configuration;
        	File file;
        	ObjectMapper objectMapper = new ObjectMapper();
        	
        	if ( args.length > 0 ) {
        		file = new File(args[0]);
        	} else {
        		file = new File("aeroslack.json");
        	}
        	
    		if ( file.canRead() ) {
    			configuration = objectMapper.readValue(file, Configuration.class);
    		} else {
    			logger.severe(String.format("File %s is unreadable. Exiting", file.getPath()));
    			return;
    		}
			
			String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(configuration);
			logger.info(json);
			
			while (!cancel) {
				if ( session == null || !session.isConnected() ) {
					try {
						logger.info("connecting...");
						session = SlackSessionFactory.createWebSocketSlackSession(configuration.slackSessionId);
						session.connect();
						
						SlackAgent slackAgent = new AerospikeSlackAgent(configuration, session);
				        session.addMessagePostedListener(slackAgent);
					} catch (Exception e) {
						logger.severe(e.getMessage());
					}
				}
				
				Thread.sleep(configuration.slackSessionRetryInterval);
			}
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
        finally {
        	try {
				if ( session != null )
					session.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
}
