package ro.lrg.xcore.metametamodel;

public final class HListEmpty implements IHList {
	
	private static final class HListEmptyInstace {
		public static final HListEmpty instance = new HListEmpty(); 
	}
	
	private HListEmpty() {}
	
	public static HListEmpty getInstance() {
		return HListEmptyInstace.instance;
	}
	
}
