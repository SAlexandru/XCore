package com.salexandru.xcore.utils.interfaces;


public interface IRelationBuilder <ElementType extends XEntity, Entity extends XEntity> {
	RelationBuilder<ElementType> buildGroup(Entity entity);
}
