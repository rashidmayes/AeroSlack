package com.rashidmayes.bots.aerospike;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.threeten.bp.LocalDate;

import com.ullink.slack.simpleslackapi.ChannelHistoryModule;
import com.ullink.slack.simpleslackapi.SlackAttachment;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackMessageHandle;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;
import com.ullink.slack.simpleslackapi.impl.ChannelHistoryModuleFactory;
import com.ullink.slack.simpleslackapi.listeners.SlackMessagePostedListener;
import com.ullink.slack.simpleslackapi.replies.SlackChannelReply;

public abstract class SlackAgent implements SlackMessagePostedListener {
	
	Logger mLogger = Logger.getLogger(getClass().getName());
	Configuration mConfiguration;
	SlackSession mSession;
	SlackUser watcher;
	String name;
	
	public SlackAgent(Configuration configuration, SlackSession session) {
		this.mConfiguration = configuration;
		this.mSession = session;
		this.name = String.format("%s(%s)", configuration.name, Integer.toHexString(this.hashCode()));
		
		
		if ( configuration.slackWatcher != null ) {
			watcher = session.findUserByUserName(configuration.slackWatcher);
		}
		
		informWatcher("I'm alive.");
	}
	
	
    public final void onEvent(SlackMessagePosted event, SlackSession session) {
        if (session.sessionPersona().getId().equals(event.getSender().getId())) {
        	handleOnSelfEvent(event, session);
        } else {
        	mLogger.info(event.toString());
        	handleOnEvent(event, session);
        }
    }
    
    public void informWatcher(String message, String... args) {
    	
    	if ( watcher != null ) {
        	String text = String.format(message, (Object[]) args);
        	text = String.format("%s: %s", name, text);
        	mSession.sendMessageToUser(watcher, text, null);    		
    	}
    }
    

    public List<SlackMessagePosted>  fetchSomeMessagesFromChannelHistory(SlackChannel slackChannel) {

        ChannelHistoryModule channelHistoryModule = ChannelHistoryModuleFactory.createChannelHistoryModule(mSession);
        List<SlackMessagePosted> messages = channelHistoryModule.fetchHistoryOfChannel(slackChannel.getId());
        
        return messages;
    }


    public List<SlackMessagePosted> fetchTenLastMessagesFromChannelHistory(SlackChannel slackChannel, int limit) {

        ChannelHistoryModule channelHistoryModule = ChannelHistoryModuleFactory.createChannelHistoryModule(mSession);
        List<SlackMessagePosted> messages = channelHistoryModule.fetchHistoryOfChannel(slackChannel.getId(),limit);
        
        return messages;
    }

    public List<SlackMessagePosted> getMessages(SlackChannel slackChannel, LocalDate date, int limit) {

        ChannelHistoryModule channelHistoryModule = ChannelHistoryModuleFactory.createChannelHistoryModule(mSession);
        List<SlackMessagePosted> messages = channelHistoryModule.fetchHistoryOfChannel(slackChannel.getId(),date,limit);
        
        return messages;
    }	

    public void sendChannelMessage(String slackChannel, String message) {
        SlackChannel channel = mSession.findChannelByName(slackChannel);
        mSession.sendMessage(channel, message);
    }

    public void sendMessage(String username, String message) {

        SlackUser user = mSession.findUserByUserName(username);
        sendMessage(user, message, null);
    }
    
    public void sendMessage(SlackUser user, String message) {
    	sendMessage(user, message, null);
    }
    
    
    public void sendMessage(SlackUser user, String message, SlackAttachment attachment) {
        mSession.sendMessageToUser(user, message, attachment);
    }

    public void sendDirectMessage(String message, String... username) {
    	ArrayList<SlackUser> users = new ArrayList<SlackUser>(username.length);
    	for ( String user : username ) {
    		users.add(mSession.findUserByUserName(user));
    	}

        SlackMessageHandle<SlackChannelReply> reply = 
        		mSession.openMultipartyDirectMessageChannel(users.toArray(new SlackUser[0]));
        SlackChannel channel = reply.getReply().getSlackChannel();
        mSession.sendMessage(channel, message, null);
    }
    
    
    public abstract void handleOnEvent(SlackMessagePosted event, SlackSession session);
    
    public void handleOnSelfEvent(SlackMessagePosted event, SlackSession session) {
    	//ignore my events by default
    }
}
