package xcorexview.metrics.Groups;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.Modifier;

import com.salexandru.xcore.utils.interfaces.Group;
import com.salexandru.xcore.utils.interfaces.IRelationBuilder;
import com.salexandru.xcore.utils.metaAnnotation.RelationBuilder;

import exampletool.metamodel.entity.XClass;
import exampletool.metamodel.entity.XMethod;
import exampletool.metamodel.factory.Factory;

/**
 * 
 * @author SAlexandru222
 *
 * @param entity  the XClass used for processing
 */
@RelationBuilder
public class ListClassMethods implements IRelationBuilder<XMethod, XClass> {
	@Override
	public Group<XMethod> buildGroup(XClass entity) {
		Group<XMethod> group_ = new Group<>();
		try {
			for (final IMethod method: entity.getUnderlyingObject().getMethods()) {
			   if (Modifier.isStatic(method.getFlags()))
					group_.add(Factory.getInstance().createXMethod(method));
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return group_;
	}
}
