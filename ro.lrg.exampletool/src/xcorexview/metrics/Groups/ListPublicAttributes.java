package xcorexview.metrics.Groups;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.JavaModelException;

import ro.lrg.xcore.metametamodel.*;

import exampletool.metamodel.entity.XClass;
import exampletool.metamodel.entity.XField;
import exampletool.metamodel.factory.Factory;

@RelationBuilder
public class ListPublicAttributes implements IRelationBuilder<XField, XClass> {
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