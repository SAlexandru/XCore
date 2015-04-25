package xcorexview.metrics.methods;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;

import xmetamodel.XMethod;

import com.salexandru.corex.interfaces.IPropertyComputer;
import com.salexandru.corex.metaAnnotation.PropertyComputer;

@PropertyComputer
public class NumberOfLines implements IPropertyComputer<Integer, XMethod> {
	@Override
	public Integer compute(XMethod entity) {
		
		int count = 0;
		ICompilationUnit unit = entity.getUnderlyingObject().getCompilationUnit();
		
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
