package ro.lrg.insider.view;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

import com.salexandru.xcore.utils.annotationMarkers.ThisIsAProperty;
import com.salexandru.xcore.utils.annotationMarkers.ThisIsARelationBuilder;
import com.salexandru.xcore.utils.interfaces.RelationBuilder;
import com.salexandru.xcore.utils.interfaces.XEntity;

import ro.lrg.insider.view.ToolRegistration.XEntityEntry;

public class XCorexTableView extends ViewPart {

	public static final String viewId = "ro.lrg.insider.view.XCorexTableView";
	private static final int MAX = 12;

	private TableViewer viewer_;
	private Composite parent_;

	private Stack<List<List<XEntityEntry>>> dataHistory_ = new Stack<>();
	private Stack<List<String>> propertyHistory_ = new Stack<>();
		
	private Map<String, Integer> prop2Pos = new HashMap<>();

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

			Set<String> allTools = getAllTools(unifiedEntity);
			for(String aToolName : allTools) {
				if(!prop.contains("ToString [" + aToolName + "]"))
					prop.add("ToString [" + aToolName + "]");
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

		prop2Pos.clear();
		Map<String, List<String>> allProperties = getAllProperties(unifiedEntity,prop2Pos);
		Map<String, List<String>> allGroups = getAllGroups(unifiedEntity,prop2Pos);
		
		for(String aToolName : allProperties.keySet()) {
			Menu aToolProperties = new Menu(propertyMenu);
			MenuItem toolPropertiesItem = new MenuItem(propertyMenu, SWT.CASCADE);
			toolPropertiesItem.setMenu(aToolProperties);
			toolPropertiesItem.setText(aToolName);
			for (String aProperty: allProperties.get(aToolName)) {
				MenuItem item = new MenuItem(aToolProperties, SWT.NONE);
				item.setText(aProperty);
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
		}
		
		for(String aToolName : allGroups.keySet()) {
			Menu aToolGroups = new Menu(groupMenu);		
			MenuItem toolGroupItem = new MenuItem(groupMenu, SWT.CASCADE);
			toolGroupItem.setMenu(aToolGroups);
			toolGroupItem.setText(aToolName);
			for (String aGroup: allGroups.get(aToolName)) {
				MenuItem item = new MenuItem(aToolGroups, SWT.NONE);
				item.setText(aGroup);
				item.addListener(SWT.Selection, (Event e) -> {
					List<XEntityEntry> element = dataHistory_.peek().get(viewer_.getTable().getSelectionIndex());
					MenuItem menuItem = (MenuItem)e.widget;
					String groupNameAndTool = menuItem.getText() + " [" + menuItem.getParent().getParentItem().getText() + "]";
					@SuppressWarnings("unchecked")
					RelationBuilder<XEntity> resultedGroup = (RelationBuilder<XEntity>)applyMethod(element, groupNameAndTool);
					List<List<XEntityEntry>> unifiedElements = new ArrayList<>();
					boolean first = true;
					List<String> resultProperties = new ArrayList<>();
					for(XEntity aResultedEntity : resultedGroup.getElements()) {
						try {
							Method met = aResultedEntity.getClass().getMethod("getUnderlyingObject");
							Object result = met.invoke(aResultedEntity);
							List<XEntityEntry> aResultedUnifiedEntity = ToolRegistration.getInstance().toXEntity(result);
							unifiedElements.add(aResultedUnifiedEntity);
							if(first) {
								first = false;
								Set<String> allTools = getAllTools(aResultedUnifiedEntity);
								for(String aToolNameInResults : allTools) {
									if(!resultProperties.contains("ToString [" + aToolNameInResults + "]"))
										resultProperties.add("ToString [" + aToolNameInResults + "]");
								}
							}
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
					dataHistory_.push(unifiedElements);
					propertyHistory_.push(resultProperties);
					buildView();
				});
			}
		}
		
		for(String aToolName : getAllTools(unifiedEntity)) {
			MenuItem toolShowItem = new MenuItem(showWithMenu,SWT.NONE);		
			toolShowItem.setText(aToolName);
			toolShowItem.addListener(SWT.Selection, (Event e) -> {
				int selection = viewer_.getTable().getSelectionIndex();
				if (dataHistory_.peek().isEmpty() || dataHistory_.peek().size() <= selection) {
					return;
				}
				List<XEntityEntry> selectedUnifiedEntity = dataHistory_.peek().get(selection);
				for(XEntityEntry anEntry : selectedUnifiedEntity) {
					if(getEntityTools(anEntry.theEntity).contains(aToolName)) {
						anEntry.theConverter.show(anEntry.theEntity);
						break;
					}
				}
			});
		}

		MenuItem propertyMenuItem = new MenuItem(main, SWT.CASCADE);
		propertyMenuItem.setMenu(propertyMenu);
		propertyMenuItem.setText("Properties");
				
		new MenuItem(main, SWT.SEPARATOR);
		
		MenuItem groupMenuItem = new MenuItem(main, SWT.CASCADE);
		groupMenuItem.setMenu(groupMenu);
		groupMenuItem.setText("RelationBuilder");

		new MenuItem(main, SWT.SEPARATOR);

		MenuItem showWithItem = new MenuItem(main, SWT.CASCADE);
		showWithItem.setMenu(showWithMenu);
		showWithItem.setText("Show With");

		return main;
	}

	private Map<String,List<String>> getAllProperties(List<XEntityEntry> unifiedEntity, Map<String, Integer> crossReference) {
		Map<String,List<String>> allProperties = new HashMap<String,List<String>>();
		for(int i = 0; i < unifiedEntity.size(); i++) {
			XEntityEntry anUnifiedEntityEntry = unifiedEntity.get(i);
			Map<String, List<String>> properties = getEntityProperties(anUnifiedEntityEntry.theEntity);
			for(String aTool : properties.keySet()) {
				if(allProperties.containsKey(aTool)) {
					allProperties.get(aTool).addAll(properties.get(aTool));
				} else {
					allProperties.put(aTool,properties.get(aTool));
				}
				if(crossReference != null) {
					for(String aProp : allProperties.get(aTool))
						crossReference.put(aTool + "/" + aProp, i);
					if(!allProperties.get(aTool).contains("ToString")) {
						crossReference.put(aTool + "/" + "ToString", i);
					}
				}
			}
		}
		return allProperties;
	}

	private Map<String,List<String>> getAllGroups(List<XEntityEntry> unifiedEntity, Map<String, Integer> crossReference) {
		Map<String,List<String>> allGroups = new HashMap<String,List<String>>();
		for(int i = 0; i < unifiedEntity.size(); i++) {
			XEntityEntry anUnifiedEntityEntry = unifiedEntity.get(i);
			Map<String, List<String>> groups = getEntityGroups(anUnifiedEntityEntry.theEntity);
			for(String aToolName : groups.keySet()) {
				if(allGroups.containsKey(aToolName)) {
					allGroups.get(aToolName).addAll(groups.get(aToolName));
				} else {
					allGroups.put(aToolName,groups.get(aToolName));
				}
				if(crossReference != null) {
					for(String aGroup : allGroups.get(aToolName))
						crossReference.put(aToolName + "/" + aGroup, i);
				}
			}
		}
		return allGroups;
	}

	private Set<String> getAllTools(List<XEntityEntry> unifiedEntity) {
		HashSet<String> res = new HashSet<>();		
		res.addAll(getAllProperties(unifiedEntity,null).keySet());
		res.addAll(getAllGroups(unifiedEntity,null).keySet());
		return res;
	}

	private Set<String> getEntityTools(XEntity anEntity) {
		HashSet<String> res = new HashSet<>();		
		res.addAll(getEntityProperties(anEntity).keySet());
		res.addAll(getEntityGroups(anEntity).keySet());
		return res;
	}
	
	private Map<String,List<String>> getEntityProperties(XEntity anEntity) {
		Map<String,List<String>> names = new HashMap<String,List<String>>();
		for(Class<?> anInterface : getAllInterfaces(anEntity.getClass())) {
			String toolName = anInterface.getCanonicalName().substring(0, anInterface.getCanonicalName().indexOf('.'));
			for (Method m: anInterface.getDeclaredMethods()) {
				if (m.isAnnotationPresent(ThisIsAProperty.class) ) {
					if(!names.containsKey(toolName)) {
						names.put(toolName, new ArrayList<String>());
					}
					names.get(toolName).add(capitalize(m.getName()));
				}
			}
		}
		return names;
	}
	
	private Map<String,List<String>> getEntityGroups(XEntity anEntity) {
		Map<String,List<String>> names = new HashMap<String,List<String>>();
		for(Class<?> anInterface : getAllInterfaces(anEntity.getClass())) {
			String toolName = anInterface.getCanonicalName().substring(0, anInterface.getCanonicalName().indexOf('.'));
			for (Method m: anInterface.getDeclaredMethods()) {
				if (m.isAnnotationPresent(ThisIsARelationBuilder.class) ) {
					if(!names.containsKey(toolName)) {
						names.put(toolName, new ArrayList<String>());
					}
					names.get(toolName).add(capitalize(m.getName()));
				}
			}
		}
		return names;
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

	private Object applyMethod(List<XEntityEntry> unifiedEntity, String methodNameTool) {
		try {
			String aMethod = methodNameTool.substring(0,methodNameTool.indexOf('[')).trim();
			String aTool = methodNameTool.substring(methodNameTool.indexOf('[') + 1,methodNameTool.lastIndexOf(']')).trim();
			int pos = prop2Pos.get(aTool + "/" + aMethod);
			return unifiedEntity.get(pos).theEntity.getClass().getMethod(decapitalize(aMethod)).invoke(unifiedEntity.get(pos).theEntity);
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
				  applyMethod((List<XEntityEntry>)element, propertyHistory_.peek().get(columnIndex)).toString();
		}
		
	}
		
}
