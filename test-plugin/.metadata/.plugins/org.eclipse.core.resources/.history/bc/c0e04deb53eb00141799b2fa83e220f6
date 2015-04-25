package xmetamodel.implementation;

import xmetamodel.*;
import com.salexandru.corex.interfaces.Group;
import xcorexview.metrics.methods.NumberOfLines;
import xcorexview.metrics.methods.NumberOfCasts;
import xcorexview.metrics.methods.CyclomaticComplexity;
import xcorexview.metrics.methods.NumberOfNullChecks;
import xcorexview.metrics.methods.Name;


public class XMethodImpl implements XMethod {
    private org.eclipse.jdt.core.IMethod underlyingObj_;

    private static final NumberOfLines NumberOfLines_INSTANCE = new NumberOfLines();
    private static final NumberOfCasts NumberOfCasts_INSTANCE = new NumberOfCasts();
    private static final CyclomaticComplexity CyclomaticComplexity_INSTANCE = new CyclomaticComplexity();
    private static final NumberOfNullChecks NumberOfNullChecks_INSTANCE = new NumberOfNullChecks();
    private static final Name Name_INSTANCE = new Name();


    public XMethodImpl(org.eclipse.jdt.core.IMethod underlyingObj) {
        underlyingObj_ = underlyingObj;
    }
    @Override
    public org.eclipse.jdt.core.IMethod getUnderlyingObject() {
        return underlyingObj_;
    }
@Override
public java.lang.Integer numberOfLines() {
    return NumberOfLines_INSTANCE.compute(this);
}
@Override
public java.lang.Integer numberOfCasts() {
    return NumberOfCasts_INSTANCE.compute(this);
}
@Override
public java.lang.Integer cyclomaticComplexity() {
    return CyclomaticComplexity_INSTANCE.compute(this);
}
@Override
public java.lang.Integer numberOfNullChecks() {
    return NumberOfNullChecks_INSTANCE.compute(this);
}
@Override
public java.lang.String name() {
    return Name_INSTANCE.compute(this);
}
    public boolean equals(Object obj) {
        if (null == obj || !(obj instanceof XMethodImpl)) {
           return false;
        }
        XMethodImpl iObj = (XMethodImpl)obj;
        if (null == underlyingObj_ || null == iObj.getUnderlyingObject()) {
           return true;
        }
        return underlyingObj_.equals(iObj);
    }
}
