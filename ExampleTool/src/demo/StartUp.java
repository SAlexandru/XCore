package demo;

import javax.management.RuntimeErrorException;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;

import com.salexandru.xcore.interfaces.XEntity;

import ro.lrg.insider.view.BrowseEntityAction;
import xmetamodel.factory.FactoryMethod;

public class StartUp implements org.eclipse.ui.IStartup{

	@Override
	public void earlyStartup() {
		BrowseEntityAction.registerXEntityConverter(
				new BrowseEntityAction.XEntityConverter() {
					@Override
					public XEntity convert(IJavaElement element) {
						switch (element.getElementType()) {
							case IJavaElement.METHOD: return FactoryMethod.createXMethod((IMethod)element);
							case IJavaElement.TYPE: return FactoryMethod.createXClass((IType)element);
							case IJavaElement.COMPILATION_UNIT:
								ICompilationUnit unit = (ICompilationUnit)element;
								return FactoryMethod.createXClass(unit.findPrimaryType());
								
							case IJavaElement.PACKAGE_FRAGMENT: return FactoryMethod.createXPackage((IPackageFragment)element);
							case IJavaElement.JAVA_PROJECT: return FactoryMethod.createXProject((IJavaProject)element);
							case IJavaElement.PACKAGE_FRAGMENT_ROOT: throw new RuntimeErrorException(null);		
							default: return null;
						}
					}
				}
		);		
	}

}
