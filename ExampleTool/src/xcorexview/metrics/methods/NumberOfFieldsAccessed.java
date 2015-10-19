package xcorexview.metrics.methods;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import xmetamodel.XMethod;

import com.salexandru.xcore.interfaces.IPropertyComputer;
import com.salexandru.xcore.metaAnnotation.PropertyComputer;


@PropertyComputer
public class NumberOfFieldsAccessed implements IPropertyComputer <Integer, XMethod>{

	@Override
	public Integer compute(XMethod entity) {
		ASTParser astParser = ASTParser.newParser(AST.JLS8);
		astParser.setSource(entity.getUnderlyingObject().getCompilationUnit());
		
		final String name = entity.name();
		final NodeVisitor visitor = new NodeVisitor(name);
		astParser.createAST(null).accept(new NodeVisitor(name));
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
		public boolean visit(FieldAccess node) {
			++count;
			return true;
		}
		
		public int getCount() {return count;}
		
	}
}