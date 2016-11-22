package xcorexview.metrics.methods;

import org.eclipse.jdt.core.JavaModelException;

import com.salexandru.xcore.utils.interfaces.IPropertyComputer;
import com.salexandru.xcore.utils.metaAnnotation.PropertyComputer;

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
