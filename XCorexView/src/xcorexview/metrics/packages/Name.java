package xcorexview.metrics.packages;

import xmetamodel.XPackage;

import com.salexandru.xcorex.interfaces.IPropertyComputer;
import com.salexandru.xcorex.metaAnnotation.PropertyComputer;

/**
 * 
 * @author SAlexandru
 *
 */
@PropertyComputer
public class Name implements IPropertyComputer<String, XPackage> {
	/**
	 * 
	 * @author SAlexandru1
	 *
	 */
	@Override
	public String compute(XPackage entity) {
		return entity.getUnderlyingObject().getElementName();
	}
}
