package com.salexandru.xcore.utils.interfaces;

public class UnderlyingPair <XSomething extends XEntity,  OriginalUnderlyingType, NewUnderlyingType> {
	NewUnderlyingType  otherUnderlyingType;
	OriginalUnderlyingType originalUnderlyingType;
	
	ITransform<OriginalUnderlyingType, NewUnderlyingType> transformer;
	
	public UnderlyingPair (NewUnderlyingType  otherUnderlyingType_, OriginalUnderlyingType originalUnderlyingType_, ITransform<OriginalUnderlyingType, NewUnderlyingType> transformer_) {
		otherUnderlyingType = otherUnderlyingType_;
		originalUnderlyingType = originalUnderlyingType_;
		transformer = transformer_;
	}
}
