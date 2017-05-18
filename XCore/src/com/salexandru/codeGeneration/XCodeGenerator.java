package com.salexandru.codeGeneration;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.type.TypeMirror;

import com.salexandru.xcore.preferencepage.ExtraTypeBinding;
import com.salexandru.xcore.utils.interfaces.XEntity;

public class XCodeGenerator {
	
	private final String entity_package;
	private final String impl_package;
	private final String factory_package; 
	
	private Map<String, XMetaModelEntityGenerator> entities_;
	private String theBasePackage;
	
	public XCodeGenerator(String theBasePackage) {
		entities_ = new HashMap<>();
		this.theBasePackage =  theBasePackage;
		entity_package = theBasePackage + ".entity";
		impl_package = theBasePackage + ".impl";
		factory_package = theBasePackage + ".factory";
	}

	public XMetaModelEntityGenerator createEntity(TypeMirror elem) {
		if (!entities_.containsKey(elem.toString())) {
			entities_.put(elem.toString(), new XMetaModelEntityGenerator(elem,entity_package, impl_package));
		}
		return entities_.get(elem.toString());
	}

	public XMetaModelEntityGenerator getEntity(TypeMirror typeMirror) {
		return entities_.get(typeMirror.toString());
	}
	
	public void generate(Filer filer) throws IOException {
		String superFactory = null;
		for (XMetaModelEntityGenerator pc: entities_.values()) {
			Writer out = filer.createSourceFile(pc.getQualifiedName()).openWriter();
			out.write(pc.toString());
			out.flush();
			out.close();
			if(superFactory == null && pc.isExtension()) {
				superFactory = pc.getExtendedMetaType();
				superFactory = superFactory.substring(0, superFactory.lastIndexOf('.'));
				superFactory = superFactory.substring(0, superFactory.lastIndexOf('.'));
				superFactory = superFactory + ".factory.Factory";
			}
		}
		generateImpl(filer);
		generateFactory(filer,superFactory);
	}
	
	public Collection<XMetaModelEntityGenerator> getEntities() {
		return entities_.values();
	}
	
	private void generateImpl(Filer filer) throws IOException {
		for (XMetaModelEntityGenerator pc: entities_.values()) {
			Writer out = filer.createSourceFile(pc.getQualifiedNameImpl()).openWriter();
			out.write(pc.generateImpl());
			out.flush();
			out.close();
		}
	}
	
	private void generateFactory(Filer filer, String superFactory) throws IOException {
		generateLRUCache(filer);
		Writer out = filer.createSourceFile(factory_package + ".Factory").openWriter();
		out.write("package " + factory_package + ";\n\n");
		out.write("import " + XEntity.class.getCanonicalName() + ";\n");
		out.write("import " + entity_package + ".*;\n");
		out.write("import " + impl_package + ".*;\n");
		out.write("\n");
		
		if(superFactory == null) {
			out.write("public class Factory {\n");
		} else {
			out.write("public class Factory extends " + superFactory + " {\n");			
		}
		
		out.write("   protected static Factory singleInstance = new Factory();\n");
		if(superFactory != null) {
			out.write("   static {" + superFactory + ".singleInstance = singleInstance;}\n");
		}
		out.write("   public static Factory getInstance() { return singleInstance;}\n");
		out.write("   protected Factory() {}\n");
		
		out.write("   private LRUCache<Object, XEntity> lruCache_ = new LRUCache<>(1000);\n");
		out.write("   public void setCacheCapacity(int capacity) {\n");
		out.write("       lruCache_.setCapacity(capacity);\n");
		out.write("   }\n");
		out.write("   public void clearCache() {lruCache_.clear();}\n");
		for (XMetaModelEntityGenerator pc: entities_.values()) {
			out.write(String.format("   public %1$s create%1$s(%2$s obj) {\n", pc.getName(), pc.getUnderlyingType()));
			out.write("       XEntity instance = lruCache_.get(obj);\n");
			out.write("        if (null == instance) {\n");
			out.write("           instance = new " + pc.getNameImpl() + "(obj);\n");
			out.write("           lruCache_.put(obj, instance);\n");
			out.write("        }\n");
			out.write("        return (" + pc.getName() +")instance;\n");
			out.write("    }\n");
			
			for (ExtraTypeBinding binding: pc.getExtraMetaTypes()) {
				out.write(String.format("   public %1$s create%1$s(%2$s obj2) {\n", pc.getName(), binding.getType()));
				out.write(String.format("       %s obj = new %s().reverse(obj2);\n", pc.getUnderlyingType(), binding.getTransformer()));
				out.write("       XEntity instance = lruCache_.get(obj);\n");
				out.write("        if (null == instance) {\n");
				out.write("           instance = new " + pc.getNameImpl() + "(obj);\n");
				out.write("           lruCache_.put(obj, instance);\n");
				out.write("        }\n");
				out.write("        return (" + pc.getName() +")instance;\n");
				out.write("    }\n");
			}
		}
		out.write("}\n");
		out.flush();
		out.close();
	}
	
	private void generateLRUCache(Filer filer) throws IOException {
		Writer out = filer.createSourceFile(theBasePackage + ".factory.LRUCache").openWriter();		
		out.write("package " + factory_package + ";\n\n");
		out.write("import java.util.LinkedHashMap;\n");
		out.write("import java.util.Map.Entry;\n\n");
		out.write("public class LRUCache <K, V> extends LinkedHashMap <K, V> {\n");
		out.write("   private int capacity;\n");
		out.write("   public LRUCache(int capacity) {\n");
		out.write("       super(capacity+1, 1.0f, true);\n");
		out.write("       this.capacity = capacity;\n");
		out.write("   }\n");
		out.write("   public void setCapacity(int capacity) {this.capacity = capacity;}\n");
		out.write("   @Override\n");
		out.write("   protected boolean removeEldestEntry(Entry<K, V> entry) {\n");
		out.write("       return (size() > this.capacity);\n");
		out.write("   }\n");
		out.write("}\n");
		out.flush();
		out.close();
	}
	
}
