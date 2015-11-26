package xcorexview.metrics.methods;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import com.salexandru.xcore.interfaces.IPropertyComputer;
import com.salexandru.xcore.metaAnnotation.PropertyComputer;

import exampletool.metamodel.entity.XMethod;

@PropertyComputer()
public class NumberOfCasts implements IPropertyComputer<Integer, XMethod> {

	@Override
	public Integer compute(XMethod entity) {
		
		ASTParser astParser = ASTParser.newParser(AST.JLS8);

		astParser.setSource(entity.getUnderlyingObject().getCompilationUnit());
		
		NodeVisitor visitor = new NodeVisitor(entity.toString());
		astParser.createAST(null).accept(visitor);
		
		return visitor.getCount();
	}
	
	private static final class NodeVisitor extends ASTVisitor {
		private int count = 0;
		private final String methodName;
		
		public NodeVisitor(final String name) {
			methodName = name;
		}
		
		@Override
		public boolean visit(MethodDeclaration d) {
			return d.getName().toString().equals(methodName);
		}
		
		@Override
		public boolean visit(CastExpression node) {
			++count;
			return true;
		}
		
		public int getCount() {return count;}
		
	}
	

}
