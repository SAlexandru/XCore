package com.salexandru.codeGeneration;

import com.salexandru.xcorex.interfaces.Group;
import com.salexandru.xcorex.interfaces.XEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.type.TypeMirror;

public class XPropertyComputer {
	private TypeMirror propertie_;
	private List<XComputer> computers_;
	private List<XGroupBuilder> groupBuilders_;
	private String underlyingType_;

	public final static String PACKAGE = "xmetamodel";
	public final static String PACKAGE_IMPL = "xmetamodel.implementation";
	
	public XPropertyComputer(TypeMirror propertie) {
		if (null == propertie) {
			throw new NullPointerException();
		}
		propertie_ = propertie;
		computers_ = new ArrayList<>();
		groupBuilders_ = new ArrayList<>();
	}
	
	public String getName() {
		return getSimpleName(propertie_.toString());
	}
	
	public String getNameImpl() {
		return getName() + "Impl";
	}
	
	public String getQualifiedName() {
		return PACKAGE + '.' + getName();
	}
	
	public String getQualifiedNameImpl() {
		return PACKAGE_IMPL + '.' + getNameImpl();
	}
	
	public void addComputer (XComputer computer) {
		computers_.add(computer);
	}
	
	public String getUnderlyingType() {
		return underlyingType_;
	}
	
	public void addGroupBuilder (XGroupBuilder gb) {
		groupBuilders_.add(gb);
	}
	
	public void setUnderlyingType(String type) {
		underlyingType_ = type;
	}
	
	public List<XGroupBuilder> getBuilders() {return groupBuilders_;}
	
	public String generateImpl() {
		StringBuilder s = new StringBuilder();
		s.append("package " + PACKAGE_IMPL + ";\n\n");
		
		s.append("import xmetamodel.*;\n");
		s.append("import " + Group.class.getCanonicalName() + ";\n");
		for (XComputer computer: computers_) {
			s.append ("import " + computer.getComputer().getQualifiedName() + ";\n");
		}
		for (XGroupBuilder gb: groupBuilders_) {
			s.append ("import " + gb.getQualifiedName() + ";\n");
		}
		s.append("\n\n");
		s.append("public class " + getNameImpl() + " implements " + getName() + " {\n");
		s.append("    private " + underlyingType_ + " underlyingObj_;\n\n");
		for (XComputer computer: computers_) {
			s.append(String.format("    private static final %1$s %1$s_INSTANCE = new %1$s();\n", computer.getName()));
		}
		for (XGroupBuilder gb: groupBuilders_) {
			s.append(String.format("    private %1$s %1$s_INSTANCE;\n", gb.getName()));
			
		}
		s.append("\n\n");
		s.append("    public " + getNameImpl() + "(" + underlyingType_ + " underlyingObj) {\n");
		s.append("        underlyingObj_ = underlyingObj;\n");
		s.append("    }\n");
		
		
		final Map<String, List<String>> builders = new HashMap<>();
		
		s.append("    @Override\n");
		s.append("    public " + underlyingType_ +" getUnderlyingObject() {\n");
		s.append("        return underlyingObj_;\n");
		s.append("    }\n");
		for (XComputer computer: computers_) {
			s.append("@Override\n");
			final String x = computer.getEntityType().toString();
			s.append(computer.generateImpl(computer.getName() + "_INSTANCE"));
		}
		for (XGroupBuilder gb: groupBuilders_) {
			s.append("@Override\n");
			s.append(gb.generateImpl(gb.getName() + "_INSTANCE"));
			s.append("\n");
		}
		/*
		 * hopefully this works 
		 */
		s.append("    public boolean equals(Object obj) {\n");
		s.append("        if (null == obj || !(obj instanceof " + getNameImpl() + ")) {\n");
		s.append("           return false;\n");
		s.append("        }\n");
		s.append("        " + getNameImpl() + " iObj = (" + getNameImpl() + ")obj;\n");
		s.append("        if (null == underlyingObj_ || null == iObj.getUnderlyingObject()) {\n");
		s.append("           return true;\n");
		s.append("        }\n");
		s.append("        return underlyingObj_.equals(iObj);\n");
		s.append("    }\n");
		s.append("}\n");
		
		return s.toString();
	}
	
	public String toString() {
		StringBuilder s = new StringBuilder("");
		s.append("package " + PACKAGE + ";\n");
		s.append("import " + XEntity.class.getCanonicalName() + ";\n");
		s.append("import " + Group.class.getCanonicalName() + ";\n\n");
		
		s.append("public interface " + getName() + " extends XEntity {\n");
		for (XComputer computer: computers_) {
			s.append(computer.generateSignature());
			s.append("\n");
		}
		
		for (XGroupBuilder gb: groupBuilders_) {
			s.append(gb.generateSignature());
			s.append("\n");
		}
		s.append(underlyingType_ + " getUnderlyingObject();\n");
		s.append("}\n");
		
		return s.toString();
	}
	
	private String getSimpleName(String name) {return name.substring(name.lastIndexOf('.') + 1);}
}
