package ro.lrg.insider.view;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

public class BrowseEntityAction extends AbstractHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			page.showView(XCorexTableView.viewId);
			XCorexTableView view = (XCorexTableView) page.findView(XCorexTableView.viewId);
			view.displayEntity(((IStructuredSelection) selection).getFirstElement());

		} catch (PartInitException e) {
			e.printStackTrace();
		}
		return event;
	}
}
