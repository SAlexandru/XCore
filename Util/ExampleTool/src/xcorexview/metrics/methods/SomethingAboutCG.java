package xcorexview.metrics.methods;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;

import com.ibm.wala.cast.java.ipa.callgraph.JavaSourceAnalysisScope;
import com.ibm.wala.cast.java.translator.jdt.JDTClassLoaderFactory;
import com.ibm.wala.ide.util.JavaEclipseProjectPath;
import com.ibm.wala.ide.util.EclipseProjectPath.AnalysisScopeType;
import com.ibm.wala.ipa.callgraph.AnalysisCache;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.CallGraphBuilderCancelException;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.callgraph.propagation.SSAPropagationCallGraphBuilder;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.salexandru.xcore.utils.interfaces.IPropertyComputer;
import com.salexandru.xcore.utils.metaAnnotation.PropertyComputer;

import exampletool.metamodel.entity.XMethod;

@PropertyComputer
public class SomethingAboutCG implements IPropertyComputer<Integer, XMethod> {

	@Override
	public Integer compute(XMethod entity) {
		IJavaProject p = entity.getUnderlyingObject().getJavaProject();
		try {
			JavaEclipseProjectPath path = JavaEclipseProjectPath.make(p, AnalysisScopeType.SOURCE_FOR_PROJ_AND_LINKED_PROJS);
			AnalysisScope scope = path.toAnalysisScope(new JavaSourceAnalysisScope());
			scope.setExclusions(null);
			JDTClassLoaderFactory factory = new JDTClassLoaderFactory(scope.getExclusions());
		
		
			ClassHierarchy cha = ClassHierarchyFactory.make(scope,factory);
			
		
			
			Iterable<Entrypoint> entryPoints = Util.makeMainEntrypoints(scope, cha);
			AnalysisOptions opts = new AnalysisOptions(scope, entryPoints);
			AnalysisCache cache = new AnalysisCache(null, null, null);
			SSAPropagationCallGraphBuilder cgBuilder = Util.makeZeroCFABuilder(opts, cache, cha, scope);
			CallGraph cg = cgBuilder.makeCallGraph(opts, null);
			
			return cg.getNodes(entity.asMethodReference()).size();
		} catch (IllegalArgumentException | CallGraphBuilderCancelException | IOException | CoreException | ClassHierarchyException e) {
			e.printStackTrace();
			
			return null;
			//throw new RuntimeException(e);
		} 
	}

}
