package xcorexview.metrics.Groups;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

import com.salexandru.xcore.interfaces.Group;
import com.salexandru.xcore.interfaces.IGroupBuilder;
import com.salexandru.xcore.metaAnnotation.GroupBuilder;

import exampletool.metamodel.entity.XClass;
import exampletool.metamodel.entity.XMethod;
import exampletool.metamodel.factory.Factory;

/**
 * 
 * @author SAlexandru222
 *
 * @param entity  the XClass used for processing
 */
@GroupBuilder
public class ListClassMethods implements IGroupBuilder<XMethod, XClass> {
	@Override
	public Group<XMethod> buildGroup(XClass entity) {
		Group<XMethod> group_ = new Group<>();
		try {
			for (final IMethod method: entity.getUnderlyingObject().getMethods()) {
					group_.add(Factory.getInstance().createXMethod(method));
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return group_;
	}
}
