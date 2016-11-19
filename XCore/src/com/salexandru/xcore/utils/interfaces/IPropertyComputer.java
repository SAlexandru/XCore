package com.salexandru.xcore.utils.interfaces;

public interface IPropertyComputer <ReturnType, Entity extends XEntity> {
	ReturnType compute(Entity entity);
}
