package xcorexview.metrics.actions;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.PartInitException;

import com.salexandru.xcore.utils.interfaces.HListEmpty;
import com.salexandru.xcore.utils.interfaces.IActionPerformer;
import com.salexandru.xcore.utils.metaAnnotation.ActionPerformer;

import exampletool.metamodel.entity.XClass;

@ActionPerformer
public class OpenInEditorAction implements IActionPerformer<Void, XClass, HListEmpty> {

	@Override
	public Void performAction(XClass entity, HListEmpty args) {
		try {
			JavaUI.openInEditor(entity.getUnderlyingObject(), true, true);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
