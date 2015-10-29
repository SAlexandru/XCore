package xcorexview.metrics.projects;

import com.salexandru.xcore.interfaces.IPropertyComputer;
import com.salexandru.xcore.metaAnnotation.PropertyComputer;

import exampletool.metamodel.entity.XProject;

@PropertyComputer
public class ToString implements IPropertyComputer<String, XProject> {

	@Override
	public String compute(XProject entity) {
		return entity.getUnderlyingObject().getElementName();
	}
}
