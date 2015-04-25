package com.salexandru.corex.preferencepage;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PropertyPage;

import com.salexandru.corex.plugin.Activator;

@SuppressWarnings("restriction")
public class CorexPropertyPage extends PropertyPage implements IWorkbenchPreferencePage {
	@Override
	protected Control createContents(Composite parent) {
		IProject project = getProject();
		
		if (null == project || !isJavaProject(project)) {
			return null;
		}
		
		IJavaProject jProject = JavaCore.create(project);
		Composite corex = new Composite(parent, SWT.NONE);
		new CorexFieldEditor(jProject, corex);
		
		return corex;
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	    setDescription("Corex Bindings definition");
	}
	
	private IProject getProject() {
        IAdaptable adaptable= getElement();
		return adaptable == null ? null : (IProject)adaptable.getAdapter(IProject.class);
	}

	private boolean isJavaProject(IProject proj) {
		try {
			return proj.hasNature(JavaCore.NATURE_ID);
		} catch (CoreException e) {
			JavaPlugin.log(e);
		}
		return false;
	}

}
