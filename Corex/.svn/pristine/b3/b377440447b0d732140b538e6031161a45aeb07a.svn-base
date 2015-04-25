package com.salexandru.corex.interfaces;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Group <T> implements Iterable<T> {
	private List<T> elements_;
	
	public Group(List<T> elements) {
		elements_ = new ArrayList<>(elements);
	}

	@Override
	public Iterator<T> iterator() {
		return elements_.iterator();
	}
}
