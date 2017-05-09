package com.salexandru.xcore.utils.interfaces;

public interface IReversableTransform <OriginalUnderlyingType, NewUnderlyingType> {
	NewUnderlyingType transform();
	OriginalUnderlyingType reverse();
}
