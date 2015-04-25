package xcorexview.metrics.Groups;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;

import xmetamodel.XPackage;
import xmetamodel.XProject;
import xmetamodel.factory.FactoryMethod;

import com.salexandru.corex.interfaces.Group;
import com.salexandru.corex.interfaces.IGroupBuilder;
import com.salexandru.corex.metaAnnotation.GroupBuilder;

@GroupBuilder
public class ListProjectPackages implements IGroupBuilder<XPackage, XProject> {
	private Group<XPackage> group_;
	
	public ListProjectPackages() {
		group_ = new Group<>();
	}
	
	@Override
	public void buildGroup(XProject entity) {
		try {
			for (final IJavaElement element: entity.getUnderlyingObject().getChildren()) {
				if (element.getElementType() == IJavaElement.PACKAGE_FRAGMENT) {
					group_.add(FactoryMethod.createXPackage((IPackageFragment)element));
				}
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public Group<XPackage> getGroup() {
		return group_;
	}
}
