package src;

import xmetamodel.XClass;

import com.salexandru.xcorex.interfaces.IPropertyComputer;
import com.salexandru.xcorex.metaAnnotation.PropertyComputer;

@PropertyComputer
public class Metric implements IPropertyComputer<Integer, XClass> {

	@Override
	public Integer compute(XClass entity) {
		// TODO Auto-generated method stub
		return null;
	}

}
