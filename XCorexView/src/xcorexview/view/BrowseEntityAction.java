package xcorexview.view;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.salexandru.corex.interfaces.XEntity;

import xmetamodel.factory.FactoryMethod;


public class BrowseEntityAction implements IObjectActionDelegate {
	private ISelection selection_;
	
	@Override
	public void run(IAction action) {
		try {
			IJavaElement element = ((IJavaElement)((IStructuredSelection)selection_).getFirstElement());
			XEntity entity = toXEntity(element);
			
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			try {
				page.showView("InsiderViewer.Insider");
			} catch (PartInitException e) {
				e.printStackTrace();
				return;
			}
			CorexTableView view = (CorexTableView)page.findView(CorexTableView.viewId);
			view.displayEntity(entity);
		}
		catch (ClassCastException e) {
			
		}
	}
	
	private XEntity toXEntity(IJavaElement element) {
		switch (element.getElementType()) {
			case IJavaElement.METHOD: return FactoryMethod.createXMethod((IMethod)element);
			case IJavaElement.COMPILATION_UNIT: return FactoryMethod.createXClass((ICompilationUnit)element);
			case IJavaElement.PACKAGE_FRAGMENT: return FactoryMethod.createXPackage((IPackageFragment)element);
			case IJavaElement.JAVA_PROJECT: return FactoryMethod.createXProject((IJavaProject)element);
			
			default: return null;
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		selection_ = selection;
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {}



	
}
