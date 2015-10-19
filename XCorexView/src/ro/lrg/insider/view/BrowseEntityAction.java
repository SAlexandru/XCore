package ro.lrg.insider.view;

import java.util.ArrayList;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.salexandru.xcore.interfaces.XEntity;

public class BrowseEntityAction implements IObjectActionDelegate {

	private ISelection selection_;
		
	public interface XEntityConverter {
		public XEntity convert(IJavaElement element);
	}
	
	private static ArrayList<XEntityConverter> converters = new ArrayList<>();
	
	private static XEntity toXEntity(IJavaElement element) {
		for(XEntityConverter aConv : converters) {
			XEntity res = aConv.convert(element);
			if(res != null) {
				return res;
			}
		}
		return null;
	}
	
	public static void registerXEntityConverter(XEntityConverter conv) {
		converters.add(conv);
	}
	
	@Override
	public void run(IAction action) {
		IJavaElement element = ((IJavaElement)((IStructuredSelection)selection_).getFirstElement());	
		XEntity entity = toXEntity(element);			
		if(entity == null) {
			return;
		}

		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try {
			page.showView(XCorexTableView.viewId);
		} catch (PartInitException e) {
			e.printStackTrace();
			return;
		}
						
		XCorexTableView view = (XCorexTableView)page.findView(XCorexTableView.viewId);
		view.displayEntity(entity);
	}
	
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		selection_ = selection;
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {}
	
}
