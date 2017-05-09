package com.salexandru.xcore.preferencepage;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.ui.dialogs.FilteredTypesSelectionDialog;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.salexandru.xcore.utils.interfaces.ITransform;

@SuppressWarnings("restriction")
public class ExtraMetaTypePropertyDialog extends Dialog {

	private Text selectedXcoreMetaTypeTxt_; //hidden
	private Text underlyingMetaTypeTxt_;
	private Text originalUnderlyingMetaTypeTxt_;
	private Text transformerFromOriginalUnderlyingMetaTypeTxt_;
	
	private String xcoreMetaType_;
	private String originalUnderlyingMetaType_;
	private String underlyingMetaType_;
	private String transformerFromOriginalUnderlyingMetaType_;
	
	private Map<String, String> originalEntityTypes_;
	
	private IJavaProject jProject_;

	public ExtraMetaTypePropertyDialog(Shell parentShell, IJavaProject jProject, Map<String, String> originalEntityTypes) {
		super(parentShell);
		jProject_ = jProject;
		
		originalEntityTypes_ = originalEntityTypes;
	}
	
	public ExtraMetaTypePropertyDialog(Shell parentShell, IJavaProject jProject, String xcoreMetaType, String originalUnderlyingMetaType) {
		super(parentShell);
		jProject_ = jProject;
		
		if (null == xcoreMetaType || xcoreMetaType.trim().isEmpty() || null == originalUnderlyingMetaType || originalUnderlyingMetaType.trim().isEmpty()) {
			throw new IllegalArgumentException("XcoeMetaType: " +  xcoreMetaType_ + " Original: " + originalUnderlyingMetaType_);
		}
		
		xcoreMetaType_ = xcoreMetaType.trim();
		originalUnderlyingMetaType_ = originalUnderlyingMetaType.trim();
		originalEntityTypes_ = new HashMap<>();
	}
  
	public String[] getData() {
	  if (null == xcoreMetaType_ || null == underlyingMetaType_ || xcoreMetaType_.isEmpty() || underlyingMetaType_.isEmpty() || transformerFromOriginalUnderlyingMetaType_ == null || transformerFromOriginalUnderlyingMetaType_.isEmpty()) {
		  return null;
	  }
	  return new String[] {xcoreMetaType_.trim(), underlyingMetaType_.trim(), transformerFromOriginalUnderlyingMetaType_.trim()};
	}
	
	

  	@Override
  	protected Control createDialogArea(Composite parent) {		
  		if ((null == xcoreMetaType_ || xcoreMetaType_.isEmpty()) && originalEntityTypes_.isEmpty()) {
  			showDialogBox("There is currently no Xcore Entity in your project. Please run the annotation processor first and come back later!");
  			close();
  			return parent;
  		}
  		
  
  		parent.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING | GridData.BEGINNING));
  		parent.setLayout(new GridLayout(2, false));
  		
  		if (null == xcoreMetaType_) {
	  		/**
	  		 *  Create a drop-down menu for the xcore meta-type
	  		 */
			final Composite btnCntrl = new Composite(parent, SWT.BORDER);
	        btnCntrl.setBackgroundMode(SWT.INHERIT_FORCE);
	        
	        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).spacing(0, 1).applyTo(btnCntrl);
	        CLabel lbl = new CLabel(btnCntrl, SWT.NONE);
	        lbl.setText("XCore meta-type:");
	        
	        final Button btn = new Button(btnCntrl, SWT.FLAT | SWT.ARROW | 	SWT.DOWN);
	        btn.setLayoutData(new GridData(GridData.FILL_BOTH));
	        
	        final Composite p = parent;
	        
	        btn.addSelectionListener(new SelectionAdapter() {
	            @Override
	            public void widgetSelected(SelectionEvent e) {
	                super.widgetSelected(e);
	                
	                Menu menu = new Menu(p.getShell(), SWT.POP_UP);
	                for (Entry<String, String> entry: originalEntityTypes_.entrySet()) {
	                	MenuItem item = new MenuItem(menu, SWT.PUSH);
	                    item.setText(entry.getKey());
	                    item.addSelectionListener(new SelectionAdapter() {
	                    	 @Override
	                         public void widgetSelected(SelectionEvent e) {
	                    		 super.widgetSelected(e);
	                    		 lbl.setText(item.getText().trim());
	                    		 selectedXcoreMetaTypeTxt_.setText(item.getText().trim());
	                    		 originalUnderlyingMetaTypeTxt_.setText(entry.getValue().trim());
	                    	 }
	                    });
	                }

	                Point loc = btnCntrl.getLocation();
	                Rectangle rect = btnCntrl.getBounds();

	                Point mLoc = new Point(loc.x-1, loc.y+rect.height);

	                menu.setLocation(p.getDisplay().map(btnCntrl.getParent(), null, mLoc));
	                menu.setVisible(true);      
	                menu.setEnabled(true);
	            }
	        });
  		}
  		else {
  			new Label(parent, SWT.NONE).setText("XCore meta-type:");	
  		}
		
		selectedXcoreMetaTypeTxt_ = new Text(parent, SWT.NONE | SWT.NO_BACKGROUND | SWT.NO_FOCUS);
		selectedXcoreMetaTypeTxt_.setVisible(null != xcoreMetaType_);
		selectedXcoreMetaTypeTxt_.setEditable(false);
		if (null != xcoreMetaType_) selectedXcoreMetaTypeTxt_.setText(xcoreMetaType_);
		
		new Label(parent, SWT.NONE).setText("Original Underlying meta-type:");	
		originalUnderlyingMetaTypeTxt_ = new Text(parent, SWT.BORDER);
		originalUnderlyingMetaTypeTxt_.setEditable(false);
		originalUnderlyingMetaTypeTxt_.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		if (null != originalUnderlyingMetaType_) originalUnderlyingMetaTypeTxt_.setText(originalUnderlyingMetaType_);
        
		
		new Label(parent, SWT.NONE).setText("Underlying Meta-Type:");	
		underlyingMetaTypeTxt_ = new Text(parent, SWT.BORDER);
		underlyingMetaTypeTxt_.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		if (null != underlyingMetaType_) {
			underlyingMetaTypeTxt_.setText(underlyingMetaType_.trim());
			underlyingMetaTypeTxt_.setFocus();
		}
		
		Button browse = new Button(parent, SWT.PUSH);
		browse.setText("Browse");
		browse.setLayoutData(new GridData(GridData.FILL_BOTH));
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
				if (null == t || t.getFullyQualifiedName().equalsIgnoreCase(originalUnderlyingMetaTypeTxt_.getText().trim())) {
					showDialogBox("Original Meta Type and this are identical (or provided meta type is null), please provide another !");
				}
				else if (null != t) {
					underlyingMetaTypeTxt_.setText(t.getFullyQualifiedName().trim());
				}
			}		
		});
		
		transformerFromOriginalUnderlyingMetaTypeTxt_ = new Text(parent, SWT.BORDER);
		transformerFromOriginalUnderlyingMetaTypeTxt_.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Button browse2 = new Button(parent, SWT.PUSH);
		browse2.setText("Find Transformer");
		browse2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String selectedType = underlyingMetaTypeTxt_.getText().trim();
				String originalType = originalUnderlyingMetaTypeTxt_.getText().trim();
				
				if (null == selectedType || null == originalType || originalType.isEmpty() || selectedType.isEmpty()) {
					showDialogBox("Please select a meta type and it's new underlying type.");
				}
				else {
					try {
						final IType transformType = jProject_.findType(ITransform.class.getCanonicalName());
						IJavaSearchScope jScope = SearchEngine.createHierarchyScope(transformType);
						
						FilteredTypesSelectionDialog types = new FilteredTypesSelectionDialog(null, false,  null, jScope, IJavaSearchConstants.CLASS);
						types.setTitle("Search for a transformer for the underlying meta-type");
			
						types.setInitialPattern("*Transform*");
						
						types.open();
						IType t = (IType)types.getFirstResult();
							
							
						if (null != t) {
							transformerFromOriginalUnderlyingMetaTypeTxt_.setText(t.getFullyQualifiedName());
						}
					} catch (JavaModelException e2) {
						e2.printStackTrace();
						throw new IllegalArgumentException("Couldn't find type ITransform", e2);
					} catch(Exception ee) { ee.printStackTrace(); }
				}
			}		
		});
		

		return parent;
  	}
  
  	@Override
  	protected void okPressed() {
	  		try {
	  			xcoreMetaType_ = selectedXcoreMetaTypeTxt_.getText().trim();
	  			underlyingMetaType_  = underlyingMetaTypeTxt_.getText().trim();
	  			transformerFromOriginalUnderlyingMetaType_ = transformerFromOriginalUnderlyingMetaTypeTxt_.getText().trim();
	  			if (xcoreMetaType_.isEmpty() || underlyingMetaType_.isEmpty()) {
	  				showDialogBox("All fields must be specified!");
	  				return;
	  			}
	  			
	  			if (null == jProject_.findType(underlyingMetaType_)) {
	  				showDialogBox("Type " + underlyingMetaType_ + " doesn't exist");
	  				return;
				}
				setReturnCode(OK);
				close();
		  } catch (Exception e) {
			  xcoreMetaType_ = "";
			  underlyingMetaType_ = "";
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
  			newShell.setText("Multiple Underlying meta-type binding");
  	}
} 
