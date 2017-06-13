package xcorexview.metrics.methods;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import com.salexandru.xcore.utils.interfaces.IPropertyComputer;
import com.salexandru.xcore.utils.metaAnnotation.PropertyComputer;

import exampletool.metamodel.entity.XMethod;

/**
 * 
 * @author SAlexandru34
 *
 * @param entity  the XClass used for processing
 */
@PropertyComputer
public class CyclomaticComplexity implements IPropertyComputer<Integer, XMethod> {
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
			++count;
			if (null != node.getElseStatement()) {
				++count;
			}
			return true;
		}
		
		@Override
		public boolean visit(ForStatement node) {
			++count;
			return true;
		}
		
		@Override
		public boolean visit(SwitchCase node) {
			++count;
			return true;
		}
		
		@Override
		public boolean visit(ConditionalExpression node) {
			++count;
			if (null != node.getElseExpression()) {
				++count;
			}
			return true;
		}
		
		@Override
		public boolean visit(InfixExpression node) {
			InfixExpression.Operator type = node.getOperator();
			
			if (InfixExpression.Operator.CONDITIONAL_AND == type ||
				InfixExpression.Operator.CONDITIONAL_OR == type) {
				++count;
			}
			
			return true;
		}
		
		
		@Override
		public boolean visit(WhileStatement node) {
			++count;
			return true;
		}
		
		@Override
		public boolean visit(DoStatement node) {
			++count;
			return true;
		}
		
		@Override
		public boolean visit(CatchClause node) {
			++count;
			return true;
		}
		
		@Override
		public boolean visit(ThrowStatement node) {
			++count;
			return true;
		}
		
		@Override
		public boolean visit(BreakStatement node) {
			++count;
			return true;
		}
		
		@Override
		public boolean visit(ContinueStatement node) {
			++count;
			return true;
		}
			
		public int getCount() {return count + 1;}	
	}
}