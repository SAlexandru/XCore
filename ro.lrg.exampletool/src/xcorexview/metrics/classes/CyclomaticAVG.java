package xcorexview.metrics.classes;

import ro.lrg.xcore.metametamodel.*;

import exampletool.metamodel.entity.XClass;
import exampletool.metamodel.entity.XMethod;

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
