package xcorexview.metrics.Groups;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.salexandru.xcore.utils.interfaces.Group;
import com.salexandru.xcore.utils.interfaces.IRelationBuilder;
import com.salexandru.xcore.utils.metaAnnotation.RelationBuilder;

import exampletool.metamodel.entity.XClass;
import exampletool.metamodel.entity.XPackage;
import exampletool.metamodel.factory.Factory;

@RelationBuilder
public class ListPackageClasses implements IRelationBuilder<XClass, XPackage> {
	@Override
	public Group<XClass> buildGroup(XPackage entity) {
		Group<XClass> group_ = new Group<>();
		try {
			for (final IJavaElement element: entity.getUnderlyingObject().getChildren()) {
				if (element.getElementType() == IJavaElement.TYPE) {
					group_.add(Factory.getInstance().createXClass((IType)element));
				}
				else if (element.getElementType() == IJavaElement.COMPILATION_UNIT) {
					IType type = ((ICompilationUnit)element).findPrimaryType();
					
					if (null != type) {
						group_.add(Factory.getInstance().createXClass(type));
					}
				}
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return group_;
	}
}
