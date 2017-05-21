package xcoreview.transformations;

import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;

import com.ibm.wala.cast.java.translator.jdt.JDTIdentityMapper;
import com.ibm.wala.types.ClassLoaderReference;

public class WalaInit {
	private static final ConcurrentHashMap<IJavaProject, JDTIdentityMapper> jdtToWalaMappers = new ConcurrentHashMap<>();
	
	
	public static final JDTIdentityMapper getWalaMapperFor(IJavaProject p) {
		if (jdtToWalaMappers.contains(p)) return jdtToWalaMappers.get(p);
		
		final ASTParser parser = ASTParser.newParser(AST.JLS8);
		
		parser.setProject(p);
		
		final JDTIdentityMapper mapper = new JDTIdentityMapper(ClassLoaderReference.Primordial, parser.createAST(null).getAST());
		
		jdtToWalaMappers.put(p, mapper);
		
		return mapper;
	}
	
	
}
