package xcorexview.metrics.Groups;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;

import xmetamodel.XClass;
import xmetamodel.XPackage;
import xmetamodel.factory.FactoryMethod;

import com.salexandru.corex.interfaces.Group;
import com.salexandru.corex.interfaces.IGroupBuilder;
import com.salexandru.corex.metaAnnotation.GroupBuilder;

@GroupBuilder
public class ListPackageClasses implements IGroupBuilder<XClass, XPackage> {
	private Group<XClass> group_;
	
	public ListPackageClasses() {
		group_ = new Group<>();
	}
	
	@Override
	public void buildGroup(XPackage entity) {
		try {
			for (final IJavaElement element: entity.getUnderlyingObject().getChildren()) {
				if (element.getElementType() == IJavaElement.COMPILATION_UNIT) {
					group_.add(FactoryMethod.createXClass((ICompilationUnit)element));
				}
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public Group<XClass> getGroup() {
		return group_;
	}
}
