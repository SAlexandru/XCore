package com.salexandru.codeGeneration;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

public class XGroupBuilder {
	private TypeElement builder_;
	private DeclaredType entityType_;
	private DeclaredType elementType_;
	
	public XGroupBuilder (TypeElement builder, DeclaredType entityType, DeclaredType elementType) {
		if (null == builder || null == entityType || null == entityType) {
			throw new IllegalArgumentException();
		}
		builder_ = builder;
		entityType_ = entityType;
		elementType_ = elementType;
	}
	
	public String getName() {
		return builder_.getSimpleName().toString().substring(0, 1).toLowerCase() +
			   builder_.getSimpleName().toString().substring(1);
	}
	
	public TypeElement   getBuilder()     {return builder_;}
	public DeclaredType  getEntityType()  {return entityType_;}
	public DeclaredType  getElementType() {return elementType_;}
}
