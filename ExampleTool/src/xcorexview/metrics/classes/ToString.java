package xcorexview.metrics.classes;

import xmetamodel.XClass;

import com.salexandru.xcore.interfaces.IPropertyComputer;
import com.salexandru.xcore.metaAnnotation.PropertyComputer;

@PropertyComputer
public class ToString implements IPropertyComputer<String, XClass> {
	@Override
	public String compute(XClass entity) {
		return entity.getUnderlyingObject().getElementName();
	}
}
