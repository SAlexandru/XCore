package xcorexview.metrics.classes;

import ro.lrg.xcore.metametamodel.*;

import exampletool.metamodel.entity.XClass;

@PropertyComputer
public class ToString implements IPropertyComputer<String, XClass> {
	@Override
	public String compute(XClass entity) {
		return entity.getUnderlyingObject().getElementName();
	}
}
