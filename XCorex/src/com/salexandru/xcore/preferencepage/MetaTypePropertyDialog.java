package com.salexandru.xcore.preferencepage;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.ui.dialogs.FilteredTypesSelectionDialog;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

@SuppressWarnings("restriction")
public class MetaTypePropertyDialog extends Dialog {

	private Text xcoreMetaTypeTxt_;
	private Text underlyingMetaTypeTxt_;
	
	private String xcoreMetaType;
	private String underlyingMetaType;
	
	private IJavaProject jProject_;

	public MetaTypePropertyDialog(Shell parentShell, IJavaProject jProject) {
		super(parentShell);
		jProject_ = jProject;
	}
  
	public String[] getData() {
	  if (null == xcoreMetaType || null == underlyingMetaType || xcoreMetaType.isEmpty() || underlyingMetaType.isEmpty()) {
		  return null;
	  }
	  return new String[] {xcoreMetaType, underlyingMetaType};
	}
  
 	public void setXCoreMetaType(String text) {
  		xcoreMetaType = text;
  	}

  	public void setUnderlyingMetaType(String text) {
  		underlyingMetaType = text;
  	}

  	@Override
  	protected Control createDialogArea(Composite parent) {		
  		Composite newParent = new Composite(parent, SWT.NONE);
  		newParent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
  		
  		parent = newParent;
  		parent.setLayout(new GridLayout(3, false));
	
		new Label(parent, SWT.NONE).setText("XCore meta-type:");
		xcoreMetaTypeTxt_ = new Text(parent, SWT.BORDER);
		GridData tmp = new GridData(GridData.FILL_HORIZONTAL);
		tmp.horizontalSpan = 2;
		xcoreMetaTypeTxt_.setLayoutData(tmp);
		if (null != xcoreMetaType) {
			xcoreMetaTypeTxt_.setText(xcoreMetaType);
		}
		xcoreMetaTypeTxt_.setEditable(false);
	
		new Label(parent, SWT.NONE).setText("Underlying meta-type:");	
		underlyingMetaTypeTxt_ = new Text(parent, SWT.BORDER);
		underlyingMetaTypeTxt_.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		if (null != underlyingMetaType) {
			underlyingMetaTypeTxt_.setText(underlyingMetaType);
			underlyingMetaTypeTxt_.setFocus();
		}
		Button browse = new Button(parent, SWT.PUSH);
		browse.setText("Browse");
		browse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IJavaSearchScope jScope = SearchEngine.createJavaSearchScope(new IJavaElement[] {jProject_});		
				FilteredTypesSelectionDialog types = new FilteredTypesSelectionDialog(null, false,  null, jScope, IJavaSearchConstants.TYPE);
				types.setTitle("Search for underlying meta-type");
				if (null != underlyingMetaTypeTxt_.getText() && !underlyingMetaTypeTxt_.getText().trim().isEmpty()) {
					try {
						types.setInitialPattern(underlyingMetaTypeTxt_.getText().trim());
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				types.open();
				IType t = (IType)types.getFirstResult();
				if (null != t) {
					underlyingMetaTypeTxt_.setText(t.getFullyQualifiedName());
				}
			}		
		});

		return parent;
  	}
  
  	@Override
  	protected void okPressed() {
  		try {
  			xcoreMetaType = xcoreMetaTypeTxt_.getText().trim();
  			underlyingMetaType  = underlyingMetaTypeTxt_.getText().trim();
  			if (xcoreMetaType.isEmpty() || underlyingMetaType.isEmpty()) {
  				showDialogBox("All fields must be specified!");
  				return;
  			}
  			IType underType = jProject_.findType(underlyingMetaType);
  			if (null == underType) {
  				showDialogBox("Type " + underlyingMetaType + " doesn't exist");
  				return;
			}
			setReturnCode(OK);
			close();
	  } catch (Exception e) {
		  xcoreMetaType = "";
		  underlyingMetaType = "";
		  showDialogBox("All fields must be specified!");
	  }
  	}
  
  	private void showDialogBox(String msg) {
  		MessageBox msb = new MessageBox(getShell(), SWT.ICON_ERROR);
  		msb.setMessage(msg);
  		msb.open();
  	}

  	@Override
  	protected void configureShell(Shell newShell) {
  		super.configureShell(newShell);
  		newShell.setText("Underlying meta-type binding");
  	}

} 