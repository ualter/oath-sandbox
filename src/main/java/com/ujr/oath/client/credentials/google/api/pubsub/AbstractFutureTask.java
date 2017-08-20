package com.ujr.oath.client.credentials.google.api.pubsub;

import java.util.concurrent.FutureTask;

public abstract class AbstractFutureTask<T> extends FutureTask<T> {

	protected AbstractCallable<T> callable;
	
	public AbstractFutureTask(AbstractCallable<T> callable) {
		super(callable);
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		this.callable.cancel = true;
		return super.cancel(mayInterruptIfRunning);
	}
	
	
	

}
