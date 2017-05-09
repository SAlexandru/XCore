package com.salexandru.xcore.annotationProcessor;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import com.salexandru.codeGeneration.XPropertyComputerGenerator;
import com.salexandru.codeGeneration.XRelationBuilder;
import com.salexandru.codeGeneration.XMetaModelEntityGenerator;
import com.salexandru.codeGeneration.XActionPreformerGenerator;
import com.salexandru.codeGeneration.XCodeGenerator;
import com.salexandru.xcore.preferencepage.XCorexPropertyPage.XCorePropertyStore;
import com.salexandru.xcore.utils.metaAnnotation.ActionPerformer;
import com.salexandru.xcore.utils.metaAnnotation.RelationBuilder;
import com.salexandru.xcore.utils.metaAnnotation.PropertyComputer;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class XAnnotationProcessor extends AbstractProcessor {
	
	private Set<String> supportedAnnotations_;
	private XCodeGenerator generator_;
	
	public XAnnotationProcessor() {
		super();
		supportedAnnotations_ = new HashSet<>();
		supportedAnnotations_.add(PropertyComputer.class.getCanonicalName());
		supportedAnnotations_.add(RelationBuilder.class.getCanonicalName());
		supportedAnnotations_.add(ActionPerformer.class.getCanonicalName());
	}
	
	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return supportedAnnotations_;
	}
	
	public void printNotice(String msg) {
		processingEnv.getMessager().printMessage(Kind.NOTE, msg);
	}
	
	public void printNotice(Element e, String msg) {
		processingEnv.getMessager().printMessage(Kind.NOTE, msg, e);
	}
	
	public void printWarning (Element e, String msg) {
		processingEnv.getMessager().printMessage(Kind.WARNING, msg, e);
	}
	
	public void printError(Element e, String msg) {
		processingEnv.getMessager().printMessage(Kind.ERROR, msg, e);
	}
	
	public void printError(String msg) {
		processingEnv.getMessager().printMessage(Kind.ERROR, msg);
	}
		
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

		IJavaProject jProject = getJavaProject();

		generator_ = new XCodeGenerator(jProject.getElementName().toLowerCase() + ".metamodel");
		
		if (roundEnv.processingOver()) {
			return true;
		}
		
		processPropertyComputer(roundEnv);
		processGroupBuilder(roundEnv);
		processActionPerform(roundEnv);
	
		if (null != jProject) {
			XCorePropertyStore prop = new XCorePropertyStore(jProject);
			for (XMetaModelEntityGenerator p: generator_.getEntities()) {
				if(prop.getUnderlyingMetaType(p.getName()) == null) {
					prop.setDefaultBindings(p.getName());
				}
				p.setExtendedMetaType(prop.getExtendedMetaType(p.getName()));
				p.setUnderlyingMetaType(prop.getUnderlyingMetaType(p.getName()));
				p.addExtraMetaType(prop.getExtendedTypes(p.getName()));
			}
			
			prop.save(Collections.emptyMap());
		}
		
		try {
			generator_.generate(processingEnv.getFiler());
		}
		catch (IOException e) {
			printError(e.getMessage());
		}
		
		return true;
	}
	
	private void processActionPerform(RoundEnvironment env) {
		for (Element elem: env.getElementsAnnotatedWith(ActionPerformer.class)) {
			if (ElementKind.CLASS != elem.getKind()) {
				printError (elem, "@ActionPerformer must annotate classes!");
			}
			else {
				TypeElement tElem = (TypeElement)elem;
				
				if (tElem.getSimpleName().equals(tElem.getQualifiedName())) {
					printError (elem, "Class must be in a named package not in the default one!");
				}
				else if (!hasDefaultConstructor(tElem)) {
					printError (elem, "The class must have a default constructor!");
				}
				else {
					try {
						XActionPreformerGenerator gen = new XActionPreformerGenerator(tElem);
						processingEnv.getElementUtils().getDocComment(tElem.getEnclosingElement());
						gen.setElementUtils(processingEnv.getElementUtils());
						generator_.createEntity(gen.getEntityType()).addActionPerformer(gen);
					}
					catch (NullPointerException | IllegalArgumentException e) {
						printError(elem, e.getMessage());
					}
				}
				
			}
		}
		
	}

	private IJavaProject getJavaProject() {
		try {
			JavaFileObject jObj = processingEnv.getFiler().createSourceFile("CorexToTest");
			IWorkspace workspace= ResourcesPlugin.getWorkspace();    
			IPath location= Path.fromOSString(jObj.toUri().getPath()); 
			IFile ifile= workspace.getRoot().getFileForLocation(location);
			return JavaCore.create(ifile).getJavaProject();
		}
		catch (Exception e) {
			printError(e.getMessage() + ":\n" + Arrays.deepToString(e.getStackTrace()));
		}
		return null;
	}

	private void processPropertyComputer(RoundEnvironment env) {
		for (Element elem: env.getElementsAnnotatedWith(PropertyComputer.class)) {
			if (ElementKind.CLASS != elem.getKind()) {
				printError (elem, "@PropertyComputer must annotate classes!");
			}
			else {
				TypeElement tElem = (TypeElement)elem;
				
				if (tElem.getSimpleName().equals(tElem.getQualifiedName())) {
					printError (elem, "Class must be in a named package not in the default one!");
					continue;
				}
				if (!hasDefaultConstructor(tElem)) {
					printError (elem, "The class must have a default constructor!");
					continue;
				}
				try {
					XPropertyComputerGenerator computer = new XPropertyComputerGenerator(tElem);
					processingEnv.getElementUtils().getDocComment(tElem.getEnclosingElement());
					computer.setElementUtils(processingEnv.getElementUtils());
					generator_.createEntity(computer.getEntityType()).addComputer(computer);
				}
				catch (NullPointerException | IllegalArgumentException e) {
					printError (elem, e.getMessage());
				}
			}
		}
	}
	
	private void processGroupBuilder(RoundEnvironment env) {
		for (Element elem: env.getElementsAnnotatedWith(RelationBuilder.class)) {
			if (ElementKind.CLASS != elem.getKind()) {
				printError (elem, "@GroupBuilder must annotate classes!");
			}
			else {
				TypeElement tElem = (TypeElement)elem;
				if (tElem.getSimpleName().equals(tElem.getQualifiedName())) {
					printError (elem, "Class must be in a named package not in the default one!");
				}
				if (!hasDefaultConstructor(tElem)) {
					printError (elem, "The class must have a default constructor!");
					continue;
				}
				if (null != tElem.getAnnotation(PropertyComputer.class)) {
					printError (elem, "@PropertyComputer and @GroupBuilder are mutually exclusive!");
					continue;
				}
				try {
					XRelationBuilder gb = new XRelationBuilder ((TypeElement)elem);
					gb.setElementUtils(processingEnv.getElementUtils());
					generator_.createEntity(gb.getEntityType()).addGroupBuilder(gb);
					generator_.createEntity(gb.getElementType());
				}
				catch (IllegalArgumentException e) {
					printError (elem, e.getMessage());
				}
				catch (ClassCastException e) {
					printError (elem, "For IGroupBuilder specify concrete type, not ? [exteds/super T]");
				}
			}
		}
	}
	
	private boolean hasDefaultConstructor(TypeElement elem) {
		int count = 0;
		boolean isOk = false;
		for (Element e: elem.getEnclosedElements()) {
			if (e.getSimpleName().toString().equals("<init>")) {
				continue;
			}
			if (ElementKind.CONSTRUCTOR == e.getKind()) {
				++count;
				ExecutableElement constr = (ExecutableElement)e;
				if (constr.isDefault()) {
					isOk = true;
					break;
				}
			}
		}
		return 0 == count || isOk;
	}
}