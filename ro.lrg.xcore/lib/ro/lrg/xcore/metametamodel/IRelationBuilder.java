package ro.lrg.xcore.metametamodel;

public interface IRelationBuilder <ElementType extends XEntity, Entity extends XEntity> {
	Group<ElementType> buildGroup(Entity entity);
}
