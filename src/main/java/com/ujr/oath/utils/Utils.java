package com.ujr.oath.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Utils {
	
	public static ExecutorService createExecutorService(String threadName) {
		ExecutorService executorService = Executors.newFixedThreadPool(1, r -> {
			Thread t = new Thread(r);
			t.setName(threadName);
			t.setDaemon(true);
			return t;
		});
		return executorService;
	}
	
	public static ExecutorService createExecutorService() {
		return createExecutorService("UJR ");
	}

}
