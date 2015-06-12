package xcorexview.metrics.Groups;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

import xmetamodel.XClass;
import xmetamodel.XMethod;
import xmetamodel.factory.FactoryMethod;

import com.salexandru.xcorex.interfaces.Group;
import com.salexandru.xcorex.interfaces.IGroupBuilder;
import com.salexandru.xcorex.metaAnnotation.GroupBuilder;


@GroupBuilder
/**
 * 
 * @author SAlexandru
 *
 */
public class ListClassMethods implements IGroupBuilder<XMethod, XClass> {
	@Override
	/**
	 * 
	 * @author SAlexandru222
	 *
	 * @param entity  the XClass used for processing
	 */
	public Group<XMethod> buildGroup(XClass entity) {
		Group<XMethod> group_ = new Group<>();
		try {
			for (final IMethod method: entity.getUnderlyingObject().getMethods()) {
					group_.add(FactoryMethod.createXMethod(method));
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return group_;
	}
}
