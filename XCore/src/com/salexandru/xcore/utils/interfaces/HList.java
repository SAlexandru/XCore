package com.salexandru.xcore.utils.interfaces;

public final class HList<HeadType,TailType extends IHList> implements IHList {

	private final HeadType head;
	private final TailType tail;
	
	public HList(HeadType h, TailType t) {
		head = h;
		tail = t;
	}
	
	public HeadType head() {
		return head;
	}

	public TailType tail() {
		return tail;
	}

}
