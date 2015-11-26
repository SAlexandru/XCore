package xcorexview.metrics.Groups;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.JavaModelException;

import com.salexandru.xcore.interfaces.Group;
import com.salexandru.xcore.interfaces.IGroupBuilder;
import com.salexandru.xcore.metaAnnotation.GroupBuilder;

import exampletool.metamodel.entity.XClass;
import exampletool.metamodel.entity.XField;
import exampletool.metamodel.factory.Factory;

@GroupBuilder
public class ListPublicAttributes implements IGroupBuilder<XField, XClass> {
	@Override
	public Group<XField> buildGroup(XClass entity) {
		Group<XField> group_ = new Group<>();
		try {
			for (final IField field: entity.getUnderlyingObject().getFields()) {
					group_.add(Factory.getInstance().createXField(field));
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return group_;
	}
}