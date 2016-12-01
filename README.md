# AeroSlack

AeroSlack is a example of a Slack bot that uses the [Aerospike Java client](http://www.aerospike.com/docs/client/java/) to interact with [Aerospike](http://www.aerospike.com) database. View the handlers section of the configuration file for a complete list of supported functionality.

AeroSlack uses the [Simple Slack API](https://github.com/Ullink/simple-slack-api).

##Screenshots

!(/screenshots/screenshot1.png)

##Configuration

The bot reads the configuration details from a JSON file, specified as the first argument. For example, if your the config file is `/home/myuser/aeroslack.json`, lanuching the bot would look something like `java -jar aeroslack.jar /home/myuser/aeroslack.json`.

Same configuration file:

```json
{
  "slackSessionId" : "<my bot/sesion id>",
  "host" : "<my aerospike host>",
  "port" : <my aerospike port>,
  "name" : "AeroSlack",
  "slackSessionRetryInterval" : 10000,
  "slackWatcher" : "<user id of slack user for logging>",
  "handlers" : {
    "cluster" : "com.rashidmayes.bots.aerospike.impl.Cluster",
    "help" : "com.rashidmayes.bots.aerospike.impl.Help",
    "s" : "com.rashidmayes.bots.aerospike.impl.Sets",
    "sets" : "com.rashidmayes.bots.aerospike.impl.Sets",
    "build" : "com.rashidmayes.bots.aerospike.impl.Build",
    "!info" : "com.rashidmayes.bots.aerospike.impl.Info",
    "version" : "com.rashidmayes.bots.aerospike.impl.Version",
    "n" : "com.rashidmayes.bots.aerospike.impl.Namespaces",
    "namespaces" : "com.rashidmayes.bots.aerospike.impl.Namespaces"
  }
}
```
Before running the bot, specify a *slackSessionId*, *host*, and *port*. If you do not have a *slackSessionId*, you can create one at [https://api.slack.com/bot-users](https://api.slack.com/bot-users). See the section labeled Setting up your bot user.

Optionally, use the *slackWatcher* attribute to delegate a Slack user that will receive debugging messages and general notifications.

##How to Add Commands

To add new command, extend the [com.rashidmayes.bots.aerospike.impl.CommandHandler](https://github.com/rashidmayes/AeroSlack/blob/master/src/main/java/com/rashidmayes/bots/aerospike/impl/CommandHandler.java) class and override the `public void execute(SlackMessagePosted event)` method. Next, update the handlers section of the configuration file to include a mapping for the newly created handler. 

Example:

```java
public class Help extends MyCommandHandler {
	
	@Override
	public void execute(SlackMessagePosted event) {
		
		try {		
      AerospikeClient client = mAgent.getClient();
			StringBuffer buffer = new StringBuffer("```").append("hello world").append("```\n");
			
			this.mAgent.sendMessage(event.getSender(), buffer.toString());
		} catch (Exception e) {
			mLogger.severe(e.getMessage());
			this.mAgent.sendMessage(event.getSender(), e.getMessage());
		}
	}
	
	@Override
	public String getDescription() {
		return "Does something cool";
	}
}
```
Visit (https://get.slack.help/hc/en-us/articles/202288908-Format-your-messages) and (https://api.slack.com/docs/messages/builder) for tips on message formatting.
