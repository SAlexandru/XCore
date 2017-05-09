package com.salexandru.xcore.preferencepage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.ui.IWorkbenchPropertyPage;

import com.salexandru.xcore.utils.interfaces.XEntity;

public class XCorexPropertyPage extends FieldEditorPreferencePage implements IWorkbenchPropertyPage {
	
	private IJavaProject theProject;
	
	public XCorexPropertyPage() {
	    setDescription("Properties of the generated meta-model");
	}
		
	@Override
	protected void createFieldEditors() {
		addField(new XCorexFieldEditor(theProject, getFieldEditorParent()));
	}

	@Override
	public IAdaptable getElement() {
		return theProject;
	}

	@Override
	public void setElement(IAdaptable element) {
		theProject = (IJavaProject)element;
		setPreferenceStore(new XCorePropertyStore(theProject));
	}

	public static class XCorePropertyStore extends PreferenceStore {
		private IJavaProject prj;
		
		private static final String pageId = "com.salexandru.xcore.XCorePropertyPage";
		private static final String pageName = "XCore bindings definition";
		
		public XCorePropertyStore(IJavaProject prj) {
			this.prj = prj;
			load();
		}
		
		public void load() {
			String value;
			try {
				value = prj.getResource().getPersistentProperty(new QualifiedName(pageId, pageName));
				
				if (null == value || value.trim().isEmpty()) return;
				
				for (String entry: value.split(";")) {
					final ExtraBinding bindings = ExtraBinding.parse(entry);
					super.setValue(bindings.getEntity(), bindings.getBindingsAsString());
					super.setDefault(bindings.getEntity(), Object.class.getCanonicalName() + "," + XEntity.class.getCanonicalName());
				}
				
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		
		public String getExtendedMetaType(String entity) {
			if (null == super.getString(entity) || super.getString(entity).trim().isEmpty()) {
				return null;
			}
			final ExtraBinding bindings = ExtraBinding.parse(entity, super.getString(entity));
			return bindings.getBindings().size() < 2 ? null : bindings.getBindings().get(1).getType();
		}

		public String getUnderlyingMetaType(String entity) {
			if (null == super.getString(entity) || super.getString(entity).trim().isEmpty()) {
				return null;
			}
			final ExtraBinding bindings = ExtraBinding.parse(entity, super.getString(entity));
			return bindings.getBindings().isEmpty() ? null : bindings.getBindings().get(0).getType();
		}
		
		public Map<String, Entry<String, String>> loadOriginal() {
			Map<String, Entry<String, String>> m = new HashMap<>();
			
			for (String entry: super.preferenceNames()) {
				m.put(entry, new Map.Entry<String, String>() {

					@Override
					public String getKey() {
						return getUnderlyingMetaType(entry);
					}

					@Override
					public String getValue() {
						return getExtendedMetaType(entry);
					}

					@Override
					public String setValue(String value) {
						return null;
					}
				});
			}
			
			return m;
		}
		
		public String[][] toMatrix() {
			String[] allNames = super.preferenceNames();
			String[][] matrix = new String[allNames.length][3];
			
			for (int i = 0; i < allNames.length; ++i) {
				matrix[i][0] = allNames[i];
				matrix[i][1] = getUnderlyingMetaType(allNames[i]);
				matrix[i][1] = getExtendedMetaType(allNames[i]);
			}
			
			return matrix;
		}
		
		public void save() {  save(Collections.emptyMap()); }
		public void save(Map<String, Set<String>> removed) {
			final StringBuffer buffer = new StringBuffer();
			for (String entity: super.preferenceNames()) {
				if (removed.containsKey(entity)) {
					final ExtraBinding bindings = ExtraBinding.parse(entity, super.getString(entity));
					final ExtraBinding updateBindings = new ExtraBinding(entity);
					
					for (IBinding b: bindings.getBindings()) {
						if (!removed.containsKey(b.getType())) {
							updateBindings.addBinding(b);
						}
					}
					
					super.setValue(entity, updateBindings.getBindingsAsString());
					buffer.append(String.format("%s,%s;", entity, updateBindings.getBindingsAsString()));
				}
				else {
					buffer.append(String.format("%s,%s;", entity, super.getString(entity)));
				}
			}
			try {
				prj.getResource().setPersistentProperty(new QualifiedName(pageId, pageName), buffer.toString());
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void setBinding(String entity, IBinding... bindings) {
		    if (null == bindings || 0 == bindings.length) {
		      super.setValue(entity, Object.class.getCanonicalName() + "," + XEntity.class.getCanonicalName());
		      super.setDefault(entity, Object.class.getCanonicalName() + "," + XEntity.class.getCanonicalName());
		    }
		    else {
		      String[] s = new String[bindings.length];
		      
		      for (int i = 0; i < bindings.length; ++i) {
		        s[i] = bindings[i].toString();
		      }
		      
		      super.setValue(entity, String.join(",", s));
		    }
		}
		
		public void setDefaultBindings(String entity) {
			super.setDefault(entity, Object.class.getCanonicalName() + "," + XEntity.class.getCanonicalName());
			super.setToDefault(entity);
		}

		public List<ExtraTypeBinding> getExtendedTypes(String entity) {
			if (null == super.getString(entity) || super.getString(entity).trim().isEmpty()) {
				return Collections.emptyList();
			}
			
			final ExtraBinding bindings = ExtraBinding.parse(entity, super.getString(entity));
			final List<ExtraTypeBinding> ans = new ArrayList<>();
			
			for (IBinding b: bindings.getBindings()) {
				if (b instanceof ExtraTypeBinding) {
					ans.add((ExtraTypeBinding)b);
				}
			}
			
			return ans;
		}

		public void setExternalMetaType(String entity, String fullyQualifiedName) {
			final ExtraBinding bindings = ExtraBinding.parse(entity, super.getString(entity));
			bindings.replaceBindingAt(1, new SimpleBinding(fullyQualifiedName));
		}

		public void setUnderlyingMetaType(String entity, String fullyQualifiedName) {
			final ExtraBinding bindings = ExtraBinding.parse(entity, super.getString(entity));
			bindings.replaceBindingAt(0, new SimpleBinding(fullyQualifiedName));	
		}

		public Map<String, String> loadUnderlying() {
			Map<String, String> m = new HashMap<>();
			
			for (String entry: super.preferenceNames()) {
				m.put(entry, getUnderlyingMetaType(entry));
			}
			
			return m;
		}

		public List<ExtraBinding> loadAll() {
			final List<ExtraBinding> list = new ArrayList<>();
			
			for (String entry: super.preferenceNames()) {
				list.add(ExtraBinding.parse(entry, super.getString(entry)));
			}
			
			return list;
		}
		
	}
}
