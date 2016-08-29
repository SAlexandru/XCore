package com.salexandru.codeGeneration;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

public class XActionPreformerGenerator {
	private TypeElement builder_;
	private DeclaredType entityType_;
	private Elements     utils_;
	
	public XActionPreformerGenerator(TypeElement tElem) {
		// TODO Auto-generated constructor stub
	}

	public TypeMirror getEntityType() {
		return entityType_;
	}

	public void setElementUtils(Elements elementUtils) {
		utils_ = elementUtils;
		
	}
	

}
