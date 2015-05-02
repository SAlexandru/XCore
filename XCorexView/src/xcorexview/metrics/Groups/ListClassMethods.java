package xcorexview.metrics.Groups;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

import xmetamodel.XClass;
import xmetamodel.XMethod;
import xmetamodel.factory.FactoryMethod;

import com.salexandru.corex.interfaces.Group;
import com.salexandru.corex.interfaces.IGroupBuilder;
import com.salexandru.corex.metaAnnotation.GroupBuilder;

@GroupBuilder
public class ListClassMethods implements IGroupBuilder<XMethod, XClass> {
	private Group<XMethod> group_;
	
	public ListClassMethods() {
		group_ = new Group<>();
	}
	
	@Override
	public void buildGroup(XClass entity) {
		try {
			for (final IMethod method: entity.getUnderlyingObject().getMethods()) {
					group_.add(FactoryMethod.createXMethod(method));
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public Group<XMethod> getGroup() {
		return group_;
	}
}
