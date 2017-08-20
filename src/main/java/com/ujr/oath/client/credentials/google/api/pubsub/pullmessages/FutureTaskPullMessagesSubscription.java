package com.ujr.oath.client.credentials.google.api.pubsub.pullmessages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ujr.oath.client.credentials.google.api.pubsub.AbstractCallable;
import com.ujr.oath.client.credentials.google.api.pubsub.AbstractFutureTask;
import com.ujr.oath.client.credentials.google.api.pubsub.domain.pull.ResponsePullMessagesSubscription;

public class FutureTaskPullMessagesSubscription extends AbstractFutureTask<ResponsePullMessagesSubscription>  {
	
	static final Logger LOG = LoggerFactory.getLogger(FutureTaskPullMessagesSubscription.class);
	
	public FutureTaskPullMessagesSubscription(AbstractCallable<ResponsePullMessagesSubscription> callable) {
		super(callable);
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		// TODO Auto-generated method stub
		return super.cancel(mayInterruptIfRunning);
	}
	
	



}
