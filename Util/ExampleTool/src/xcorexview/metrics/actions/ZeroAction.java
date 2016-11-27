package xcorexview.metrics.actions;

import com.salexandru.xcore.utils.interfaces.HListEmpty;
import com.salexandru.xcore.utils.interfaces.IActionPerformer;
import com.salexandru.xcore.utils.metaAnnotation.ActionPerformer;

import exampletool.metamodel.entity.XClass;

@ActionPerformer
public class ZeroAction implements IActionPerformer<String, XClass, HListEmpty> {

	@Override
	public String performAction(XClass arg0, HListEmpty arg) {
		// TODO Auto-generated method stub
		return null;
	}
}
