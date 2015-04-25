package xcorexview.metrics.packages;

import xmetamodel.XPackage;

import com.salexandru.corex.interfaces.IPropertyComputer;
import com.salexandru.corex.metaAnnotation.PropertyComputer;

@PropertyComputer
public class Name implements IPropertyComputer<String, XPackage> {

	@Override
	public String compute(XPackage entity) {
		return entity.getUnderlyingObject().getElementName();
	}
}
