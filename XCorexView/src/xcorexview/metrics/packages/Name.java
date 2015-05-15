package xcorexview.metrics.packages;

import xmetamodel.XPackage;

import com.salexandru.xcorex.interfaces.IPropertyComputer;
import com.salexandru.xcorex.metaAnnotation.PropertyComputer;

@PropertyComputer
public class Name implements IPropertyComputer<String, XPackage> {

	@Override
	public String compute(XPackage entity) {
		return entity.getUnderlyingObject().getElementName();
	}
}
