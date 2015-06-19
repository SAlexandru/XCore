package xcorexview.metrics.classes;

import xmetamodel.XClass;
import xmetamodel.XMethod;

import com.salexandru.xcorex.interfaces.IPropertyComputer;
import com.salexandru.xcorex.metaAnnotation.PropertyComputer;

@PropertyComputer
public class CyclomaticAVG implements IPropertyComputer<Double, XClass>{

	@Override
	public Double compute(XClass entity) {
		long sum = 0;
		int count = 0;
		
		
		for (final XMethod method: entity.listClassMethods().getElements()) {
			sum += method.cyclomaticComplexity();
			++count;
		}
		
		return (1.0 * sum) / count;
	}

}
