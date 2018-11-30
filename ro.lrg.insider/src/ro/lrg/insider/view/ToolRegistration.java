package ro.lrg.insider.view;

import java.util.ArrayList;
import java.util.List;

import ro.lrg.xcore.metametamodel.XEntity;

public class ToolRegistration {

	private ToolRegistration() {}
	
	private static ToolRegistration tr = new ToolRegistration();
	
	public static ToolRegistration getInstance() {
		return tr;
	}
	
	public interface XEntityConverter {
		
		public XEntity convert(Object element);
		
	}
	
	private ArrayList<XEntityConverter> converters = new ArrayList<>();

	public void registerXEntityConverter(XEntityConverter conv) {
		converters.add(conv);
	}

	class XEntityEntry {

		XEntity theEntity;
		XEntityConverter theConverter;

		XEntityEntry(XEntity anEntity, XEntityConverter theConverter) {
			this.theEntity = anEntity;
			this.theConverter = theConverter;
		}
		
	}
	
	List<XEntityEntry> toXEntity(Object element) {
		List<XEntityEntry> res = new ArrayList<>();
		for(XEntityConverter aConv : converters) {
			XEntity anEntity = aConv.convert(element);
			if(anEntity != null) {
				res.add(new XEntityEntry(anEntity,aConv));
			}
		}
		return res;
	}
	
}