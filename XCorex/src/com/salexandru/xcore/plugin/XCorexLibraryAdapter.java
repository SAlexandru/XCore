package com.salexandru.xcore.plugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IJavaProject;

public class XCorexLibraryAdapter extends ClasspathContainerInitializer {
	@Override
	public void initialize(IPath containerPath, IJavaProject project)
			throws CoreException {
		
		System.out.println(containerPath);
		System.out.println(project);

	}

}
