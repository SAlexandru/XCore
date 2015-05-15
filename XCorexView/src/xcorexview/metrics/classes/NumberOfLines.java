package xcorexview.metrics.classes;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import xmetamodel.XClass;

import com.salexandru.xcorex.interfaces.IPropertyComputer;
import com.salexandru.xcorex.metaAnnotation.PropertyComputer;

@PropertyComputer
public class NumberOfLines implements IPropertyComputer<Integer, XClass> {
	@Override
	public Integer compute(XClass entity) {
		
		int count = 0;
		
		IType unit = entity.getUnderlyingObject();
		
		try {
			Matcher match = Pattern.compile("[^\n\r]{1,}").matcher(unit.getSource());
			
			while (match.find()) {
				++count;
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return count;
	}
}
