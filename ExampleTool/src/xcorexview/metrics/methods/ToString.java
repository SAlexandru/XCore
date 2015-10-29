package xcorexview.metrics.methods;

import com.salexandru.xcore.interfaces.IPropertyComputer;
import com.salexandru.xcore.metaAnnotation.PropertyComputer;

import exampletool.metamodel.entity.XMethod;

@PropertyComputer
public class ToString implements IPropertyComputer<String, XMethod> {

	@Override
	public String compute(XMethod entity) {
		return entity.getUnderlyingObject().getElementName();
	}
}
