package xcoreview.transformations;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.ibm.wala.types.TypeReference;
import com.salexandru.xcore.utils.interfaces.ITransform;

public class JdtClassToWala implements ITransform<IType, TypeReference> {

	@Override
	public TypeReference transform(IType origObj) {
		final ASTParser parser = ASTParser.newParser(AST.JLS8);
		final TypeBindingFinder finder = new TypeBindingFinder(origObj);
		
		parser.setSource(origObj.getCompilationUnit());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setProject(origObj.getJavaProject());
		parser.setResolveBindings(true);
		
		parser.createAST(null).accept(finder);
	
		 return WalaInit.getWalaMapperFor(origObj.getJavaProject()).getTypeRef(finder.getBinding());
	}

	@Override
	public IType reverse(TypeReference newObj) { return null; }
	
	private static final class TypeBindingFinder extends ASTVisitor {
		private IType type_;
		private ITypeBinding typeBinding_ = null;
		
		public TypeBindingFinder (IType type) {
			if (null == type) {
				throw new IllegalArgumentException("MethodBindingFinder was given to find a null method ...");
			}
			
			type_ = type;
		}
		
		public ITypeBinding getBinding() {
			if (type_ == null) {
				throw new RuntimeException("Method Binding for " + type_.toString() + " wasn't find in the AST");
			}
			return typeBinding_;
		}
		
		@Override
		public boolean visit(TypeDeclaration t) {
			if (t.resolveBinding().getJavaElement().getJavaModel().equals(type_.getJavaModel())) {
				typeBinding_ = t.resolveBinding();
				return false;
			}
			
			return true;
		}
	}
}
