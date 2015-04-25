package xcorexview.metrics.projects;

import xmetamodel.XProject;

import com.salexandru.corex.interfaces.IPropertyComputer;
import com.salexandru.corex.metaAnnotation.PropertyComputer;

@PropertyComputer
public class Name implements IPropertyComputer<String, XProject> {

	@Override
	public String compute(XProject entity) {
		return entity.getUnderlyingObject().getElementName();
	}
}
