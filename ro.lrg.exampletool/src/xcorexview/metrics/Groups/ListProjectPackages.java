package xcorexview.metrics.Groups;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;

import ro.lrg.xcore.metametamodel.*;

import exampletool.metamodel.entity.XPackage;
import exampletool.metamodel.entity.XProject;
import exampletool.metamodel.factory.Factory;

@RelationBuilder
public class ListProjectPackages implements IRelationBuilder<XPackage, XProject> {
	private void getPackages(final IPackageFragmentRoot rootFragment, final Group<XPackage> group_) {
		try {
			if (null == rootFragment || IPackageFragmentRoot.K_SOURCE != rootFragment.getKind()) {
				return;
			}
			
			for (final IJavaElement element: rootFragment.getChildren()) {
				if (element.getElementType() == IJavaElement.PACKAGE_FRAGMENT_ROOT) {
					getPackages((IPackageFragmentRoot)element, group_);
				}
				else if (element.getElementType() == IJavaElement.PACKAGE_FRAGMENT) {
					getPackages((IPackageFragment)element, group_);
				}
			}
			
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean isValid(final IPackageFragment fragment) throws JavaModelException {
		for (final IJavaElement element: fragment.getChildren()) {
			if (IJavaElement.COMPILATION_UNIT == element.getElementType()) {
				return true;
			}
		}
		return false;
	}
	
	private void getPackages(final IPackageFragment fragment, final Group<XPackage> group_) {
		try {
			if (null == fragment || IPackageFragmentRoot.K_SOURCE != fragment.getKind()) {
				return ;
			}
			
			if (isValid(fragment)) {
				group_.add(Factory.getInstance().createXPackage(fragment));
			}
			
			if (fragment.hasSubpackages()) {
				for (final IJavaElement element: fragment.getChildren()) {
					if (element.getElementType() == IJavaElement.PACKAGE_FRAGMENT_ROOT) {
						getPackages((IPackageFragmentRoot)element, group_);
					}
					else if (element.getElementType() == IJavaElement.PACKAGE_FRAGMENT) {
						getPackages((IPackageFragment)element, group_);
					}
				}			
			}
			
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Override
	public Group<XPackage> buildGroup(XProject entity) {
		Group<XPackage> group_ = new Group<>();
		try {	
			for (final IPackageFragment fragment: entity.getUnderlyingObject().getPackageFragments()) {
				if (fragment.isDefaultPackage()) {
					continue;
				}
				getPackages(fragment, group_);
			}
		
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return group_;
	}

}
