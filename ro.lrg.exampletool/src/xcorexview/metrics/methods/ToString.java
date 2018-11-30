package xcorexview.metrics.methods;

import ro.lrg.xcore.metametamodel.*;

import exampletool.metamodel.entity.XMethod;

@PropertyComputer
public class ToString implements IPropertyComputer<String, XMethod> {

	@Override
	public String compute(XMethod entity) {
		return entity.getUnderlyingObject().getElementName();
	}
}
