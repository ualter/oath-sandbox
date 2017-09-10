package com.ujr.oauth.client.credentials.google.api.pubsub;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ujr.oauth.client.credentials.google.api.appservice.GoogleAppServiceAccount;
import com.ujr.oauth.client.credentials.google.api.appservice.GoogleAppServiceAccountScorecardUjr;
import com.ujr.oauth.client.credentials.google.api.pubsub.GooglePubSubApiHandler;
import com.ujr.oauth.client.credentials.google.api.pubsub.domain.list.Topic;
import com.ujr.oauth.client.credentials.google.api.pubsub.domain.publish.ListMessages;
import com.ujr.oauth.client.credentials.google.api.pubsub.domain.pull.RequestPullMessagesSubscription;
import com.ujr.oauth.client.credentials.google.api.pubsub.domain.pull.ResponsePullMessagesSubscription;
import com.ujr.oauth.utils.Utils;

public class GooglePubSubApiHandlerTest {
	
	static final Logger LOG = LoggerFactory.getLogger(GooglePubSubApiHandlerTest.class);
	
	private static GoogleAppServiceAccount appServiceAccount;
	private static GooglePubSubApiHandler pubSubApiHandler;
	
	@BeforeClass
	public static void init() {
		appServiceAccount = new GoogleAppServiceAccountScorecardUjr();
		pubSubApiHandler = new GooglePubSubApiHandler(appServiceAccount);
	}
	
	@Test
	public void testListTopicsAndSubscriptions() {
		// List Topics
		List<Topic> listTopics = pubSubApiHandler.listTopics();
		// List Subscriptions
		for(Topic topic : listTopics) {
			pubSubApiHandler.listSubscriptions(topic.getShortName());
		}
	}
	
	@Test
	public void testPublishAndPull() {
		
		// Publish a Message each 5 seconds during 60 seconds
		Utils.createExecutorService("PUBLISHER").execute(new Runnable() {
			@Override
			public void run() {
				int times = 60;
				int index = times; 
				try {
					while (true) {
						Thread.sleep(5000);
						testPublishMessages(1);
						if ( --index == 0 ) {
							break;
						}
					}
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		});
		
		Utils.createExecutorService("CONSUMER -->LISTENING").execute(new Runnable() {
			@Override
			public void run() {
				testPullMessagesForSubscription("ualter");
			}
		});
		
		try {
			Thread.sleep(65000);
		} catch (InterruptedException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
		
	}
	
	@Test
	public void testPublishMessages() {
		testPublishMessages(1);
	}
	private void testPublishMessages(int howMany) {
		ListMessages listMessages = new ListMessages();
		for(int i = howMany; i > 0; i--) {
			listMessages.addMessage("hello world message number " + ((howMany - i) + 1));
		}
		pubSubApiHandler.publishMessageOnTopic("saques",listMessages);
	}
	
	@Test
	public void testPullMessagesForSubscription() {
		testPullMessagesForSubscription("ualter");
	}
	private void testPullMessagesForSubscription(String subscription) {
		RequestPullMessagesSubscription request = new RequestPullMessagesSubscription();
		request.setSubscription(subscription);
		request.setMaxMessages(1000);
		request.setReturnImmediately(false);
		
		FutureTask<ResponsePullMessagesSubscription> futureTaskPullMessagesForSubscription = pubSubApiHandler.pullMessagesForSubscription(request, true);
		ExecutorService executor = Utils.createExecutorService("CONSUMER -->PULLING");
		executor.execute(futureTaskPullMessagesForSubscription);
		try {
			int times = 60;
			int index = times;
			
			// Start Listening  
			while ( true ) {
				Thread.sleep(1000);
				LOG.info("#{} Listening for a messages...", (times - index) + 1 );
				
				// Check if were found Messages analyzing if the Pull Task Messages were Done (finalized) 
				if ( futureTaskPullMessagesForSubscription.isDone() ) {
					// If were finalized the Task Pull Messages (messaged were found) show all of them
					ResponsePullMessagesSubscription response = futureTaskPullMessagesForSubscription.get();
					response.getReceivedMessages().forEach(m -> LOG.info(m.getMessage().getMessageId() + " - " + m.getMessage().getDataDecoded()));
					
					// Get Back a Task Pull Messages listening during the rest of time for new Messages
					futureTaskPullMessagesForSubscription = pubSubApiHandler.pullMessagesForSubscription(request, true);
					executor.execute(futureTaskPullMessagesForSubscription);
				}
				
				if ( --index == 0 ) {
					futureTaskPullMessagesForSubscription.cancel(true);
					break;
				}
			}
			
			LOG.info("Listening for Pulling Messages, done!");
			executor.shutdownNow();
			
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

}
