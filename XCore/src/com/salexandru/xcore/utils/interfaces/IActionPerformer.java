package com.salexandru.xcore.utils.interfaces;

public interface IActionPerformer <ReturnType, Entity extends XEntity, ArgTypeList extends IHList> {

	ReturnType performAction(Entity entity, ArgTypeList args);

}
