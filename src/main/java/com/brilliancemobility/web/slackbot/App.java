package com.brilliancemobility.web.slackbot;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;

/**
https://github.com/Ullink/simple-slack-api/blob/master/samples/events/src/main/java/events/ListeningToMessageEvents.java
 */
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
    			//return;
    		}
    		
			
			configuration = new Configuration();
			configuration.slackSessionId = "";
			configuration.host = "127.0.0.1";
			configuration.port = 2012;
			configuration.slackWatcher = "rashid";
			configuration.handlers = new HashMap<String, String>();
			configuration.handlers.put("id","com.brilliancemobility.web.slackbot.ID");
			
			String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(configuration);
			System.out.println(json);
			configuration = objectMapper.readValue(json, Configuration.class);
			System.out.println(configuration.handlers.get("id"));
		
			
			while (!cancel) {
				if ( session == null || !session.isConnected() ) {
					session = SlackSessionFactory.createWebSocketSlackSession(configuration.slackSessionId);
					session.connect();
					
					SlackAgent slackAgent = new AerospikeSlackAgent(configuration, session);
			        session.addMessagePostedListener(slackAgent);
				}
				
				Thread.sleep(10 * 1000);
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
