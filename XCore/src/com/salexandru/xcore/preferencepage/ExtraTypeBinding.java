package com.salexandru.xcore.preferencepage;

public class ExtraTypeBinding implements IBinding {
	private final String type_;
	private final String transformer_;
	
	public ExtraTypeBinding(String type, String transformer) {
		if (null == type || null == transformer || type.trim().isEmpty() || transformer.trim().isEmpty()) {
			throw new IllegalArgumentException(String.format("Binding %s %s --- one or both is null / empty ", type, transformer));
		}
		
		type_ = type;
		transformer_ = transformer;
	}
	
	public String getType() { return type_; }
	public String getTransformer() { return transformer_; }
	
	@Override
	public String toString() { return String.format("%s:%s", type_, transformer_); }

}
