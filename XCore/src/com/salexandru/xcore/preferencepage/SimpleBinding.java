package com.salexandru.xcore.preferencepage;

public class SimpleBinding implements IBinding {
	private final String type_;
	
	public SimpleBinding(String type) {
		if (null == type || type.trim().isEmpty()) {
			throw new IllegalArgumentException(String.format("Binding %s %s --- one or both is null / empty ", type));
		}
		
		type_ = type;
	}
	
	public String getType() { return type_; }
	
	@Override
	public String toString() { return type_; }

}
