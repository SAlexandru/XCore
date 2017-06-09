package xcoreview.transformations;


import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

import com.ibm.wala.cast.java.translator.jdt.JDTIdentityMapper;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.MethodReference;
import com.salexandru.xcore.utils.interfaces.ITransform;

public class JdtMethodToWala implements ITransform<IMethod, MethodReference> {

	@Override
	public MethodReference transform(IMethod origObj) {
		final ASTParser parser = ASTParser.newParser(AST.JLS8);
		final MethodBindingFinder finder = new MethodBindingFinder(origObj);
		
		parser.setSource(origObj.getCompilationUnit());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setProject(origObj.getJavaProject());
		parser.setResolveBindings(true);
		
		final ASTNode node = parser.createAST(null);
		
		node.accept(finder);
		
		final JDTIdentityMapper mapper = new JDTIdentityMapper(ClassLoaderReference.Application, node.getAST());
	
		 return mapper.getMethodRef(finder.getBinding());
		
	}

	@Override
	public IMethod reverse(MethodReference newObj) {
		return null;
	}
	
	
	private static final class MethodBindingFinder extends ASTVisitor {
		private IMethod method_;
		private IMethodBinding methodBinding_ = null;
		
		public MethodBindingFinder (IMethod method) {
			if (null == method) {
				throw new IllegalArgumentException("MethodBindingFinder was given to find a null method ...");
			}
			
			method_ = method;
		}
		
		public IMethodBinding getBinding() {
			if (methodBinding_ == null) {
				throw new RuntimeException("Method Binding for " + method_.toString() + " wasn't found in the AST");
			}
			return methodBinding_;
		}
		
		@Override
		public void endVisit(MethodDeclaration m) {
			if (methodBinding_ == null && m.getName().toString().equals(method_.getElementName())) {
				methodBinding_ = m.resolveBinding();
			}
		}
		
		@Override
		public boolean visit(MethodDeclaration m) {
			if (methodBinding_ == null && m.getName().toString().equals(method_.getElementName())) {
				methodBinding_ = m.resolveBinding();
			}
			
			return true;
		}
		
		@Override
		public void endVisit(MethodInvocation m) {
			if (methodBinding_ == null && m.getName().toString().equals(method_.getElementName())) {
				methodBinding_ = m.resolveMethodBinding();
			}
		}
		
		@Override
		public boolean visit(MethodInvocation m) {
			if (methodBinding_ == null && m.getName().toString().equals(method_.getElementName())) {
				methodBinding_ = m.resolveMethodBinding();
			}
			
			return true;
		}
	}

}
