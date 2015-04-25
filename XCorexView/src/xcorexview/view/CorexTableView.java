package xcorexview.view;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.part.ViewPart;

import xmetamodel.factory.FactoryMethod;

import com.salexandru.corex.interfaces.Group;
import com.salexandru.corex.interfaces.XEntity;

public class CorexTableView extends ViewPart {
	private TableViewer viewer_;
	private Composite parent_;
	private Stack<List<XEntity>> dataHistory_ = new Stack<>();
	private Stack<List<String>> propertyHistory_ = new Stack<>();
	
	private static int MAX = 12;
	public static String viewId = "com.salexandru.xcorexview.view.CorexTreeView";
	
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
		
		viewer_.setLabelProvider(new CorexLabelProvider());
		viewer_.setUseHashlookup(false);
		viewer_.getTable().setHeaderVisible(true);
		viewer_.getTable().setLinesVisible(true);
		viewer_.setInput(dataHistory_);
		
		for (int i = 0; i < MAX; ++i) {
			TableViewerColumn column = new TableViewerColumn(viewer_, SWT.NONE);
			column.getColumn().setMoveable(true);
			column.getColumn().setResizable(true);
			column.getColumn().setText("");
			column.getColumn().setWidth(100);			
		}
		
		addControlButtons();
	}

	@Override
	public void setFocus() {
		viewer_.getTable().setFocus();
	}
	
	public void displayEntity(XEntity entity) {
		try {
			dataHistory_.push(Arrays.asList(entity));
			propertyHistory_.push(Arrays.asList("name"));
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
		}
		
		viewer_.refresh(true);
	}
	
	private void addControlButtons() {
		IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
		
		mgr.add(new Action("Back") {
			@Override
			public void run() {
				if (!dataHistory_.isEmpty()) {
					dataHistory_.pop();
					propertyHistory_.pop();
					//TODO rebuild view
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
				
			});
		}
		
		new MenuItem(main, SWT.SEPARATOR);
		
		MenuItem groupMenuItem = new MenuItem(main, SWT.CASCADE);
		groupMenuItem.setMenu(propertyMenu);
		groupMenuItem.setText("Properties");
		
		for (String group: groups) {
			MenuItem item = new MenuItem(groupMenu, SWT.NONE);
			item.setText(group);
			item.addListener(SWT.Selection, (Event e) -> {
				
			});
		}
		
		return main;
	}
	
	private List<String> getEntityProperties(XEntity entity) {
		List<String> names = new ArrayList<>();
		
		for (Method m: entity.getClass().getMethods()) {
			if (!"getUnderlyingObject".equals(m.getName()) && !Group.class.equals(m.getReturnType())) {
				names.add(m.getName());
			}
		}
		
		return names;
	}
	
	private List<String> getEntityGroups(XEntity entity) {
		List<String> names = new ArrayList<>();
		
		for (Method m: entity.getClass().getMethods()) {
			if (!"getUnderlyingObject".equals(m.getName()) && Group.class.equals(m.getReturnType())) {
				names.add(m.getName());
			}
		}
		
		return names;
	}
	
	private String applyMethod(XEntity entity, String methodName) {
		try {
			return entity.getClass().getMethod(methodName).invoke(entity).toString();
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
		}
		return "Exception occured when computing: " + methodName;
	}
	
	private class CorexLabelProvider extends LabelProvider implements ITableLabelProvider {

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			return dataHistory_.isEmpty() || dataHistory_.peek().size() <= columnIndex ? 
				   "" : 
				  applyMethod(dataHistory_.peek().get(columnIndex), "name");
		}
		
	}
	
}
