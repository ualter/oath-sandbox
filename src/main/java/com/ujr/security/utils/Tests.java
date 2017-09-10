package com.ujr.security.utils;

import java.util.ArrayList;
import java.util.List;

public class Tests {
	
	public static void main(String[] args) {
		
		
		List<String> x = new ArrayList<String>();
		int desiredIndex = 0;
		if ( x.size() > desiredIndex && x.get(desiredIndex) != null ) {
			System.out.println("Ok! " + x.get(desiredIndex));
		} else {
			System.out.println("Ops...  not posible.");
		}
		System.exit(0);
		
		
	}

}
