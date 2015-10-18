package com.salexandru.xcore.preferencepage;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.osgi.service.prefs.BackingStoreException;

/**
 * An abstract field editor that manages a table_ of input values. The editor
 * displays a table_ containing the rows of values, buttons for adding,
 * duplicating and removing rows and buttons to adjust the order of rows in the
 * table_. The table_ also allows in-place editing of values.
 * 
 * <p>
 * Subclasses must implement the <code>parseString</code>,
 * <code>createList</code>, and <code>getNewInputObject</code> framework
 * methods.
 * </p>
 * 
 * @author Sandip V. Chitale
 * 
 * @since 1.0.54
 */
public class XCorexFieldEditor extends FieldEditor {
	/**
	 * The table_ widget; <code>null</code> if none (before creation or after
	 * disposal).
	 */
	private Table table_;

	/**
	 * The button box containing the Add, Remove, Up, and Down buttons;
	 * <code>null</code> if none (before creation or after disposal).
	 */
	private Composite buttonBox_;

	/**
	 * The Add button.
	 */
	private Button addButton_;

	private Button editButton_;

	/**
	 * The Remove button.
	 */
	private Button removeButton_;

	/**
	 * The Up button.
	 */
	private Button upButton_;

	/**
	 * The Down button.
	 */
	private Button downButton_;

	private final String[] columnNames_;
	private final int[] columnWidths_;

	/**
	 * The selection listener.
	 */
	private SelectionListener selectionListener_;

	private Composite parent_;

	private IJavaProject jProject_;

	private IEclipsePreferences pref_;

	/**
	 * Creates a table_ field editor.
	 * 
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param columnNames__
	 *            the names of columns
	 * @param columnWidths_
	 *            the widths of columns
	 * @param parent
	 *            the parent of the field editor's control
	 * 
	 */
	public XCorexFieldEditor(IJavaProject project, Composite parent) {
		columnNames_ = new String[] { "Corex Types", "Java Concrete Type" };
		columnWidths_ = new int[] {150, 150};
		parent_ = parent;
		jProject_ = project;
		pref_ = InstanceScope.INSTANCE.getNode(jProject_.getElementName());
		init("Corex Bindings",
				"Define the type of the underlying object for the corex meta-type. Makes things type-safer ;)");
		createControl(parent_);
		initTable();
	}

	private void initTable() {
		Table table_ = getTableControl(parent_);
		try {
			for (String key : pref_.keys()) {
				String value = pref_.get(key, "Object");
				TableItem item = new TableItem(table_, SWT.NONE);
				item.setText(new String[] { key, value });
			}
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Combines the given list of items into a single string. This method is the
	 * converse of <code>parseString</code>.
	 * <p>
	 * Subclasses must implement this method.
	 * </p>
	 * 
	 * @param items
	 *            the list of items
	 * @return the combined string
	 * @see #parseString
	 */
	protected String createList(String[][] items) {
		StringBuffer sb = new StringBuffer();

		for (String[] rows : items) {
			for (String item : rows) {
				sb.append(item);
				sb.append(',');
			}
			sb.append('/');
		}

		return sb.toString();
	}

	/**
	 * Splits the given string into a array of array of value. This method is
	 * the converse of <code>createList</code>.
	 * <p>
	 * Subclasses must implement this method.
	 * </p>
	 * 
	 * @param string
	 *            the string
	 * @return an array of array of <code>string</code>
	 * @see #createList
	 */
	protected String[][] parseString(String string) {
		String[] rows = string.split("/");
		String[][] items = new String[rows.length][];

		for (int i = 0; i < items.length; ++i) {
			items[i] = rows[i].split(",");
		}

		return items;
	}

	/**
	 * Creates and returns a new value row for the table_.
	 * <p>
	 * Subclasses must implement this method.
	 * </p>
	 * 
	 * @return a new item
	 */
	protected String[] getNewInputObject() {
		MyDialog d = new MyDialog(parent_.getShell(), jProject_);
		d.open();
		if (null != d.getData()) {
			pref_.put(d.getCorexType(), d.getJavaType());
			try {
				pref_.flush();
			} catch (BackingStoreException e) {
				e.printStackTrace();
			}
		}
		return d.getData();
	}

	protected String[] getChangedInputObject(TableItem item) {
		MyDialog d = new MyDialog(parent_.getShell(), jProject_);
		d.setCorexType(item.getText(0));
		d.setJavaType(item.getText(1));
		d.open();
		if (null != d.getData()) {
			pref_.remove(item.getText(0));
			pref_.put(d.getCorexType(), d.getJavaType());
			try {
				pref_.flush();
			} catch (BackingStoreException e) {
				e.printStackTrace();
			}
		}
		return d.getData();
	}

	/**
	 * Creates the Add, Remove, Up, and Down button in the given button box.
	 * 
	 * @param box
	 *            the box for the buttons
	 */
	private void createButtons(Composite box) {
		addButton_ = createPushButton(box, "New");
		editButton_ = createPushButton(box, "Edit");
		removeButton_ = createPushButton(box, "Remove");
		upButton_ = createPushButton(box, "Up");
		downButton_ = createPushButton(box, "Down");
	}

	/**
	 * Return the Add button.
	 * 
	 * @return the button
	 */
	protected Button getAddButton() {
		return addButton_;
	}

	/**
	 * Return the Edit button.
	 * 
	 * @return the button
	 */
	protected Button getEditButton() {
		return editButton_;
	}

	/**
	 * Return the Remove button.
	 * 
	 * @return the button
	 */
	protected Button getRemoveButton() {
		return removeButton_;
	}

	/**
	 * Return the Up button.
	 * 
	 * @return the button
	 */
	protected Button getUpButton() {
		return upButton_;
	}

	/**
	 * Return the Down button.
	 * 
	 * @return the button
	 */
	protected Button getDownButton() {
		return downButton_;
	}

	/**
	 * Helper method to create a push button.
	 * 
	 * @param parent
	 *            the parent control
	 * @param key
	 *            the resource name used to supply the button's label text
	 * @return Button
	 */
	private Button createPushButton(Composite parent, String key) {
		Button button = new Button(parent, SWT.PUSH);
		button.setText(key);
		button.setFont(parent.getFont());
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		int widthHint = convertHorizontalDLUsToPixels(button,
				IDialogConstants.BUTTON_WIDTH);
		data.widthHint = Math.max(widthHint,
				button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		button.setLayoutData(data);
		button.addSelectionListener(getSelectionListener());
		return button;
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void adjustForNumColumns(int numColumns) {
		Control control = getLabelControl();
		((GridData) control.getLayoutData()).horizontalSpan = numColumns;
		((GridData) table_.getLayoutData()).horizontalSpan = numColumns - 1;
	}

	/**
	 * Creates a selection listener.
	 */
	public void createSelectionListener() {
		selectionListener_ = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				Widget widget = event.widget;
				if (widget == addButton_) {
					addPressed();
				} else if (widget == editButton_) {
					editPressed();
				} else if (widget == removeButton_) {
					removePressed();
				} else if (widget == upButton_) {
					upPressed();
				} else if (widget == downButton_) {
					downPressed();
				} else if (widget == table_) {
					selectionChanged();
				}
			}
		};
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		Control control = getLabelControl(parent);
		GridData gd = new GridData();
		gd.horizontalSpan = numColumns;
		control.setLayoutData(gd);

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		gridData.horizontalSpan = 2;
		gridData.widthHint = 550;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(2, false));

		table_ = getTableControl(composite);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalSpan = numColumns - 1;
		gd.grabExcessHorizontalSpace = true;
		table_.setLayoutData(gd);

		buttonBox_ = getButtonBoxControl(composite);
		gd = new GridData();
		gd.verticalAlignment = GridData.BEGINNING;
		buttonBox_.setLayoutData(gd);
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doLoad() {
		if (table_ != null) {
			String s = getPreferenceStore().getString(getPreferenceName());
			String[][] array = parseString(s);
			for (int i = 0; i < array.length; i++) {
				TableItem table_Item = new TableItem(table_, SWT.NONE);
				table_Item.setText(array[i]);
			}
		}
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doLoadDefault() {
		if (table_ != null) {
			table_.removeAll();
			String s = getPreferenceStore().getDefaultString(
					getPreferenceName());
			String[][] array = parseString(s);
			for (int i = 0; i < array.length; i++) {
				TableItem table_Item = new TableItem(table_, SWT.NONE);
				for (int j = 0; j < array[i].length; j++) {
					table_Item.setText(array[i][j]);
				}
			}
		}
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doStore() {
		TableItem[] items = table_.getItems();
		String[][] commands = new String[items.length][];
		for (int i = 0; i < items.length; i++) {
			commands[i] = new String[columnNames_.length];
			TableItem item = items[i];
			for (int j = 0; j < columnNames_.length; j++) {
				commands[i][j] = item.getText(j);
			}
		}
		String s = createList(commands);
		if (s != null) {
			getPreferenceStore().setValue(getPreferenceName(), s);
		}
	}

	/**
	 * Returns this field editor's button box containing the Add, Remove, Up,
	 * and Down button.
	 * 
	 * @param parent
	 *            the parent control
	 * @return the button box
	 */
	public Composite getButtonBoxControl(Composite parent) {
		if (buttonBox_ == null) {
			buttonBox_ = new Composite(parent, SWT.NULL);
			GridLayout layout = new GridLayout();
			layout.marginWidth = 0;
			buttonBox_.setLayout(layout);
			createButtons(buttonBox_);
			buttonBox_.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent event) {
					addButton_ = null;
					editButton_ = null;
					removeButton_ = null;
					upButton_ = null;
					downButton_ = null;
					buttonBox_ = null;
				}
			});

		} else {
			checkParent(buttonBox_, parent);
		}

		selectionChanged();
		return buttonBox_;
	}

	/**
	 * Returns this field editor's table_ control.
	 * 
	 * @param parent
	 *            the parent control
	 * @return the table_ control
	 */
	public Table getTableControl(Composite parent) {
		if (table_ == null) {
			table_ = new Table(parent, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL
					| SWT.H_SCROLL | SWT.FULL_SELECTION);
			table_.setFont(parent.getFont());
			table_.setLinesVisible(true);
			table_.setHeaderVisible(true);
			table_.addSelectionListener(getSelectionListener());
			table_.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent event) {
					table_ = null;
				}
			});
			for (String columnName : columnNames_) {
				TableColumn table_Column = new TableColumn(table_, SWT.LEAD);
				table_Column.setText(columnName);
				table_Column.setWidth(100);
			}
			if (columnNames_.length > 0) {
				TableLayout layout = new TableLayout();
				if (columnNames_.length > 1) {
					for (int i = 0; i < (columnNames_.length - 1); i++) {
						layout.addColumnData(new ColumnWeightData(0,
								columnWidths_[i], false));

					}
				}
				layout.addColumnData(new ColumnWeightData(100,
						columnWidths_[columnNames_.length - 1], true));
				table_.setLayout(layout);
			}
			final TableEditor editor = new TableEditor(table_);
			editor.horizontalAlignment = SWT.LEFT;
			editor.grabHorizontal = true;
			table_.addListener(SWT.MouseDoubleClick, new Listener() {
				public void handleEvent(Event event) {
					Rectangle clientArea = table_.getClientArea();
					Point pt = new Point(event.x, event.y);
					int index = table_.getTopIndex();
					while (index < table_.getItemCount()) {
						boolean visible = false;
						final TableItem item = table_.getItem(index);
						for (int i = 0; i < table_.getColumnCount(); i++) {
							Rectangle rect = item.getBounds(i);
							if (rect.contains(pt)) {
								final int column = i;
								final Text text = new Text(table_, SWT.NONE);
								Listener textListener = new Listener() {
									public void handleEvent(final Event e) {
										switch (e.type) {
										case SWT.FocusOut:
											item.setText(column, text.getText());
											text.dispose();
											break;
										case SWT.Traverse:
											switch (e.detail) {
											case SWT.TRAVERSE_RETURN:
												item.setText(column,
														text.getText());
												// FALL THROUGH
											case SWT.TRAVERSE_ESCAPE:
												text.dispose();
												e.doit = false;
											}
											break;
										}
									}
								};
								text.addListener(SWT.FocusOut, textListener);
								text.addListener(SWT.Traverse, textListener);
								editor.setEditor(text, item, i);
								text.setText(item.getText(i));
								text.selectAll();
								text.setFocus();
								return;
							}
							if (!visible && rect.intersects(clientArea)) {
								visible = true;
							}
						}
						if (!visible)
							return;
						index++;
					}
				}
			});
		} else {
			//checkParent(table_, parent);
		}
		return table_;
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	public int getNumberOfControls() {
		return 2;
	}

	/**
	 * Returns this field editor's selection listener. The listener is created
	 * if necessary.
	 * 
	 * @return the selection listener
	 */
	private SelectionListener getSelectionListener() {
		if (selectionListener_ == null) {
			createSelectionListener();
		}
		return selectionListener_;
	}

	/**
	 * Returns this field editor's shell.
	 * <p>
	 * This method is internal to the framework; subclassers should not call
	 * this method.
	 * </p>
	 * 
	 * @return the shell
	 */
	protected Shell getShell() {
		if (addButton_ == null) {
			return null;
		}
		return addButton_.getShell();
	}

	/**
	 * Notifies that the Add button has been pressed.
	 */
	private void addPressed() {
		setPresentsDefaultValue(false);
		String[] newInputObject = getNewInputObject();
		if (null != newInputObject) {
			TableItem table_Item = new TableItem(table_, SWT.NONE);
			table_Item.setText(newInputObject);
		}
		selectionChanged();
	}

	/**
	 * Notifies that the Add button has been pressed.
	 */
	private void editPressed() {
		setPresentsDefaultValue(false);
		int index = table_.getSelectionIndex();
		TableItem item = table_.getItem(index);
		
		String[] data = getChangedInputObject(item);
		
		if (null != data) {
			item.setText(data);
		}
		
		selectionChanged();
	}

	/**
	 * Notifies that the Remove button has been pressed.
	 */
	private void removePressed() {
		setPresentsDefaultValue(false);
		int index = table_.getSelectionIndex();
		if (index >= 0) {
			try {
				pref_.remove(table_.getItem(index).getText(0));
				pref_.flush();
			}
			catch (BackingStoreException e) {
				e.printStackTrace();
			}
			table_.remove(index);
			selectionChanged();
		}
	}

	/**
	 * Notifies that the Up button has been pressed.
	 */
	private void upPressed() {
		swap(true);
	}

	/**
	 * Notifies that the Down button has been pressed.
	 */
	private void downPressed() {
		swap(false);
	}

	/**
	 * Invoked when the selection in the list has changed.
	 * 
	 * <p>
	 * The default implementation of this method utilizes the selection index
	 * and the size of the list to toggle the enabled state of the up, down and
	 * remove buttons.
	 * </p>
	 * 
	 * <p>
	 * Subclasses may override.
	 * </p>
	 * 
	 */
	protected void selectionChanged() {
		int index = table_.getSelectionIndex();
		int size = table_.getItemCount();

		editButton_.setEnabled(index >= 0 && index < size);
		removeButton_.setEnabled(index >= 0);
		upButton_.setEnabled(size > 1 && index > 0);
		downButton_.setEnabled(size > 1 && index >= 0 && index < size - 1);
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	public void setFocus() {
		if (table_ != null) {
			table_.setFocus();
		}
	}

	/**
	 * Moves the currently selected item up or down.
	 * 
	 * @param up
	 *            <code>true</code> if the item should move up, and
	 *            <code>false</code> if it should move down
	 */
	private void swap(boolean up) {
		setPresentsDefaultValue(false);
		int index = table_.getSelectionIndex();
		int target = up ? index - 1 : index + 1;

		if (index >= 0) {
			TableItem[] selection = table_.getSelection();
			Assert.isTrue(selection.length == 1);
			String[] values = new String[columnNames_.length];
			for (int j = 0; j < columnNames_.length; j++) {
				values[j] = selection[0].getText(j);
			}
			table_.remove(index);
			TableItem table_Item = new TableItem(table_, SWT.NONE, target);
			table_Item.setText(values);
			table_.setSelection(target);
		}
		selectionChanged();
	}

	/*
	 * @see FieldEditor.setEnabled(boolean,Composite).
	 */
	public void setEnabled(boolean enabled, Composite parent) {
		super.setEnabled(enabled, parent);
		getTableControl(parent).setEnabled(enabled);
		addButton_.setEnabled(enabled);
		editButton_.setEnabled(enabled);
		removeButton_.setEnabled(enabled);
		upButton_.setEnabled(enabled);
		downButton_.setEnabled(enabled);
	}

}