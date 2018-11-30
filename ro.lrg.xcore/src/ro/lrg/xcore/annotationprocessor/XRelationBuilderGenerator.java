package ro.lrg.xcore.annotationprocessor;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import ro.lrg.xcore.metametamodel.Group;
import ro.lrg.xcore.metametamodel.IRelationBuilder;
import ro.lrg.xcore.metametamodel.ThisIsARelationBuilder;

public class XRelationBuilderGenerator {
	
	private TypeElement builder_;
	private DeclaredType entityType_;
	private DeclaredType elementType_;
	private Elements     utils_;
	
	public XRelationBuilderGenerator (TypeElement builder, Elements utils) {
		this(builder);
		utils_ = utils;
	}
		
	public XRelationBuilderGenerator (TypeElement builder) {
		if (null == builder) {
			throw new NullPointerException();
		}
		
		for (TypeMirror tIntf: builder.getInterfaces()) {
			DeclaredType dTIntf = (DeclaredType)tIntf;
			if (!dTIntf.asElement().getSimpleName().toString().equals(IRelationBuilder.class.getSimpleName())) {
				continue;
			}
			if (2 != dTIntf.getTypeArguments().size()) {
				throw new IllegalArgumentException("IGroupBuilder must have exactely two type arguments");
			}
			entityType_ =  (DeclaredType)dTIntf.getTypeArguments().get(1);
			elementType_ = (DeclaredType)dTIntf.getTypeArguments().get(0);
		}
		
		builder_ = builder;
	}
	
	public void  setElementUtils(Elements utils) {utils_ = utils;}
	public Elements  getElementUtils() {return utils_;}
	
	public String getCamelCaseName() {
		return Character.toLowerCase(builder_.getSimpleName().charAt(0)) +
			   builder_.getSimpleName().toString().substring(1);
	}
	
	public String getName() {return builder_.getSimpleName().toString();}
	
	public String getNameImpl() {return getName() + "Impl";}
	public String getCamelCaseNameImpl() {return getCamelCaseName() + "Impl";}
	
	public String getQualifiedName() {
		return builder_.getQualifiedName().toString();
	}
	
	public TypeElement   getBuilder()     {return builder_;}
	public DeclaredType  getEntityType()  {return entityType_;}
	public DeclaredType  getElementType() {return elementType_;}
	
	public String generateSignature() {
		StringBuilder doc = new StringBuilder("");
		if (null != utils_) {
			if (null != utils_.getDocComment(getBuilder())) {
				doc = new StringBuilder("\t/**\n");
				doc.append(utils_.getDocComment(getBuilder()));
				doc.append("\n\t*/\n");
			}
		}
		
		return doc + "\t@" + ThisIsARelationBuilder.class.getCanonicalName() + "\n\tpublic "
			+ String.format(Group.class.getCanonicalName()
			+ "<%s> %s();\n\n", elementType_.asElement().getSimpleName(), getCamelCaseName());
	}
	
	public String generateImpl(String instanceName) {
		StringBuilder doc = new StringBuilder("");
		if (null != utils_) {
			if (null != utils_.getDocComment(getBuilder())) {
				doc = new StringBuilder("\t/**\n");
				doc.append(utils_.getDocComment(getBuilder()));
				doc.append("\n\t*/\n");
			}
		}
		return doc + String.format("\t@" + ThisIsARelationBuilder.class.getCanonicalName() 
				+ "\n\tpublic " + Group.class.getCanonicalName() + "<%s> %s() {\n\t\treturn %s.buildGroup(this);\n\t}\n", 
				elementType_.asElement().getSimpleName(), getCamelCaseName(), instanceName);
	}
}
