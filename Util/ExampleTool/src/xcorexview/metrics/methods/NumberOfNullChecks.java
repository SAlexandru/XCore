package xcorexview.metrics.methods;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import com.salexandru.xcore.utils.interfaces.IPropertyComputer;
import com.salexandru.xcore.utils.metaAnnotation.PropertyComputer;

import exampletool.metamodel.entity.XMethod;

@PropertyComputer
public class NumberOfNullChecks implements IPropertyComputer<Integer, XMethod> {

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
		
		public NodeVisitor(final String methodName) {
			this.methodName = methodName;
		}
		
		@Override
		public boolean visit(MethodDeclaration d) {
			return d.getName().toString().equals(methodName);
		}
		
		@Override
		public boolean visit(IfStatement node) {
			node.getExpression().accept(new ASTVisitor() {
				@Override
				public boolean visit(InfixExpression infixExpr) {
					if (infixExpr.getLeftOperand().getNodeType() == ASTNode.NULL_LITERAL || 
						infixExpr.getRightOperand().getNodeType() == ASTNode.NULL_LITERAL) {
						++count;
					}
					return true;
				}
			});
			
			return true;
		}
			
		
		public int getCount() {return count;}
		
	}

}
