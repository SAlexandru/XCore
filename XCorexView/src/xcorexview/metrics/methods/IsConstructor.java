package xcorexview.metrics.methods;

import org.eclipse.jdt.core.JavaModelException;

import xmetamodel.XMethod;

import com.salexandru.xcorex.interfaces.IPropertyComputer;
import com.salexandru.xcorex.metaAnnotation.PropertyComputer;

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
