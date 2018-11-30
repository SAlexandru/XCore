package xcorexview.metrics.packages;

import ro.lrg.xcore.metametamodel.*;

import exampletool.metamodel.entity.XPackage;

/**
 * 
 * @author SAlexandru
 *
 */
@PropertyComputer
public class ToString implements IPropertyComputer<String, XPackage> {
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
