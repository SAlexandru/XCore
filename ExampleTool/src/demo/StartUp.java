package demo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.PartInitException;

import com.salexandru.xcore.interfaces.XEntity;

import exampletool.metamodel.factory.Factory;
import ro.lrg.insider.view.ToolRegistration;

public class StartUp implements org.eclipse.ui.IStartup{

	@Override
	public void earlyStartup() {
		ToolRegistration.getInstance().registerXEntityConverter(
				new ToolRegistration.XEntityConverter() {
					@Override
					public XEntity convert(Object elem) {
						if(elem instanceof IJavaElement) {
							IJavaElement element = (IJavaElement)elem;
							switch (element.getElementType()) {
								case IJavaElement.METHOD: return Factory.getInstance().createXMethod((IMethod)element);
								case IJavaElement.TYPE: return Factory.getInstance().createXClass((IType)element);
								case IJavaElement.COMPILATION_UNIT:
									ICompilationUnit unit = (ICompilationUnit)element;
									return Factory.getInstance().createXClass(unit.findPrimaryType());
									
								case IJavaElement.PACKAGE_FRAGMENT: return Factory.getInstance().createXPackage((IPackageFragment)element);
								case IJavaElement.JAVA_PROJECT: return Factory.getInstance().createXProject((IJavaProject)element);
							}
						}
						return null;
					}
					
					@Override
					public String getToolName() {
						return "Example Tool";
					}
					
					@Override
					public void show(XEntity theEntity) {
						try {
							Method met = theEntity.getClass().getMethod("getUnderlyingObject");
							Object result = met.invoke(theEntity);
							if (result instanceof IJavaElement) {
								JavaUI.openInEditor((IJavaElement)result, true, true);
							}
						}
						catch (PartInitException | JavaModelException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
							e1.printStackTrace();
						}
					}
					
				}
		);		
	}

}
