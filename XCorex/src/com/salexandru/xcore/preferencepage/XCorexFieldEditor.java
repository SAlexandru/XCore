package com.salexandru.xcore.preferencepage;

import java.util.HashSet;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.salexandru.xcore.interfaces.XEntity;
import com.salexandru.xcore.preferencepage.XCorexPropertyPage.XCorePropertyStore;

public class XCorexFieldEditor extends FieldEditor {

	private IJavaProject thePrj;
	private Table table;
	private Combo extendedMetaModelField;
	private HashSet<String> toRemoveName = new HashSet<String>();

	public XCorexFieldEditor(IJavaProject theProject, Composite parent) {
		thePrj = theProject;
		createControl(parent);
	}

	@Override
	public int getNumberOfControls() {
		return 2;
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		
		GridData tmp;
				
		Label extendedMMGroup = new Label(parent, SWT.NONE);
		extendedMMGroup.setText("Extended meta-model");
		tmp = new GridData();
		tmp.horizontalSpan = 2;
		extendedMMGroup.setLayoutData(tmp);
		Label extendedMetaModelLocationLable = new Label(parent, SWT.NONE);
		extendedMetaModelLocationLable.setText("Package location:");
		extendedMetaModelField = new Combo(parent, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		extendedMetaModelField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		try {
			for(IPackageFragmentRoot pfr : thePrj.getAllPackageFragmentRoots()) {
				for(IJavaElement jepf : pfr.getChildren()) {
					if(jepf.getElementName().startsWith(thePrj.getElementName().toLowerCase())) continue;
					IPackageFragment pf = (IPackageFragment)jepf;
					boolean pfOK = false;
					nextUnit:for(ICompilationUnit cu : pf.getCompilationUnits()) {
						pfOK = true;
						for(IType type : cu.getAllTypes()) {
							boolean typeOK = false;
							for(String aninterf : type.getSuperInterfaceNames()) {
								String[][] resolvedInterface = type.resolveType(aninterf);
								if(resolvedInterface != null && resolvedInterface.length == 1 && (resolvedInterface[0][0]+"."+resolvedInterface[0][1]).equals(XEntity.class.getCanonicalName())) {
									typeOK = true;
								}
							}
							if(!typeOK) {
								pfOK = false;
								break nextUnit;
							}
						}
					}
					if(pfOK) {
						extendedMetaModelField.add(pf.getElementName());
					}
				}
			}
		} catch (JavaModelException e1) {
			e1.printStackTrace();
		}
		
		Label underlyingMMGroup = new Label(parent, SWT.NONE);
		underlyingMMGroup.setText("Underlying meta-model");
		tmp = new GridData();
		tmp.horizontalSpan = 2;
		underlyingMMGroup.setLayoutData(tmp);
		Composite panel = new Composite(parent, SWT.NONE);
		panel.setLayout(new GridLayout());
		Button underlyingMetaModelEdit = new Button(panel, SWT.PUSH);
		underlyingMetaModelEdit.setText("Edit");
		underlyingMetaModelEdit.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Button underlyingMetaModelRemove = new Button(panel, SWT.PUSH);
		underlyingMetaModelRemove.setText("Remove");
		underlyingMetaModelRemove.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		table= new Table(parent, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		table.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableColumn t1 = new TableColumn(table, SWT.LEAD);
		t1.setText("XCore meta-type");
		t1.setResizable(true);
		TableColumn t2 = new TableColumn(table, SWT.LEAD);
		t2.setText("Underlying meta-type");
		t2.setResizable(true);
		TableColumn t3 = new TableColumn(table, SWT.LEAD);
		t3.setText("Extended meta-type");
		t3.setResizable(true);
		TableLayout table_layout = new TableLayout();
		table.setLayout(table_layout);
		table_layout.addColumnData(new ColumnWeightData(0,100));
		table_layout.addColumnData(new ColumnWeightData(0,200));
		table_layout.addColumnData(new ColumnWeightData(0,200));

		underlyingMetaModelEdit.addSelectionListener(new SelectionListener() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] var = table.getSelection();
				if(var.length > 0) {
					if(!var[0].getText(2).equals(XEntity.class.getCanonicalName())) {
				  		MessageBox msb = new MessageBox(parent.getShell(), SWT.ICON_ERROR);
				  		msb.setMessage("The meta-type" + var[0].getText(1) + " cannot be edited because it extends another meta-type i.e., "  + var[0].getText(2));
				  		msb.open();						
						return;
					}
					MetaTypePropertyDialog d = new MetaTypePropertyDialog(null, thePrj);
					d.setXCoreMetaType(var[0].getText(0));
					d.setUnderlyingMetaType(var[0].getText(1));
					d.open();
					if(d.getReturnCode() == Window.OK) {
						var[0].setText(d.getData());				
					}
				}
			}	
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		extendedMetaModelField.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(((Combo)e.widget).getText() != null && !((Combo)e.widget).getText().isEmpty()) {
					String pack = ((Combo)e.widget).getText();
					XCorePropertyStore pp = (XCorePropertyStore) getPreferenceStore();
					String[][] underlayingMappings = pp.toMatrix();
					for(String[] aMetaTypeEntry : underlayingMappings){
						try {
							IType t = thePrj.findType(pack + "." + aMetaTypeEntry[0]);
							if(t != null) {
								for(IMethod am : t.getMethods()) {
									if(am.getElementName().equals("getUnderlyingObject")) {
										pp.setBindings(aMetaTypeEntry[0], Signature.toString(Signature.getReturnType(am.getSignature())), t.getFullyQualifiedName());
									}
								}
							}
						} catch (JavaModelException e1) {
							e1.printStackTrace();
						}
					}				
				}
				doLoad();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		
		parent.addControlListener(new ControlListener() {		
			@Override
			public void controlResized(ControlEvent e) {
				parent.pack();
			}
			
			@Override
			public void controlMoved(ControlEvent e) {}
		});
		
		underlyingMetaModelRemove.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] var = table.getSelection();
				if(var.length > 0) {
					toRemoveName.add(var[0].getText(0));
					table.remove(table.getSelectionIndices());
				}
			}			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		parent.pack();
	}

	@Override
	protected void doLoad() {
		toRemoveName.clear();
		table.removeAll();
		XCorePropertyStore pp = (XCorePropertyStore) getPreferenceStore();
		String[][] mappings = pp.toMatrix();
		for(int i = 0; i < mappings.length; i++) {
			TableItem ti = new TableItem(table, SWT.NONE);
			ti.setText(mappings[i]);
			if(!mappings[i][2].equals(XEntity.class.getCanonicalName())) {
				String txt = mappings[i][2];
				txt = txt.substring(0, txt.lastIndexOf('.'));
				extendedMetaModelField.setText(txt);
			}
		}
	}

	@Override
	protected void doLoadDefault() {
		toRemoveName.clear();
		XCorePropertyStore pp = (XCorePropertyStore) getPreferenceStore();
		for(int i = 0; i < table.getItemCount(); i++) {
			pp.setToDefault(table.getItem(i).getText(0));
		}		
		doLoad();
		extendedMetaModelField.deselectAll();
	}

	@Override
	protected void doStore() {
		XCorePropertyStore pp = (XCorePropertyStore) getPreferenceStore();
		for(int i = 0; i < table.getItemCount(); i++) {
			pp.setBindings(table.getItem(i).getText(0), table.getItem(i).getText(1), pp.getExtendedMetaType(table.getItem(i).getText(0)));
		}
	}

	@Override
	public void store() {
		doStore();
		XCorePropertyStore pp = (XCorePropertyStore) getPreferenceStore();
		pp.doSave(toRemoveName);
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {
		return;
	}
	
}