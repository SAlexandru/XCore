package ro.lrg.xcore.metametamodel;

public interface IActionPerformer <ReturnType, Entity extends XEntity, ArgTypeList extends IHList> {

	ReturnType performAction(Entity entity, ArgTypeList args);

}
