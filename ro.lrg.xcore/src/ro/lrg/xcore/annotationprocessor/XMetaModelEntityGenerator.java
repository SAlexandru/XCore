package ro.lrg.xcore.annotationprocessor;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.type.TypeMirror;

import ro.lrg.xcore.metametamodel.XEntity;

public class XMetaModelEntityGenerator {
	
	private TypeMirror propertie_;
	
	private List<XPropertyComputerGenerator> computers_;
	private List<XRelationBuilderGenerator> groupBuilders_;
	private List<XActionPreformerGenerator> actionPerformers_;
	
	private String underlyingType_;
	private String extendedMetaType;
	
	private String entity_package;
	private String impl_package;
	
	public XMetaModelEntityGenerator(TypeMirror propertie, String entity_package, String impl_package) {
		if (null == propertie) {
			throw new NullPointerException();
		}
		propertie_ = propertie;
		computers_ = new ArrayList<>();
		groupBuilders_ = new ArrayList<>();
		actionPerformers_ = new ArrayList<>();
		this.entity_package = entity_package;
		this.impl_package = impl_package;
	}
	
	public String getName() {
		return getSimpleName(propertie_.toString());
	}

	private String getSimpleName(String name) {
		return name.substring(name.lastIndexOf('.') + 1);
	}

	public String getNameImpl() {
		return getName() + "Impl";
	}

	public String getQualifiedName() {
		return entity_package + "." + getName();
	}
	
	public String getQualifiedNameImpl() {
		return impl_package + "." + getNameImpl();
	}
		
	public void addComputer(XPropertyComputerGenerator computer) {
		computers_.add(computer);
	}	
	public void addRelationBuilder (XRelationBuilderGenerator gb) {
		groupBuilders_.add(gb);
	}
	
	public void addActionPerformer(XActionPreformerGenerator gen) {
		actionPerformers_.add(gen);
	}
	
	public String getUnderlyingType() {
		return underlyingType_;
	}

	public void setUnderlyingMetaType(String type) {
		underlyingType_ = type;
	}

	public void setExtendedMetaType(String type) {
		extendedMetaType = type;
	}

	public String getExtendedMetaType() {
		return extendedMetaType;
	}

	public List<XRelationBuilderGenerator> getBuilders() {
		return groupBuilders_;
	}
	
	public String generateImpl() {
		boolean isExtension = this.isExtension();
		
		StringBuilder s = new StringBuilder();
		s.append("package " + impl_package +";\n\n");
		
		s.append("import " + entity_package + ".*;\n");
		for (XPropertyComputerGenerator computer: computers_) {
			s.append ("import " + computer.getComputer().getQualifiedName() + ";\n");
		}
		for (XRelationBuilderGenerator gb: groupBuilders_) {
			s.append ("import " + gb.getQualifiedName() + ";\n");
		}
		for (XActionPreformerGenerator act: actionPerformers_) {
			s.append ("import " + act.getQualifiedName() + ";\n");
		}
		
		s.append("\n");
		if(isExtension) {
			String extendedImpl = extendedMetaType.substring(0,extendedMetaType.indexOf(".entity"));
			extendedImpl = extendedImpl + ".impl." + getName() + "Impl";
			s.append("public class " + getName() + "Impl" + " extends " + extendedImpl + " implements " + getName() + " {\n");			
		} else {
			s.append("public class " + getName() + "Impl" + " implements " + getName() + " {\n");
		}
		
		s.append("\n");
		s.append("\tprivate " + underlyingType_ + " underlyingObj_;\n\n");		
		for (XPropertyComputerGenerator computer: computers_) {
			s.append(String.format("\tprivate static final %1$s %1$s_INSTANCE = new %1$s();\n", computer.getName()));
		}
		for (XRelationBuilderGenerator gb: groupBuilders_) {
			s.append(String.format("\tprivate static final %1$s %1$s_INSTANCE = new %1$s();\n", gb.getName()));
		}
		for (XActionPreformerGenerator act: actionPerformers_) {
			s.append(String.format("\tprivate static final %1$s %1$s_INSTANCE = new %1$s();\n", act.getName()));
		}
		
		s.append("\n");
		s.append("\tpublic " + getName() + "Impl" + "(" + underlyingType_ + " underlyingObj) {\n");
		if(isExtension) {
			s.append("\t\tsuper(underlyingObj);\n");			
		}
		s.append("\t\tunderlyingObj_ = underlyingObj;\n");
		s.append("\t}\n\n");
		
		s.append("\t@Override\n");
		s.append("\tpublic " + underlyingType_ +" getUnderlyingObject() {\n");
		s.append("\t\treturn underlyingObj_;\n");
		s.append("\t}\n\n");
		
		for (XPropertyComputerGenerator computer: computers_) {
			s.append("\t@Override\n");
			s.append(computer.generateImpl(computer.getName() + "_INSTANCE"));
			s.append("\n");
		}
		for (XRelationBuilderGenerator gb: groupBuilders_) {
			s.append("\t@Override\n");
			s.append(gb.generateImpl(gb.getName() + "_INSTANCE"));
			s.append("\n");
		}
		for (XActionPreformerGenerator act: actionPerformers_) {
			s.append("\t@Override\n");
			s.append(act.generateImpl(act.getName() + "_INSTANCE"));
			s.append("\n");
		}
		
		s.append("\tpublic boolean equals(Object obj) {\n");
		s.append("\t\tif (null == obj || !(obj instanceof " + getName() + "Impl" + ")) {\n");
		s.append("\t\t\treturn false;\n");
		s.append("\t\t}\n");
		s.append("\t\t" + getName() + "Impl" + " iObj = (" + getName() + "Impl" + ")obj;\n");
		s.append("\t\treturn underlyingObj_.equals(iObj.underlyingObj_);\n");
		s.append("\t}\n\n");
		
		s.append("\tpublic int hashCode() {\n");
		s.append("\t\treturn 97 * underlyingObj_.hashCode();\n");
		s.append("\t}\n");
		s.append("}\n");

		return s.toString();
	}
	
	public String toString() {
		StringBuilder s = new StringBuilder("");
		s.append("package " + entity_package + ";\n\n");
		s.append("public interface " + getName() + " extends " + extendedMetaType + " {\n\n");
		for (XPropertyComputerGenerator computer: computers_) {
			s.append(computer.generateSignature());
		}
		for (XRelationBuilderGenerator gb: groupBuilders_) {
			s.append(gb.generateSignature());
		}		
		for (XActionPreformerGenerator act: actionPerformers_) {
			s.append(act.generateSignature());
		}
		s.append("\t" + underlyingType_ + " getUnderlyingObject();\n");
		s.append("}\n");
		return s.toString();
	}

	public boolean isExtension() {
		return !extendedMetaType.equals(XEntity.class.getCanonicalName());
	}

}
