package xcorexview.metrics.methods;

import xmetamodel.XMethod;

import com.salexandru.corex.interfaces.IPropertyComputer;
import com.salexandru.corex.metaAnnotation.PropertyComputer;

@PropertyComputer
public class Name implements IPropertyComputer<String, XMethod> {

	@Override
	public String compute(XMethod entity) {
		return entity.getUnderlyingObject().getElementName();
	}
}
