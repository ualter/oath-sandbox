package com.ujr.oauth.client.credentials.google.api.pubsub.tasks;

import java.util.concurrent.FutureTask;

public abstract class AbstractFutureTask<T> extends FutureTask<T> {

	protected AbstractCallable<T> abstractCallable;
	
	public AbstractFutureTask(AbstractCallable<T> callable) {
		super(callable);
		this.abstractCallable = callable;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		this.abstractCallable.cancel = true;
		return super.cancel(mayInterruptIfRunning);
	}
	
	
	

}
