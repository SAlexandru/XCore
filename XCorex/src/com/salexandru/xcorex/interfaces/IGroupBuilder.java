package com.salexandru.xcorex.interfaces;


public interface IGroupBuilder <ElementType extends XEntity, Entity extends XEntity> {
	Group<ElementType> buildGroup(Entity entity);
}
