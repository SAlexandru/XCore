package ro.lrg.insider.view;

import java.util.ArrayList;
import java.util.List;

import com.salexandru.xcore.interfaces.XEntity;

public class ToolRegistration {

	private ToolRegistration() {}
	
	private static ToolRegistration tr = new ToolRegistration();
	
	public static ToolRegistration getInstance() {
		return tr;
	}
	
	public interface XEntityConverter {
		
		public XEntity convert(Object element);
			
		public void show(XEntity theEntity);
		
		public String getToolName();
		
	}
	
	private ArrayList<XEntityConverter> converters = new ArrayList<>();

	public void registerXEntityConverter(XEntityConverter conv) {
		converters.add(conv);
	}

	class XEntityEntry {

		String toolName;		
		XEntity theEntity;
		XEntityConverter theConverter;

		XEntityEntry(String toolName, XEntity anEntity, XEntityConverter theConverter) {
			this.toolName = toolName;
			this.theEntity = anEntity;
			this.theConverter = theConverter;
		}
		
	}
	
	List<XEntityEntry> toXEntity(Object element) {
		List<XEntityEntry> res = new ArrayList<>();
		for(XEntityConverter aConv : converters) {
			XEntity anEntity = aConv.convert(element);
			if(anEntity != null) {
				String toolName = aConv.getToolName();
				res.add(new XEntityEntry(toolName,anEntity,aConv));
			}
		}
		return res;
	}
	
}