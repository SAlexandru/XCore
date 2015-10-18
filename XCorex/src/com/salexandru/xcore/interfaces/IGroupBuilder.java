package com.salexandru.xcore.interfaces;


public interface IGroupBuilder <ElementType extends XEntity, Entity extends XEntity> {
	Group<ElementType> buildGroup(Entity entity);
}
