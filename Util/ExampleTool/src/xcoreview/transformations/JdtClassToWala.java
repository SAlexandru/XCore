package xcoreview.transformations;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.ibm.wala.cast.java.translator.jdt.JDTIdentityMapper;
import com.ibm.wala.types.ClassLoaderReference;
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
		
		final ASTNode node = parser.createAST(null);
		
		node.accept(finder);
		
		final JDTIdentityMapper mapper = new JDTIdentityMapper(ClassLoaderReference.Application, node.getAST());
	
		return mapper.getTypeRef(finder.getTypeBinding());
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
		
		public ITypeBinding getTypeBinding() { return typeBinding_; }
		
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
