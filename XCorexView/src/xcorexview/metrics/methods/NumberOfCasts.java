package xcorexview.metrics.methods;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CastExpression;

import xmetamodel.XMethod;

import com.salexandru.corex.interfaces.IPropertyComputer;
import com.salexandru.corex.metaAnnotation.PropertyComputer;

@PropertyComputer()
public class NumberOfCasts implements IPropertyComputer<Integer, XMethod> {

	@Override
	public Integer compute(XMethod entity) {
		
		ASTParser astParser = ASTParser.newParser(AST.JLS8);
		try {
			astParser.setSource(entity.getUnderlyingObject().getSource().toCharArray());
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		
		NodeVisitor visitor = new NodeVisitor();
		astParser.createAST(null).accept(visitor);
		
		return visitor.getCount();
	}
	
	private static final class NodeVisitor extends ASTVisitor {
		private int count = 0;
		
		@Override
		public boolean visit(CastExpression node) {
			++count;
			return true;
		}
		
		public int getCount() {return count;}
		
	}
	

}
