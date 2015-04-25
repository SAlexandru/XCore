package xcorexview.metrics.methods;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import xmetamodel.XMethod;

import com.salexandru.corex.interfaces.IPropertyComputer;
import com.salexandru.corex.metaAnnotation.PropertyComputer;

@PropertyComputer
public class NumberOfNullChecks implements IPropertyComputer<Integer, XMethod> {

	@Override
	public Integer compute(XMethod entity) {
		ICompilationUnit unit = entity.getUnderlyingObject().getCompilationUnit();
		
		ASTParser astParser = ASTParser.newParser(AST.JLS8);
		astParser.setSource(unit);
		
		NodeVisitor visitor = new NodeVisitor();
		astParser.createAST(null).accept(visitor);
		
		return visitor.getCount();
	}
	
	private static final class NodeVisitor extends ASTVisitor {
		private int count = 0;
		
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
