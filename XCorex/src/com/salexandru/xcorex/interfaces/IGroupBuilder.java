package com.salexandru.xcorex.interfaces;


public interface IGroupBuilder <ElementType extends XEntity, Entity extends XEntity> {
	void buildGroup(Entity entity);
	Group<ElementType> getGroup();
}
