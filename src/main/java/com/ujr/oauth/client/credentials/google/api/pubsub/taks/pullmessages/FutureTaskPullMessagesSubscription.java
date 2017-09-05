package com.ujr.oauth.client.credentials.google.api.pubsub.taks.pullmessages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ujr.oauth.client.credentials.google.api.pubsub.domain.pull.ResponsePullMessagesSubscription;
import com.ujr.oauth.client.credentials.google.api.pubsub.tasks.AbstractCallable;
import com.ujr.oauth.client.credentials.google.api.pubsub.tasks.AbstractFutureTask;

public class FutureTaskPullMessagesSubscription extends AbstractFutureTask<ResponsePullMessagesSubscription>  {
	
	static final Logger LOG = LoggerFactory.getLogger(FutureTaskPullMessagesSubscription.class);
	
	public FutureTaskPullMessagesSubscription(AbstractCallable<ResponsePullMessagesSubscription> callable) {
		super(callable);
	}


}
