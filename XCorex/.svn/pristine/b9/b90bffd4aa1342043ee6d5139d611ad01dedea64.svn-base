package com.salexandru.codeGeneration;

import com.salexandru.corex.interfaces.Group;
import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

public class XPropertyComputer {
	private TypeMirror propertie_;
	private List<TypeElement> computers_;
	private List<TypeElement> groupBuilders_;
	
	public final static String PREFIX = "X";
	public final static String PACKAGE = "xmetamodel";
	
	public XPropertyComputer(TypeMirror propertie) {
		propertie_ = propertie;
		computers_ = new ArrayList<>();
		groupBuilders_ = new ArrayList<>();
	}
	
	public String getName() {
		return getSimpleName(propertie_.toString());
	}
	
	public String getQualifiedName() {
		return PACKAGE + '.' + getName();
	}
	
	public void addComputer (TypeElement pc) {
		computers_.add(pc);
	}
	
	public void addGroupBuilder (TypeElement gb) {
		groupBuilders_.add(gb);
	}
	
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("package " + PACKAGE + ";\n");
		s.append("import " + Group.class.getPackage().getName() + ".Group;\n\n");
		
		s.append("public interface " + getName() + " {\n");
		for (TypeElement elem: computers_) {
			s.append("\tdouble " + elem.getSimpleName() + "();\n");
		}
		s.append("\tGroup<" + getName() + "> buildGroup();\n");
		s.append("\tObject getUnderlyingObject();\n");
		s.append("}\n");
		
		return s.toString();
	}
	
	private String getSimpleName(String name) {return name.substring(name.lastIndexOf('.') + 1);}
}
