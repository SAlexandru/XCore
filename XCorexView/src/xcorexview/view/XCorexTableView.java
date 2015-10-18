package xcorexview.view;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import xmetamodel.XClass;
import xmetamodel.XMethod;
import xmetamodel.factory.FactoryMethod;

import com.salexandru.xcore.interfaces.Group;
import com.salexandru.xcore.interfaces.XEntity;

public class XCorexTableView extends ViewPart {
	private TableViewer viewer_;
	private Composite parent_;
	private Stack<List<XEntity>> dataHistory_ = new Stack<>();
	private Stack<List<String>> propertyHistory_ = new Stack<>();
	
	private static int MAX = 12;
	public static String viewId = "com.salexandru.xcorexview.view.XCorexTableView";
	
	@Override
	public void createPartControl(Composite parent) {
		parent_ = parent;
		viewer_ = new TableViewer(parent_, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		
		viewer_.setContentProvider(new IStructuredContentProvider() {
			@Override
			public void dispose() {	}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput,Object newInput) {}

			@Override
			public Object[] getElements(Object inputElement) {
				return dataHistory_.isEmpty() ? new Object[0]: dataHistory_.peek().toArray();
			}
		});
		
		for (int i = 0; i < MAX; ++i) {
			TableViewerColumn column = new TableViewerColumn(viewer_, SWT.NONE);
			column.getColumn().setMoveable(true);
			column.getColumn().setResizable(true);
			column.getColumn().setText("");
			column.getColumn().setWidth(100);			
		}
		
		viewer_.setLabelProvider(new CorexLabelProvider());
		viewer_.setUseHashlookup(false);
		viewer_.getTable().setHeaderVisible(true);
		viewer_.getTable().setLinesVisible(true);
		viewer_.setInput(dataHistory_);
		
		addControlButtons();
	}

	@Override
	public void setFocus() {
		viewer_.getTable().setFocus();
	}
	
	public void displayEntity(XEntity entity) {
		try {
			ArrayList<XEntity> data = new ArrayList<>();
			ArrayList<String> prop = new ArrayList<>();
			data.add(entity);
			prop.add("Name");
			
			dataHistory_.add(data);
			propertyHistory_.add(prop);
			buildView();
		}
		catch (Throwable e) {
			
		}
	}
	
	private void buildView() {
		for (int i = 0; i < MAX; ++i) {
			viewer_.getTable().getColumn(i).setText("");
			viewer_.getTable().getColumn(i).setWidth(100);
		}
				
		XEntity entity = dataHistory_.peek().isEmpty() ? null : dataHistory_.peek().get(0);
		
		if (null != entity) {
			int i = 0;
			for (String property: propertyHistory_.peek()) {
				viewer_.getTable().getColumn(i).setText(property);
				viewer_.getTable().getColumn(i).setWidth(100);
			}
			
			IStatusLineManager slm = getViewSite().getActionBars().getStatusLineManager();
			slm.setMessage("No. of entities:" + (dataHistory_.isEmpty() ? 0 : dataHistory_.peek().size()));
			viewer_.getTable().setSortColumn(null);	
			viewer_.getTable().setSortDirection(SWT.NONE);
			viewer_.getTable().setMenu(addMenues(entity));
		
			viewer_.getTable().addMouseListener(new MouseListener() {

				@Override
				public void mouseDoubleClick(MouseEvent e) {
					int selection = viewer_.getTable().getSelectionIndex();
					if (dataHistory_.peek().isEmpty() || dataHistory_.peek().size() <= selection) {
						return;
					}
					XEntity entity = dataHistory_.peek().get(selection);
					try {
						if (entity instanceof XClass) {
							JavaUI.openInEditor(((XClass)entity).getUnderlyingObject(), true, true);
						}
						else if (entity instanceof XMethod) {
							JavaUI.openInEditor(((XMethod)entity).getUnderlyingObject(), true, true);
						}
					}
					catch (PartInitException | JavaModelException e1) {
						e1.printStackTrace();
					}
				}

				@Override
				public void mouseDown(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseUp(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
				
			});
		}
		
		viewer_.refresh(true);
	}
	
	private String capitalize(String name) {
		return Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}
	
	private String decapitalize(String name) {
		return Character.toLowerCase(name.charAt(0)) + name.substring(1);
	}

	private void addControlButtons() {
		IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
		
		mgr.add(new Action("Back") {
			@Override
			public void run() {
				if (!dataHistory_.isEmpty()) {
					dataHistory_.pop();
					propertyHistory_.pop();
					buildView();
					viewer_.refresh(true);
				}
			}
		});
		
		mgr.add(new Action("Clear") {
			@Override
			public void run() {
				dataHistory_.clear();
				propertyHistory_.clear();
				FactoryMethod.clearCache();
				viewer_.getTable().removeAll();
				viewer_.refresh();
			}
		});
	}
	
	private Menu addMenues(XEntity entity) {
		Menu main = new Menu(parent_);
		Menu propertyMenu = new Menu(main);
		Menu groupMenu = new Menu(main);
		
		List<String> properties = getEntityProperties(entity);
		List<String> groups = getEntityGroups(entity);
		
		MenuItem propertyMenuItem = new MenuItem(main, SWT.CASCADE);
		propertyMenuItem.setMenu(propertyMenu);
		propertyMenuItem.setText("Properties");
		
		for (String property: properties) {
			MenuItem item = new MenuItem(propertyMenu, SWT.NONE);
			item.setText(property);
			item.addListener(SWT.Selection, (Event e) -> {
				MenuItem menuItem = (MenuItem)e.widget;
				if (!propertyHistory_.peek().contains(menuItem.getText())) {
					propertyHistory_.peek().add(menuItem.getText());
					viewer_.getTable().getColumn(propertyHistory_.peek().size() - 1).setText(menuItem.getText());
					viewer_.getTable().getColumn(propertyHistory_.peek().size()).setWidth(100);
					viewer_.refresh(true);
				}
			});
		}
		
		new MenuItem(main, SWT.SEPARATOR);
		
		MenuItem groupMenuItem = new MenuItem(main, SWT.CASCADE);
		groupMenuItem.setMenu(groupMenu);
		groupMenuItem.setText("Group");
		
		for (String group: groups) {
			MenuItem item = new MenuItem(groupMenu, SWT.NONE);
			item.setText(group);
			item.addListener(SWT.Selection, (Event e) -> {
				XEntity element = dataHistory_.peek().get(viewer_.getTable().getSelectionIndex());
				MenuItem menuItem = (MenuItem)e.widget;
				
				@SuppressWarnings("unchecked")
				Group<XEntity> groupElement = (Group<XEntity>)applyMethod(element, menuItem.getText());
				dataHistory_.push(groupElement.getElements());
				List<String> array = new ArrayList<>();
				array.add("Name");
				propertyHistory_.push(array);
				buildView();
			});
		}
		
		return main;
	}
	
	private List<String> getEntityProperties(XEntity entity) {
		List<String> names = new ArrayList<>();
		
		for (Method m: entity.getClass().getInterfaces()[0].getDeclaredMethods()) {
			if (!"getUnderlyingObject".equals(m.getName()) && !Group.class.equals(m.getReturnType())) {
				names.add(capitalize(m.getName()));
			}
		}
		
		return names;
	}
	
	private List<String> getEntityGroups(XEntity entity) {
		List<String> names = new ArrayList<>();
		
		for (Method m: entity.getClass().getInterfaces()[0].getDeclaredMethods()) {
			if (!"getUnderlyingObject".equals(m.getName()) && Group.class.equals(m.getReturnType())) {
				names.add(capitalize(m.getName()));
			}
		}
		
		return names;
	}
	
	private Object applyMethod(XEntity entity, String methodName) {
		try {
			return entity.getClass().getMethod(decapitalize(methodName)).invoke(entity);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private class CorexLabelProvider extends LabelProvider implements ITableLabelProvider {

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			boolean query = (propertyHistory_.isEmpty() || propertyHistory_.peek().size() <= columnIndex);
			System.out.println(columnIndex + " " + propertyHistory_.peek().size() + ": " + query);
			return (propertyHistory_.isEmpty() || propertyHistory_.peek().size() <= columnIndex) ? 
				   "" : 
				  applyMethod((XEntity)element, decapitalize(propertyHistory_.peek().get(columnIndex))).toString();
		}
		
	}
	
}
