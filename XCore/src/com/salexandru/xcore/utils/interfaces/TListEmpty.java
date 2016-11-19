package com.salexandru.xcore.utils.interfaces;

public final class TListEmpty implements IHList {
	
	private static final class TListEmptyInstace {
		public static final TListEmpty instance = new TListEmpty(); 
	}
	
	private TListEmpty() {}
	
	public static TListEmpty getInstance() {
		return TListEmptyInstace.instance;
	}
	
}
