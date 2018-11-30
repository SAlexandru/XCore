package ro.lrg.xcore.metametamodel;

public interface IPropertyComputer <ReturnType, Entity extends XEntity> {
	ReturnType compute(Entity entity);
}
