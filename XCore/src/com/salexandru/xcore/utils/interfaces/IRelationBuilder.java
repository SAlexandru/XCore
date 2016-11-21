package com.salexandru.xcore.utils.interfaces;

public interface IRelationBuilder <ElementType extends XEntity, Entity extends XEntity> {
	Group<ElementType> buildGroup(Entity entity);
}
