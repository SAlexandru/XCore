package xcorexview.metrics.projects;

import ro.lrg.xcore.metametamodel.*;

import exampletool.metamodel.entity.XProject;

@PropertyComputer
public class ToString implements IPropertyComputer<String, XProject> {

	@Override
	public String compute(XProject entity) {
		return entity.getUnderlyingObject().getElementName();
	}
}
