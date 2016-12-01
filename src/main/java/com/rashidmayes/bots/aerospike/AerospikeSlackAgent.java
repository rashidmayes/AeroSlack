package com.rashidmayes.bots.aerospike;

import com.aerospike.client.AerospikeClient;
import com.rashidmayes.bots.aerospike.impl.CommandHandler;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;

public class AerospikeSlackAgent extends SlackAgent {

	private AerospikeClient mClient = null;
	
	public AerospikeSlackAgent(Configuration configuration, SlackSession session) {
		
		super(configuration, session);
	
	}

	@Override
	public void handleOnEvent(SlackMessagePosted event, SlackSession session) {
        String messageContent = event.getMessageContent();
        SlackUser messageSender = event.getSender();
        
        CommandHandler handler = getCommandHandler(messageSender, messageContent);
        if ( handler == null ) { 
        	mSession.sendMessageToUser(messageSender, String.format("No configred handler for '%s'",messageContent), null);   
        } else {
        	handler.setSlackAgent(this);
        	handler.execute(event);	
        }
	}
	
	private CommandHandler getCommandHandler(SlackUser messageSender, String commandLine) {
		CommandHandler handler = null;
    	int index = commandLine.indexOf(' ');
    	String command = ( index == -1 ) ? commandLine : commandLine.substring(0, index);
    	mLogger.info(command + " from " + commandLine);
		
        try {
        	String handlerClass = mConfiguration.handlers.get(command);
        	if ( handlerClass == null ) {
        		mLogger.info(String.format("Parsed command %s, but not handler class.",  command));
        	} else {
        		mLogger.info(command + " -> " + handlerClass);
        		handler = (CommandHandler)(Class.forName(handlerClass).newInstance());
        	}
        } catch (Exception e) {
        	mLogger.warning(e.toString());        	
        }
        
        return handler;
	}
	
	protected SlackSession getSession() {
		return this.mSession;
	}
	
	public AerospikeClient getClient() {
		if ( mClient == null || !mClient.isConnected() ) {
			
			mClient = new AerospikeClient(mConfiguration.host, mConfiguration.port);
			mClient.writePolicyDefault.timeout = 4000;
			mClient.readPolicyDefault.timeout = 4000;
			mClient.queryPolicyDefault.timeout = 4000;
			
			mLogger.info(this + " Connected" + mClient.toString());
			//informWatcher(mClient.toString());
		}
		
		return mClient;
	}
}
