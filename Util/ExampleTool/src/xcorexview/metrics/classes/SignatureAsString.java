package xcorexview.metrics.classes;

import com.salexandru.xcore.utils.interfaces.IPropertyComputer;
import com.salexandru.xcore.utils.metaAnnotation.PropertyComputer;

import exampletool.metamodel.entity.XMethod;

@PropertyComputer
public class SignatureAsString implements IPropertyComputer<String, XMethod> {

	@Override
	public String compute(XMethod entity) {
		
		return entity.asMethodReference().getSignature();
	}

}
