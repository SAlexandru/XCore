package com.salexandru.xcore.interfaces;

public final class TListEmpty implements IHList {
	
	private static TListEmpty instance = null; 
	
	private TListEmpty() {}
	
	public static TListEmpty getInstance() {
		if(instance == null) {
			instance = new TListEmpty(); 
		}
		return instance;
	}
	
}
