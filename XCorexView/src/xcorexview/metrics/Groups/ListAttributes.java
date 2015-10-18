package xcorexview.metrics.Groups;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

import xmetamodel.XClass;
import xmetamodel.XField;
import xmetamodel.XMethod;
import xmetamodel.factory.FactoryMethod;

import com.salexandru.xcore.interfaces.Group;
import com.salexandru.xcore.interfaces.IGroupBuilder;
import com.salexandru.xcore.metaAnnotation.GroupBuilder;

@GroupBuilder
public class ListAttributes implements IGroupBuilder<XField, XClass> {
	@Override
	public Group<XField> buildGroup(XClass entity) {
		Group<XField> group_ = new Group<>();
		try {
			for (final IField field: entity.getUnderlyingObject().getFields()) {
					if (Flags.isPublic(field.getFlags())) {
						group_.add(FactoryMethod.createXField(field));
					}
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return group_;
	}
}