package xcorexview.metrics.methods;

import org.eclipse.jdt.core.JavaModelException;
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
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import xmetamodel.XMethod;

import com.salexandru.corex.interfaces.IPropertyComputer;
import com.salexandru.corex.metaAnnotation.PropertyComputer;

@PropertyComputer
public class CyclomaticComplexity implements IPropertyComputer<Integer, XMethod> {

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
		public boolean visit(ReturnStatement node) {
			++count;
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
			
		
		public int getCount() {return count;}
		
	}

}