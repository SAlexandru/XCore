package com.salexandru.xcore.interfaces;

public interface IActionPerformer <ReturnType, Entity extends XEntity, ArgTypeList extends IHList> {

	ReturnType performAction(Entity entity, ArgTypeList args);

}
