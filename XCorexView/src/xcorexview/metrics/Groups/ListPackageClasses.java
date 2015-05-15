package xcorexview.metrics.Groups;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import xmetamodel.XClass;
import xmetamodel.XPackage;
import xmetamodel.factory.FactoryMethod;

import com.salexandru.xcorex.interfaces.Group;
import com.salexandru.xcorex.interfaces.IGroupBuilder;
import com.salexandru.xcorex.metaAnnotation.GroupBuilder;

@GroupBuilder
public class ListPackageClasses implements IGroupBuilder<XClass, XPackage> {
	@Override
	public Group<XClass> buildGroup(XPackage entity) {
		Group<XClass> group_ = new Group<>();
		try {
			for (final IJavaElement element: entity.getUnderlyingObject().getChildren()) {
				if (element.getElementType() == IJavaElement.TYPE) {
					group_.add(FactoryMethod.createXClass((IType)element));
				}
				else if (element.getElementType() == IJavaElement.COMPILATION_UNIT) {
					IType type = ((ICompilationUnit)element).findPrimaryType();
					
					if (null != type) {
						group_.add(FactoryMethod.createXClass(type));
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
