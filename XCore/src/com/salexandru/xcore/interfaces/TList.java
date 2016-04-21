package com.salexandru.xcore.interfaces;

public final class TList<HeadType,TailListType extends ITList> implements ITList {

	private final HeadType head;
	private final TailListType tail;
	
	public TList(HeadType h, TailListType t) {
		head = h;
		tail = t;
	}
	
	public HeadType head() {
		return head;
	}

	public TailListType tail() {
		return tail;
	}

}
