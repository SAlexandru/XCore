package com.salexandru.xcore.preferencepage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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

import com.salexandru.xcore.preferencepage.XCorexPropertyPage.XCorePropertyStore;
import com.salexandru.xcore.utils.interfaces.XEntity;

public class XCorexFieldEditor extends FieldEditor {

	private IJavaProject thePrj;
	private Table table;
	private Table extraTable;
	private Combo extendedMetaModelField;
	private HashSet<String> toRemoveName = new HashSet<String>();
	private HashMap<String, Set<String>> removeExtra = new HashMap<>();

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
  		parent.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING | GridData.BEGINNING));
  		parent.setLayout(new GridLayout(2, false));
		
		
		GridData tmp;
				
		Label extendedMMGroup = new Label(parent, SWT.NONE);
		extendedMMGroup.setText("Extended meta-model");
		
		tmp = new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING | GridData.BEGINNING);
		tmp.horizontalSpan = 2;
		//tmp.horizontalAlignment = SWT.LEFT;
		//tmp.verticalAlignment = SWT.BEGINNING;
		
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
		
		tmp = new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING | GridData.BEGINNING);
		tmp.horizontalSpan = 2;
		//tmp.horizontalAlignment = SWT.LEFT;
		//tmp.verticalAlignment = SWT.BEGINNING;
		
		underlyingMMGroup.setLayoutData(tmp);
		Composite panel = new Composite(parent, SWT.NONE);
		panel.setLayout(new GridLayout());
		Button underlyingMetaModelEdit = new Button(panel, SWT.PUSH);
		underlyingMetaModelEdit.setText("Edit");
		underlyingMetaModelEdit.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Button underlyingMetaModelRemove = new Button(panel, SWT.PUSH);
		underlyingMetaModelRemove.setText("Remove");
		underlyingMetaModelRemove.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		table= new Table(parent, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.LEFT_TO_RIGHT | SWT.LEFT);
		table.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING ));
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
				
					for(Entry<String, Entry<String, String>> entry: pp.loadOriginal().entrySet()){
						String meta = entry.getKey();
						
						try {
							IType t = thePrj.findType(pack + "." + meta);
							if(t != null) {
								for(IMethod am : t.getMethods()) {
									if(am.getElementName().equals("getUnderlyingObject")) {
										pp.setExternalMetaType(meta, t.getFullyQualifiedName());
										pp.setUnderlyingMetaType(meta,Signature.toString(Signature.getReturnType(am.getSignature())));
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
		
		
		Label extraUnderlyingMMGroup = new Label(parent, SWT.NONE);
		extraUnderlyingMMGroup.setText("Extra Underlying meta-models");
		
		tmp = new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING | GridData.BEGINNING);
		tmp.horizontalSpan = 2;
		//tmp.horizontalAlignment = SWT.LEFT;
		//tmp.verticalAlignment = SWT.BEGINNING;
		
		extraUnderlyingMMGroup.setLayoutData(tmp);
		
		panel = new Composite(parent, SWT.NONE);
		panel.setLayout(new GridLayout());
		
		Button extraUnderlyingMetaModelAdd = new Button(panel, SWT.PUSH);
		extraUnderlyingMetaModelAdd.setText("Add New Meta-Model Type");
		extraUnderlyingMetaModelAdd.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Button extraUnderlyingMetaModelEdit = new Button(panel, SWT.PUSH);
		extraUnderlyingMetaModelEdit.setText("Edit");
		extraUnderlyingMetaModelEdit.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Button extraUnderlyingMetaModelRemove = new Button(panel, SWT.PUSH);
		extraUnderlyingMetaModelRemove.setText("Remove");
		extraUnderlyingMetaModelRemove.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		extraTable = new Table(parent, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.LEFT_TO_RIGHT | SWT.LEFT);
		extraTable.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING ));
		extraTable.setHeaderVisible(true);
		extraTable.setLinesVisible(true);
		
		TableColumn c1 = new TableColumn(extraTable, SWT.LEAD);
		c1.setText("XCore meta-type");
		c1.setResizable(true);
		
		TableColumn c2 = new TableColumn(extraTable, SWT.LEAD);
		c2.setText("New Underlying meta-type");
		c2.setResizable(true);
		
		TableColumn c3 = new TableColumn(extraTable, SWT.LEAD);
		c3.setText("Transformer");
		c3.setResizable(true);
		
		
		TableLayout extra_table_layout = new TableLayout();
		extraTable.setLayout(extra_table_layout);
		extra_table_layout.addColumnData(new ColumnWeightData(0,100));
		extra_table_layout.addColumnData(new ColumnWeightData(0,200));
		extra_table_layout.addColumnData(new ColumnWeightData(0,200));
		
		extraUnderlyingMetaModelAdd.addSelectionListener(new SelectionListener() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem item = new TableItem(extraTable, 3);
				XCorePropertyStore store = (XCorePropertyStore) getPreferenceStore();
				
				ExtraMetaTypePropertyDialog d = new ExtraMetaTypePropertyDialog(null, thePrj, store.loadUnderlying());
				
				d.open();
				if(d.getReturnCode() == Window.OK) {
					item.setText(d.getData());				
				}
			}	
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		
		extraUnderlyingMetaModelEdit.addSelectionListener(new SelectionListener() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] var = extraTable.getSelection();
				XCorePropertyStore store = (XCorePropertyStore) getPreferenceStore();
					
				ExtraMetaTypePropertyDialog d = new ExtraMetaTypePropertyDialog(null, thePrj, var[0].getText(0).trim(), store.getUnderlyingMetaType(var[0].getText(0).trim()));
					
				d.open();
				if (d.getReturnCode() == Window.OK) {
					var[0].setText(d.getData());				
				}
			}	
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		
		extraUnderlyingMetaModelRemove.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] var = extraTable.getSelection();
				if(var.length > 0) {
					String key = var[0].getText(0);
					if (!removeExtra.containsKey(key)) {
						removeExtra.put(key, new HashSet<>());
					}
					
					removeExtra.get(key).add(var[0].getText(1));
					extraTable.remove(extraTable.getSelectionIndices());
				}
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
		
		
		parent.pack();
	}

	@Override
	protected void doLoad() {
		toRemoveName.clear();
		removeExtra.clear();
		table.removeAll();
		extraTable.removeAll();
		
		for (ExtraBinding binding: ((XCorePropertyStore) getPreferenceStore()).loadAll() ) {
			List<IBinding> items = binding.getBindings();
			
			final String key = binding.getEntity();
			final String underlying = items.get(0).getType();
			final String extraModel = items.get(1).getType();
			
			
			TableItem tableBinding = new TableItem(table, SWT.NONE);
			tableBinding.setText(new String[] {key, underlying, extraModel});
			
			items = items.subList(2, items.size());
			
			if (!items.isEmpty()) {
				for (IBinding type: items) {
					TableItem extraBinding = new TableItem(extraTable, SWT.NONE);
					extraBinding.setText(new String[] {key, type.getType(), ((ExtraTypeBinding)type).getTransformer()});
				}
			}
		}
	}

	@Override
	protected void doLoadDefault() {
		toRemoveName.clear();
		removeExtra.clear();
		XCorePropertyStore pp = (XCorePropertyStore) getPreferenceStore();
		for(int i = 0; i < table.getItemCount(); i++) {
			pp.setToDefault(table.getItem(i).getText(0));
		}		
		
		doLoad();
		extendedMetaModelField.deselectAll();
	}

	@Override
	protected void doStore() {
		Map<String, List<IBinding>> stored = new HashMap<>();
		XCorePropertyStore pp = (XCorePropertyStore) getPreferenceStore();
		
		for (int i = 0; i < table.getItemCount(); ++i) {
			stored.put(table.getItem(i).getText(0), new ArrayList<>());
			stored.get(table.getItem(i).getText(0)).add(new SimpleBinding(table.getItem(i).getText(1)));
			stored.get(table.getItem(i).getText(0)).add(new SimpleBinding(table.getItem(i).getText(2)));
		}
		
		for (TableItem item: extraTable.getItems()) {
			stored.get(item.getText(0)).add(new ExtraTypeBinding(item.getText(1), item.getText(2)));
		}
		
		for (Entry<String, List<IBinding>> entry: stored.entrySet()) {
			pp.setBinding(entry.getKey(),  entry.getValue().toArray(new IBinding[entry.getValue().size()]));
		}
	}

	@Override
	public void store() {
		doStore();
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {
		return;
	}
	
}