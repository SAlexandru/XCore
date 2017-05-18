package xcoreview.transformations;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.salexandru.xcore.utils.interfaces.ITransform;

public class SimpleTransform2 implements ITransform<IType, IMethod> {

	@Override
	public IMethod transform(IType arg0) {
		if (arg0 == null) {
			throw new IllegalArgumentException("Argument passed to simple transform is null");
		}
		try {
			return arg0.getMethods()[0];
		} catch (JavaModelException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public IType reverse(IMethod newObj) {
		// TODO Auto-generated method stub
		return null;
	}

}
