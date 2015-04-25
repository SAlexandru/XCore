package com.salexandru.corex.preferencepage;
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
import org.eclipse.swt.graphics.Point;
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
public class MyDialog extends Dialog {
  private Text corexTypeTxt_;
  private Text javaClassTxt_;
  private String corexType_;
  private String javaType_;
  private IJavaProject jProject_;

  public MyDialog(Shell parentShell, IJavaProject jProject) {
    super(parentShell);
    jProject_ = jProject;
  }
  
  public String[] getData() {
	  if (null == corexType_ || null == javaType_ || corexType_.isEmpty() || javaType_.isEmpty()) {
		  return null;
	  }
	  return new String[] {corexType_, javaType_};
  }
  
  public String getCorexType() {return corexType_;}
  public String getJavaType()  {return javaType_;}

  @Override
  protected Control createDialogArea(Composite parent) {
	parent.setLayout(new GridLayout(2, false));
	
	new Label(parent, SWT.NONE).setText("Corex Type: ");
	corexTypeTxt_ = new Text(parent, SWT.BORDER);
	corexTypeTxt_.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	if (null != corexType_) {
		corexTypeTxt_.setText(corexType_);
	}
	
	new Label(parent, SWT.NONE).setText("Java Concrete Type: ");
	
	Composite javaClass = new Composite(parent, SWT.NONE);
	javaClass.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	javaClass.setLayout(new GridLayout(2, false));
	
	javaClassTxt_ = new Text(javaClass, SWT.BORDER);
	javaClassTxt_.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	if (null != javaType_) {
		javaClassTxt_.setText(javaType_);
	}
	
	Button browse = new Button(javaClass, SWT.PUSH);
	browse.setText("browse");
	browse.addSelectionListener(new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			//IJavaElement jdtCore =  JavaCore.create(resource);
			IJavaSearchScope jScope = SearchEngine.createJavaSearchScope(new IJavaElement[] {jProject_});
		
			FilteredTypesSelectionDialog types = new FilteredTypesSelectionDialog(null, false,  null, jScope, IJavaSearchConstants.TYPE);
			types.setTitle("Java search title");
			if (null != javaClassTxt_.getText() && !javaClassTxt_.getText().trim().isEmpty()) {
				try {
					types.setInitialPattern(javaClassTxt_.getText().trim());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			types.open();
			IType t = (IType)types.getFirstResult();
			if (null != t) {
				javaClassTxt_.setText(t.getFullyQualifiedName());
			}
		}
	});

    return parent;
  }
  
  @Override
  protected void okPressed() {
	  try {
		  corexType_ = corexTypeTxt_.getText().trim();
		  javaType_  = javaClassTxt_.getText().trim();
		  
		  if (corexType_.isEmpty() || javaType_.isEmpty()) {
			  showDialogBox("Please enter both corex type and java concrete type");
		  }
		  else {
			  try {
				  IType type = jProject_.findType(javaType_);
				  
				  if (null == type) {
					  showDialogBox("Type " + javaType_ + " doesn't exist");
				  }
				  else {
					  setReturnCode(OK);
					  close();
				  }
			  }
			  catch (Exception e) {
				  showDialogBox("Error occured while accessing the entered java type");
			  }
		  }
	  }
	  catch (NullPointerException e) {
		  corexType_ = "";
		  javaType_ = "";
		  showDialogBox("Please enter both corex type and java concrete type");
	  }
  }
  
  private void showDialogBox(String msg) {
	  MessageBox msb = new MessageBox(getShell(), SWT.ICON_ERROR);
	  msb.setMessage(msg);
	  msb.open();
  }

  // overriding this methods allows you to set the
  // title of the custom dialog
  @Override
  protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    newShell.setText("Binding Entry");
  }

  @Override
  protected Point getInitialSize() {
    return new Point(450, 300);
  }

  public void setCorexType(String text) {
	corexType_ = text;
  }

  public void setJavaType(String text) {
	javaType_ = text;
  }

} 