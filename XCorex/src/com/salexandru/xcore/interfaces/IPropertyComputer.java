package com.salexandru.xcore.interfaces;

public interface IPropertyComputer <ReturnType, Entity extends XEntity> {
	ReturnType compute(Entity entity);
}
