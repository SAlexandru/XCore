package com.salexandru.codeGeneration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleTypeVisitor8;

import com.salexandru.xcore.utils.interfaces.IActionPerformer;
import com.salexandru.xcore.utils.interfaces.TListEmpty;


public class XActionPreformerGenerator {
	private TypeElement action_;
	private DeclaredType entityType_;
	private DeclaredType returnType_;
	private List<String> argumentTypes_;
	private Elements     utils_;
	
	public XActionPreformerGenerator (TypeElement action, Elements utils) {
		this(action);
		utils_ = utils;
	}
	
	public XActionPreformerGenerator(TypeElement action) {
		if (action == null) {
			throw new NullPointerException();
		}
		
		for (TypeMirror tIntf: action.getInterfaces()) {
			DeclaredType dTIntf = (DeclaredType)tIntf;
			if (!dTIntf.asElement().getSimpleName().toString().equals(IActionPerformer.class.getSimpleName())) {
				continue;
			}
			if (3 != dTIntf.getTypeArguments().size()) {
				throw new IllegalArgumentException("IActionPerformer must have exactely three type arguments");
			}
			
			argumentTypes_ = dTIntf.getTypeArguments().get(2).accept(new ExtractHList(), null);
			entityType_ =  (DeclaredType)dTIntf.getTypeArguments().get(1);
			returnType_ =  (DeclaredType)dTIntf.getTypeArguments().get(0);
		}
		
		action_ = action;	
	}
	
	//this is a new type of recursivity --- recursive visitor pattern =)))
	private static final class ExtractHList extends SimpleTypeVisitor8<List<String>, Void> {
		
		@Override
		public List<String> visitDeclared(DeclaredType t, Void v) {
			List<String> typeNames = new ArrayList<>();
			List<? extends TypeMirror> types = t.getTypeArguments();
			
			if (2 == types.size()) {
				String type = types.get(0).toString();
				
				typeNames.add(type);
				
				if (!types.get(1).toString().equals(TListEmpty.class.getCanonicalName())) {
				  typeNames.addAll(types.get(1).accept(new ExtractHList(), null));
				}
			}
			
			return typeNames;
		}
	
	}
	
	public void  setElementUtils(Elements utils) {utils_ = utils;}
	public Elements  getElementUtils() {return utils_;}
	
	public String getCamelCaseName() {
		return Character.toLowerCase(action_.getSimpleName().charAt(0)) +
			   action_.getSimpleName().toString().substring(1);
	}
	
	public String getName() {return action_.getSimpleName().toString();}
	
	public String getNameImpl() {return getName() + "Impl";}
	public String getCamelCaseNameImpl() {return getCamelCaseName() + "Impl";}
	
	public String getQualifiedName() {
		return action_.getQualifiedName().toString();
	}
	
	public TypeElement   getAction()     {return action_;}
	public DeclaredType  getReturnType()  {return returnType_;}
	public DeclaredType  getEntityType()  {return entityType_;}
	public List<String>  getArgumentTypes() {return Collections.unmodifiableList(argumentTypes_);}
	
	private String returnTypeAsString() {
		String t = returnType_.toString();
		
		if (Void.class.getName().equals(t) || Void.class.getCanonicalName().equals(t)) {
			t = "void";
		}
		
		return t;
	}

	public String generateSignature() {
		StringBuilder doc = new StringBuilder("");
		if (null != utils_) {
			if (null != utils_.getDocComment(getAction())) {
				doc = new StringBuilder("/**\n");
				doc.append(utils_.getDocComment(getAction()));
				doc.append("\n*/\n");
			}
		}
		
		int i = 0;
		StringBuilder builder = new StringBuilder();
		for (String type: argumentTypes_) {
		   builder.append(type + " args" + i);	
		   ++i;
		   if (i < argumentTypes_.size()) {
			   builder.append(',');
		   }
		}
		
		return doc + String.format("@ThisIsAnAction public %s %s (%s);\n", returnTypeAsString(), getCamelCaseName(), builder);
	}
	
	public String generateImpl(String instanceName) {
		StringBuilder doc = new StringBuilder("");
		if (null != utils_) {
			if (null != utils_.getDocComment(getAction())) {
				doc = new StringBuilder("/**\n");
				doc.append(utils_.getDocComment(getAction()));
				doc.append("\n*/\n");
			}
		}
		
		StringBuilder builder = new StringBuilder(), arguments = new StringBuilder();
		builder.append("new HList<>(");
		
		String t = returnTypeAsString();
		String returnOrNot = "void".equals(t) ? "" : "return";
		
		if (argumentTypes_.isEmpty()) {
			return doc + String.format(
					"\t@ThisIsAnAction public %s %s() {\n\t\t%s %s.performAction(this, %s.getInstance());\n\t}", 
					returnTypeAsString(), 
					getCamelCaseName(),
					returnOrNot,
					instanceName,
					TListEmpty.class.getCanonicalName()
				   );
		}
		else {
			for (int i = 1; i < argumentTypes_.size(); ++i) {
			   builder.append("args" + (i - 1) + ",");	
			   builder.append("new HList<>(");
			}
			
			
			builder.append("args" + (argumentTypes_.size() - 1) + "," + TListEmpty.class.getCanonicalName() + ".getInstance()");
			
			for (int i = 0; i < argumentTypes_.size(); ++i) {
				builder.append(")");
			}
			
			int argsIdx = 0;
			for (String type: argumentTypes_) {
				arguments.append(type + " args" + argsIdx);
				++argsIdx;
				if (argsIdx < argumentTypes_.size()) {
					arguments.append(',');
				}
			}
		}
		
		return doc + String.format(
				"\t@ThisIsAnAction public %s %s(%s) {\n\t\t%s %s.performAction(this, %s);\n\t}", 
				returnTypeAsString(), 
				getCamelCaseName(),
				arguments,
				returnOrNot,
				instanceName,
				builder 
			   );
	}
}
