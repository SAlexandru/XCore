package ro.lrg.xcore.annotationprocessor;

import java.util.List;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.ThisIsAProperty;

public class XPropertyComputerGenerator {
	
	private TypeElement  computer_;
	private TypeMirror   entityType_;
	private TypeMirror   returnType_;
	private Elements     utils_;

	public XPropertyComputerGenerator(TypeElement computer, DeclaredType returnType) {
		if (null == computer || null == returnType) {
			throw new NullPointerException();
		}
		computer_ = computer;
		returnType_ = returnType;
	}
	
	public XPropertyComputerGenerator(TypeElement computer, Elements utils) {
		this(computer);
		utils_ = utils;
	}

	public XPropertyComputerGenerator(TypeElement computer) {
		if (null == computer) {
			throw new NullPointerException("Invalid (Null) computer!");
		}
		
		for (TypeMirror tIntf: computer.getInterfaces()) {
			DeclaredType dTIntf = (DeclaredType)tIntf;
			if (!dTIntf.asElement().getSimpleName().toString().equals(IPropertyComputer.class.getSimpleName())) {
				continue;
			}
			List<? extends TypeMirror> typeArgs = dTIntf.getTypeArguments();
			
			if (2 != typeArgs.size()) {
				throw new IllegalArgumentException("Illegal number of argument passed to IPropertyComputer");
			}
			for (TypeMirror t: typeArgs) {
				if (TypeKind.WILDCARD == t.getKind()) {
					throw new IllegalArgumentException("Wildcard types are not allowed (e.g ? extends T)");
				}
			}
			returnType_ = typeArgs.get(0);
			entityType_ = typeArgs.get(1);
		}
		computer_ = computer;
	}
	
	public void  setElementUtils(Elements utils) {utils_ = utils;}
	public Elements  getElementUtils() {return utils_;}

	public TypeElement getComputer()   {return computer_;}
	public TypeMirror  getReturnType() {return returnType_;}
	public TypeMirror  getEntityType() {return entityType_;}
	
	public String getName() {return getComputer().getSimpleName().toString();}
	public String getCamelCaseName() {return toCamelCase(getName());}
	
	private String toCamelCase(String name) {
		return Character.toLowerCase(name.charAt(0)) + name.substring(1);
	}

	public String generateSignature() {
		StringBuilder doc = new StringBuilder("");
		if (null != utils_) {
			if (null != utils_.getDocComment(getComputer())) {
				doc = new StringBuilder("\t/**\n");
				doc.append(utils_.getDocComment(getComputer()));
				doc.append("\n\t*/\n");
			}
		}
		return doc + "\t@" + ThisIsAProperty.class.getCanonicalName() 
			+ "\n\tpublic " + returnType_ + " " + getCamelCaseName() + "();\n\n";
	}
	
	public String generateImpl(String instanceName) {
		StringBuilder impl = new StringBuilder();
		StringBuilder doc = new StringBuilder("");
		if (null != utils_) {
			if (null != utils_.getDocComment(getComputer())) {
				doc = new StringBuilder("\t/**\n");
				doc.append(utils_.getDocComment(getComputer()));
				doc.append("\n\t*/\n");
			}
		}
		impl.append(doc + "\t@" + ThisIsAProperty.class.getCanonicalName() 
				+ "\n\tpublic " + returnType_ + " " + getCamelCaseName() + "() {\n");
		impl.append("\t\treturn " + instanceName + ".compute(this);\n");
		impl.append("\t}\n");
		return impl.toString();
	}

}
