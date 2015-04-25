package com.salexandru.corex.interfaces;


public interface IGroupBuilder <ElementType extends XEntity, Entity extends XEntity> {
	void buildGroup(Entity entity);
	Group<ElementType> getGroup();
}
