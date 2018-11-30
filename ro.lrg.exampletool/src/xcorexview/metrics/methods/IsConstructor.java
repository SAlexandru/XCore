package xcorexview.metrics.methods;

import org.eclipse.jdt.core.JavaModelException;

import ro.lrg.xcore.metametamodel.*;

import exampletool.metamodel.entity.XMethod;

@PropertyComputer
public class IsConstructor implements IPropertyComputer <Boolean, XMethod>{

	@Override
	public Boolean compute(XMethod entity) {
		try {
			return entity.getUnderlyingObject().isConstructor();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}
