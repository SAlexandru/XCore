package com.salexandru.codeGeneration;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.type.TypeMirror;

import com.salexandru.xcore.preferencepage.ExtraTypeBinding;
import com.salexandru.xcore.utils.interfaces.XEntity;

public class XMetaModelEntityGenerator {
	
	private TypeMirror propertie_;
	
	private List<XPropertyComputerGenerator> computers_;
	private List<XRelationBuilder> groupBuilders_;
	private List<XActionPreformerGenerator> actionPerformers_;
	private List<ExtraTypeBinding> extraMetaTypes_;
	
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
		extraMetaTypes_ = new ArrayList<>();
		this.entity_package = entity_package;
		this.impl_package = impl_package;
	}
	
	public String getName() {
		return getSimpleName(propertie_.toString());
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
	
	public String getUnderlyingType() {
		return underlyingType_;
	}
	
	public void addGroupBuilder (XRelationBuilder gb) {
		groupBuilders_.add(gb);
	}
	
	public void setUnderlyingMetaType(String type) {
		underlyingType_ = type;
	}
	
	public void addExtraMetaType(List<ExtraTypeBinding> bindings) {
		extraMetaTypes_.addAll(bindings);
	}
	
	public void setExtendedMetaType(String type) {
		if (null != type) {
			extendedMetaType = type;
		}
		else extendedMetaType = XEntity.class.getCanonicalName();
	}

	public String getExtendedMetaType() {
		return extendedMetaType;
	}

	public List<XRelationBuilder> getBuilders() {return groupBuilders_;}
	
	public String generateImpl() {
		underlyingType_ = underlyingType_.trim();
		boolean isExtension = this.isExtension();
		
		StringBuilder s = new StringBuilder();
		s.append("package " + impl_package +";\n\n");
		
		s.append("import " + entity_package + ".*;\n");
		s.append("import com.salexandru.xcore.utils.interfaces.HList;\n");
		s.append("import com.salexandru.xcore.utils.interfaces.HListEmpty;\n");
		s.append("import com.salexandru.xcore.utils.annotationMarkers.*;\n");
		for (XPropertyComputerGenerator computer: computers_) {
			s.append ("import " + computer.getComputer().getQualifiedName() + ";\n");
		}
		for (XRelationBuilder gb: groupBuilders_) {
			s.append ("import " + gb.getQualifiedName() + ";\n");
		}
		for (XActionPreformerGenerator act: actionPerformers_) {
			s.append ("import " + act.getQualifiedName() + ";\n");
		}
		
		s.append("\n\n");
		if(isExtension) {
			String extendedImpl = extendedMetaType.substring(0,extendedMetaType.indexOf(".entity"));
			extendedImpl = extendedImpl + ".impl." + getName() + "Impl";
			s.append("public class " + getName() + "Impl" + " extends " + extendedImpl + " implements " + getName() + " {\n");			
		} else {
			s.append("public class " + getName() + "Impl" + " implements " + getName() + " {\n");
		}
		
		s.append("    private " + underlyingType_ + " underlyingObj_;\n\n");
		
		for (XPropertyComputerGenerator computer: computers_) {
			s.append(String.format("    private static final %1$s %1$s_INSTANCE = new %1$s();\n", computer.getName()));
		}
		
		for (XRelationBuilder gb: groupBuilders_) {
			s.append(String.format("    private static final %1$s %1$s_INSTANCE = new %1$s();\n", gb.getName()));
		}
		
		for (XActionPreformerGenerator act: actionPerformers_) {
			s.append(String.format("    private static final %1$s %1$s_INSTANCE = new %1$s();\n", act.getName()));
		}
		
		
		
		s.append("\n\n");
		s.append("    public " + getName() + "Impl" + "(" + underlyingType_ + " underlyingObj) {\n");
		if(isExtension) {
			s.append("        super(underlyingObj);\n");			
		}
		s.append("        underlyingObj_ = underlyingObj;\n");
		s.append("    }\n");
		
		
		s.append("    @Override\n");
		s.append("    public " + underlyingType_ +" getUnderlyingObject() {\n");
		s.append("        return underlyingObj_;\n");
		s.append("    }\n");
		
		for (ExtraTypeBinding binding: extraMetaTypes_) {
			final String extraType = binding.getType().trim();
			final String transformer = binding.getTransformer().trim();
			final String value = extraType.substring(extraType.lastIndexOf(".") + 1);
			final String pattern = String.format("final com.salexandru.xcore.utils.interfaces.ITransform<%s, %s> ", underlyingType_, extraType);
			final String returnType = String.format("com.salexandru.xcore.utils.interfaces.IReversableTransform<%s, %s>", underlyingType_, extraType);
			
			s.append("    public " + returnType +" transformOriginalTo_" + value + " () {\n");
			s.append("    	  final " + underlyingType_ + " obj = underlyingObj_;\n" );
			s.append(String.format("        return new com.salexandru.xcore.utils.interfaces.IReversableTransform<%s, %s>() {\n", underlyingType_, extraType));
			s.append(String.format("        		private %s transformer = new %s();\n", pattern, transformer));
			s.append("        		@Override\n");
			s.append("        		public " + extraType + " transform() { return transformer.transform(obj); }\n"  );
			s.append("        		@Override\n");
			s.append("        		public " + underlyingType_ + " reverse() { return obj; }\n"  );
			s.append("    	  };\n");
			s.append("    }\n");
			
			s.append("    public " + returnType +" transformOriginalTo_" + value + " (" + pattern + " transform) {\n");
			s.append("    	  final " + underlyingType_ + " obj = underlyingObj_;\n" );
			s.append(String.format("        return new com.salexandru.xcore.utils.interfaces.IReversableTransform<%s, %s>() {\n", underlyingType_, extraType));
			s.append("        		@Override\n");
			s.append("        		public " + extraType + " transform() { return transform.transform(obj); }\n"  );
			s.append("        		@Override\n");
			s.append("        		public " + underlyingType_ + " reverse() { return obj; }\n"  );
			s.append("    	  };\n");
			s.append("    }\n");
			
		}
		
		
		for (XPropertyComputerGenerator computer: computers_) {
			s.append("@Override\n");
			s.append(computer.generateImpl(computer.getName() + "_INSTANCE"));
		}
		for (XRelationBuilder gb: groupBuilders_) {
			s.append("@Override\n");
			s.append(gb.generateImpl(gb.getName() + "_INSTANCE"));
			s.append("\n");
		}
		for (XActionPreformerGenerator act: actionPerformers_) {
			s.append("@Override\n");
			s.append(act.generateImpl(act.getName() + "_INSTANCE"));
			s.append("\n");
		}
		
		
		/*
		 * hopefully this works 
		 */
		s.append("    public boolean equals(Object obj) {\n");
		s.append("        if (null == obj || !(obj instanceof " + getName() + "Impl" + ")) {\n");
		s.append("           return false;\n");
		s.append("        }\n");
		s.append("        " + getName() + "Impl" + " iObj = (" + getName() + "Impl" + ")obj;\n");
		s.append("        return underlyingObj_.equals(iObj.underlyingObj_);\n");
		s.append("    }\n");
		
		s.append("    public int hashCode() {\n");
		s.append("        return 97 * underlyingObj_.hashCode();\n");
		s.append("    }\n");
		s.append("}\n");

		return s.toString();
	}
	
	public String toString() {
		StringBuilder s = new StringBuilder("");
		s.append("package " + entity_package + ";\n");
		s.append("import com.salexandru.xcore.utils.annotationMarkers.*;\n");
		s.append("public interface " + getName() + " extends " + extendedMetaType + " {\n");
		for (XPropertyComputerGenerator computer: computers_) {
			s.append("\t" + computer.generateSignature());
			s.append("\n");
		}
		for (XRelationBuilder gb: groupBuilders_) {
			s.append("\t" + gb.generateSignature());
			s.append("\n");
		}		
		for (XActionPreformerGenerator act: actionPerformers_) {
			s.append("\t" + act.generateSignature());
			s.append("\n");
		}
		s.append("\t" + underlyingType_ + " getUnderlyingObject();\n");
		
		for (ExtraTypeBinding binding: extraMetaTypes_) {
			String type = binding.getType().trim();
			String value = type.substring(type.lastIndexOf(".") + 1);
			String pattern = String.format("final com.salexandru.xcore.utils.interfaces.ITransform<%s, %s> transform", underlyingType_, type);
			String returnType = String.format("com.salexandru.xcore.utils.interfaces.IReversableTransform<%s, %s>", underlyingType_, type);
			
			s.append("     " + returnType +" transformOriginalTo_" + value + " ();\n");
			s.append("     " + returnType +" transformOriginalTo_" + value + " (" + pattern + ");\n");
		}
		
		s.append("}\n");
		return s.toString();
	}
	
	private String getSimpleName(String name) {return name.substring(name.lastIndexOf('.') + 1);}

	public boolean isExtension() {
		return null != extendedMetaType && !extendedMetaType.equals(XEntity.class.getCanonicalName());
	}

	public void addActionPerformer(XActionPreformerGenerator gen) {
		actionPerformers_.add(gen);
	}

}
