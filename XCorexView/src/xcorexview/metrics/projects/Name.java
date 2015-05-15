package xcorexview.metrics.projects;

import xmetamodel.XProject;

import com.salexandru.xcorex.interfaces.IPropertyComputer;
import com.salexandru.xcorex.metaAnnotation.PropertyComputer;

@PropertyComputer
public class Name implements IPropertyComputer<String, XProject> {

	@Override
	public String compute(XProject entity) {
		return entity.getUnderlyingObject().getElementName();
	}
}
