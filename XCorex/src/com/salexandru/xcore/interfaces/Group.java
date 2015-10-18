package com.salexandru.xcore.interfaces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Group <T extends XEntity> {
	private List<T> elements_;
	
	public Group() {
		elements_ = new ArrayList<>();
	}
	
	public boolean add(T element) {
		return elements_.add(element);
	}
	
	public boolean addAll(Collection<? extends T> elements) {
		return elements_.addAll(elements);
	}

	public List<T> getElements() {return elements_;}
}
