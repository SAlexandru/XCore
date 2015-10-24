package ro.lrg.insider.view;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class BrowseEntityAction implements IObjectActionDelegate {

	private ISelection selection_;
			
	@Override
	public void run(IAction action) {

		try {

			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			page.showView(XCorexTableView.viewId);
			XCorexTableView view = (XCorexTableView)page.findView(XCorexTableView.viewId);
			view.displayEntity(((IStructuredSelection)selection_).getFirstElement());

		} catch (PartInitException e) {
			e.printStackTrace();
			return;
		}
		
	}
	
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		selection_ = selection;
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {}
	
}
