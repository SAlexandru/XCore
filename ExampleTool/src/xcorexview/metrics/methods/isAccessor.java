package xcorexview.metrics.methods;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.JavaModelException;

import xmetamodel.XMethod;

import com.salexandru.xcore.interfaces.IPropertyComputer;
import com.salexandru.xcore.metaAnnotation.PropertyComputer;


@PropertyComputer
public class isAccessor implements IPropertyComputer <Boolean, XMethod>{

	@Override
	public Boolean compute(XMethod entity) {
		try {
			final String name = entity.toString();
			return Flags.isPublic(entity.getUnderlyingObject().getFlags()) &&
				   (name.startsWith("get")	|| name.startsWith("Get") ||
				    name.startsWith("set") || name.startsWith("Set")) &&
				    entity.cyclomaticComplexity() == 1.0 &&
				    entity.numberOfFieldsAccessed() == 1;		   
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return false;
	}
}