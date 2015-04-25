package com.salexandru.corex.annotationProcessor;

import java.io.IOException;
import java.util.Arrays;
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
import org.osgi.service.prefs.BackingStoreException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import com.salexandru.codeGeneration.XComputer;
import com.salexandru.codeGeneration.XGroupBuilder;
import com.salexandru.codeGeneration.XPropertyComputer;
import com.salexandru.codeGeneration.XPropertyGenarator;
import com.salexandru.corex.metaAnnotation.GroupBuilder;
import com.salexandru.corex.metaAnnotation.PropertyComputer;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class XAnnotationProcessor extends AbstractProcessor {
	private Set<String> supportedAnnotations_;
	private XPropertyGenarator xProperties_;
	
	public XAnnotationProcessor() {
		super();
		xProperties_ = new XPropertyGenarator();
		supportedAnnotations_ = new HashSet<>();
		supportedAnnotations_.add(PropertyComputer.class.getCanonicalName());
		supportedAnnotations_.add(GroupBuilder.class.getCanonicalName());
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
	
	public IJavaProject getJavaProject() {
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
	
	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {
		if (roundEnv.processingOver()) {
			return true;
		}
		processPropertyComputer(roundEnv);
		processGroupBuilder(roundEnv);
	
		IJavaProject jProject = getJavaProject();
		if (null != jProject) {
			IEclipsePreferences pref = InstanceScope.INSTANCE.getNode(jProject.getElementName());
			for (XPropertyComputer p: xProperties_.getPropertyComputers()) {
				p.setUnderlyingType(pref.get(p.getName(), "Object"));
				if (null == pref.get(p.getName(), null)) {
					try {
						pref.put(p.getName(), "Object");
						pref.flush();
					}
				    catch (BackingStoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		try {
			xProperties_.generate(processingEnv.getFiler());
		}
		catch (IOException e) {
			printError (e.getMessage());
		}
		
		return true;
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
					XComputer computer = new XComputer(tElem);
					computer.setElementUtils(processingEnv.getElementUtils());
					xProperties_.createPropertyComputer(computer.getEntityType()).addComputer(computer);
				}
				catch (NullPointerException | IllegalArgumentException e) {
					printError (elem, e.getMessage());
				}
			}
		}
	}
	
	private void processGroupBuilder(RoundEnvironment env) {
		for (Element elem: env.getElementsAnnotatedWith(GroupBuilder.class)) {
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
					XGroupBuilder gb = new XGroupBuilder ((TypeElement)elem);
					gb.setElementUtils(processingEnv.getElementUtils());
					xProperties_.createPropertyComputer(gb.getEntityType()).addGroupBuilder(gb);
					xProperties_.createPropertyComputer(gb.getElementType());
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