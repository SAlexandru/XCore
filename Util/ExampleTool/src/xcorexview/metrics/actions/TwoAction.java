package xcorexview.metrics.actions;

import com.salexandru.xcore.utils.interfaces.HList;
import com.salexandru.xcore.utils.interfaces.HListEmpty;
import com.salexandru.xcore.utils.interfaces.IActionPerformer;
import com.salexandru.xcore.utils.metaAnnotation.ActionPerformer;

import exampletool.metamodel.entity.XClass;

@ActionPerformer
public class TwoAction implements IActionPerformer<String, XClass, HList<String, HList<Integer, HListEmpty>> > {

	@Override
	public String performAction(XClass arg0, HList<String, HList<Integer, HListEmpty>> arg1) {
		// TODO Auto-generated method stub
		return null;
	}
}
