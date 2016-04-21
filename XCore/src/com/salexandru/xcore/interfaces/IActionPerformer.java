package com.salexandru.xcore.interfaces;

public interface IActionPerformer <ReturnType, Entity extends XEntity, VariadicTypeList extends ITList> {

	ReturnType performAction(Entity entity, VariadicTypeList args);

}
