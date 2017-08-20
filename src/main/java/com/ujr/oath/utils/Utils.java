package com.ujr.oath.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Utils {
	
	public static ExecutorService createExecutorService() {
		ExecutorService executorService = Executors.newFixedThreadPool(1, r -> {
			Thread t = new Thread(r);
			t.setName("UJR ");
			t.setDaemon(true);
			return t;
		});
		return executorService;
	}

}
