package xcorexview.metrics.classes;

import ro.lrg.xcore.metametamodel.*;

import exampletool.metamodel.entity.XClass;

@PropertyComputer
public class WOC implements IPropertyComputer<Double, XClass> {

	@Override
	public Double compute(XClass entity) {
        double accessorM = entity.listAccesorMethods().getElements().size() + entity.listAttributes().getElements().size() + 0.0;
        double publicM = entity.listPublicMethods().getElements().size() + entity.listPublicAttributes().getElements().size() + 0.0;

        if (publicM == 0) return 0.0;
        return 1.00 - (accessorM / publicM);
	}

}