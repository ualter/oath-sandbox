package com.ujr.oath.client.credentials.google.api.pubsub;

import java.util.concurrent.FutureTask;

public abstract class AbstractFutureTask<T> extends FutureTask<T> {

	
	public AbstractFutureTask(AbstractCallable<T> callable) {
		super(callable);
	}
	
	

}
