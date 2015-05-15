package com.salexandru.xcorex.interfaces;

public interface IPropertyComputer <ReturnType, Entity extends XEntity> {
	ReturnType compute(Entity entity);
}
