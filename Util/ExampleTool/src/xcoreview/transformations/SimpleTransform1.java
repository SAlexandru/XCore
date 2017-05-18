package xcoreview.transformations;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;

import com.salexandru.xcore.utils.interfaces.ITransform;

public class SimpleTransform1 implements ITransform<IMethod, IType> {

	@Override
	public IType transform(IMethod arg0) {
		if (arg0 == null) {
			throw new IllegalArgumentException("Argument passed to simple transform is null");
		}
		return arg0.getDeclaringType();
	}

	@Override
	public IMethod reverse(IType newObj) {
		// TODO Auto-generated method stub
		return null;
	}

}
