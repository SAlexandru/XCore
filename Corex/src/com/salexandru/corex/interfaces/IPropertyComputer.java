package com.salexandru.corex.interfaces;

public interface IPropertyComputer <ReturnType, Entity extends XEntity> {
	ReturnType compute(Entity entity);
}
