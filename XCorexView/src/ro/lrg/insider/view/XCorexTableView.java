package ro.lrg.insider.view;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

import com.salexandru.xcore.interfaces.Group;
import com.salexandru.xcore.interfaces.XEntity;

import ro.lrg.insider.view.ToolRegistration.XEntityEntry;

public class XCorexTableView extends ViewPart {

	public static final String viewId = "ro.lrg.insider.view.XCorexTableView";
	private static final int MAX = 12;

	private TableViewer viewer_;
	private Composite parent_;

	private Stack<List<List<XEntityEntry>>> dataHistory_ = new Stack<>();
	private Stack<List<String>> propertyHistory_ = new Stack<>();
		
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
	
	public void displayEntity(Object entity) {
		try {
			
			List<XEntityEntry> unifiedEntity = ToolRegistration.getInstance().toXEntity(entity);
			if(unifiedEntity.size() == 0) { return; }
			
			ArrayList<List<XEntityEntry>> data = new ArrayList<>();
			ArrayList<String> prop = new ArrayList<>();
			
			data.add(unifiedEntity);
			
			for(XEntityEntry anUnifiedEntity : unifiedEntity) {
				prop.add("ToString [" + anUnifiedEntity.toolName + "]");
			}
			
			dataHistory_.add(data);
			propertyHistory_.add(prop);
			
			buildView();

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	private void buildView() {
		
		for (int i = 0; i < MAX; ++i) {
			viewer_.getTable().getColumn(i).setText("");
			viewer_.getTable().getColumn(i).setWidth(100);
		}
				
		List<XEntityEntry> unifiedEntity = (dataHistory_.isEmpty() || dataHistory_.peek().isEmpty()) ? null : dataHistory_.peek().get(0);
		
		if (null != unifiedEntity) {
			
			int i = 0;
			for (String property: propertyHistory_.peek()) {
				viewer_.getTable().getColumn(i).setText(property);
				viewer_.getTable().getColumn(i).setWidth(100);
				i++;
			}
			
			IStatusLineManager slm = getViewSite().getActionBars().getStatusLineManager();
			slm.setMessage("No. of entities:" + (dataHistory_.isEmpty() ? 0 : dataHistory_.peek().size()));
			viewer_.getTable().setSortColumn(null);	
			viewer_.getTable().setSortDirection(SWT.NONE);
			viewer_.getTable().setMenu(addMenues(unifiedEntity));
		
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
				if (dataHistory_.size() > 1) {
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
				viewer_.getTable().setMenu(null);
				viewer_.getTable().removeAll();
				buildView();
			}
		});
	}
	
	private Menu addMenues(List<XEntityEntry> unifiedEntity) {
		
		Menu main = new Menu(parent_);
		Menu propertyMenu = new Menu(main);
		Menu groupMenu = new Menu(main);
		Menu showWithMenu = new Menu(main);
		
		for(XEntityEntry anUnifiedEntityEntry : unifiedEntity) {

			Menu toolProperties = new Menu(propertyMenu);
			Menu toolGroups = new Menu(groupMenu);		

			MenuItem toolPropertiesItem = new MenuItem(propertyMenu, SWT.CASCADE);
			toolPropertiesItem.setMenu(toolProperties);
			toolPropertiesItem.setText(anUnifiedEntityEntry.toolName);
			
			MenuItem toolGroupItem = new MenuItem(groupMenu, SWT.CASCADE);
			toolGroupItem.setMenu(toolGroups);
			toolGroupItem.setText(anUnifiedEntityEntry.toolName);
								
			MenuItem toolShowItem = new MenuItem(showWithMenu,SWT.NONE);		
			toolShowItem.setText(anUnifiedEntityEntry.toolName);
			toolShowItem.addListener(SWT.Selection, (Event e) -> {
						int selection = viewer_.getTable().getSelectionIndex();
						if (dataHistory_.peek().isEmpty() || dataHistory_.peek().size() <= selection) {
							return;
						}
						List<XEntityEntry> selectedUnifiedEntity = dataHistory_.peek().get(selection);
						for(XEntityEntry anEntry : selectedUnifiedEntity) {
							if(anEntry.toolName.equals(((MenuItem)e.widget).getText()))
								anEntry.theConverter.show(anEntry.theEntity);
						}
			});

			List<String> properties = getEntityProperties(anUnifiedEntityEntry.theEntity);
			List<String> groups = getEntityGroups(anUnifiedEntityEntry.theEntity);	
			
			for (String property: properties) {
				MenuItem item = new MenuItem(toolProperties, SWT.NONE);
				item.setText(property);
				item.addListener(SWT.Selection, (Event e) -> {
					MenuItem menuItem = (MenuItem)e.widget;
					String propertyNameAndTool = menuItem.getText() + " [" + menuItem.getParent().getParentItem().getText() + "]";
					if (!propertyHistory_.peek().contains(propertyNameAndTool)) {
						propertyHistory_.peek().add(propertyNameAndTool);
						viewer_.getTable().getColumn(propertyHistory_.peek().size() - 1).setText(propertyNameAndTool);
						viewer_.getTable().getColumn(propertyHistory_.peek().size()).setWidth(100);
						viewer_.refresh(true);
					}
				});
			}

			for (String group: groups) {
				MenuItem item = new MenuItem(toolGroups, SWT.NONE);
				item.setText(group);
				item.addListener(SWT.Selection, (Event e) -> {
					List<XEntityEntry> element = dataHistory_.peek().get(viewer_.getTable().getSelectionIndex());
					MenuItem menuItem = (MenuItem)e.widget;
					String groupNameAndTool = menuItem.getText() + " [" + menuItem.getParent().getParentItem().getText() + "]";
					@SuppressWarnings("unchecked")
					Group<XEntity> groupElements = (Group<XEntity>)applyMethod(element, groupNameAndTool);
					List<List<XEntityEntry>> unifiedElements = new ArrayList<>();
					boolean first = true;
					List<String> newProperties = new ArrayList<>();
					for(XEntity aGroupEntity : groupElements.getElements()) {
						try {
							Method met = aGroupEntity.getClass().getMethod("getUnderlyingObject");
							Object result = met.invoke(aGroupEntity);
							List<XEntityEntry> aGroupUnifiedEntity = ToolRegistration.getInstance().toXEntity(result);
							unifiedElements.add(aGroupUnifiedEntity);
							if(first) {
								first = false;
								for(XEntityEntry aGroupUnifiedEntityEntry : aGroupUnifiedEntity) {
									newProperties.add("ToString [" + aGroupUnifiedEntityEntry.toolName +"]");					
								}
							}
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
					dataHistory_.push(unifiedElements);
					propertyHistory_.push(newProperties);
					buildView();
				});
			}

		}
		
		MenuItem propertyMenuItem = new MenuItem(main, SWT.CASCADE);
		propertyMenuItem.setMenu(propertyMenu);
		propertyMenuItem.setText("Properties");
				
		new MenuItem(main, SWT.SEPARATOR);
		
		MenuItem groupMenuItem = new MenuItem(main, SWT.CASCADE);
		groupMenuItem.setMenu(groupMenu);
		groupMenuItem.setText("Group");

		new MenuItem(main, SWT.SEPARATOR);

		MenuItem showWithItem = new MenuItem(main, SWT.CASCADE);
		showWithItem.setMenu(showWithMenu);
		showWithItem.setText("Show With");

		return main;
	}
	
	private List<String> getEntityProperties(XEntity anEntity) {
		List<String> names = new ArrayList<>();
		for(Class<?> anInterface : getAllInterfaces(anEntity.getClass())) {
			for (Method m: anInterface.getDeclaredMethods()) {
				if (!"getUnderlyingObject".equals(m.getName()) && !Group.class.equals(m.getReturnType())) {
					names.add(capitalize(m.getName()));
				}
			}
		}
		return names;
	}
	
	private List<String> getEntityGroups(XEntity anEntity) {
		List<String> names = new ArrayList<>();
		for(Class<?> anInterface : getAllInterfaces(anEntity.getClass())) {
			for (Method m: anInterface.getMethods()) {
				if (!"getUnderlyingObject".equals(m.getName()) && Group.class.equals(m.getReturnType())) {
					names.add(capitalize(m.getName()));
				}
			}
		}
		return names;
	}
	
	private Object applyMethod(List<XEntityEntry> unifiedEntity, String methodNameTool) {
		try {
			String methodName = methodNameTool.substring(0,methodNameTool.indexOf('[')).trim();
			String toolName = methodNameTool.substring(methodNameTool.indexOf('[') + 1,methodNameTool.lastIndexOf(']')).trim();
			for(XEntityEntry anEntryEntry : unifiedEntity) {
				if(anEntryEntry.toolName.equals(toolName)) {
					return anEntryEntry.theEntity.getClass().getMethod(decapitalize(methodName)).invoke(anEntryEntry.theEntity);
				}
			}
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

		@SuppressWarnings("unchecked")
		@Override
		public String getColumnText(Object element, int columnIndex) {
			return (propertyHistory_.isEmpty() || propertyHistory_.peek().size() <= columnIndex) ? 
				   "" : 
				  applyMethod((List<XEntityEntry>)element, decapitalize(propertyHistory_.peek().get(columnIndex))).toString();
		}
		
	}
	
	private Set<Class<?>> getAllInterfaces(Class<?> aClass) {
		HashSet<Class<?>> res = new HashSet<Class<?>>();
		if(aClass != null) {
			for(Class<?> anInterface : aClass.getInterfaces()) {
				res.add(anInterface);
				res.addAll(getAllInterfaces(anInterface));
			}
			res.addAll(getAllInterfaces(aClass.getSuperclass()));
		}
		return res;
	}
	
}
