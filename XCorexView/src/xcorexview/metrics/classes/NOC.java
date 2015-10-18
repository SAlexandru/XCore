package xcorexview.metrics.classes;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;

import xmetamodel.XClass;

import com.salexandru.xcore.interfaces.IPropertyComputer;
import com.salexandru.xcore.metaAnnotation.PropertyComputer;

@PropertyComputer
public class NOC implements IPropertyComputer<Integer, XClass> {

	@Override
	public Integer compute(XClass entity) {
		try {
			  final IJavaProject project = entity.getUnderlyingObject().getJavaProject();
		      final IType ifaceType = project.findType(entity.getUnderlyingObject().getFullyQualifiedName());
		      final SearchPattern ifacePattern = SearchPattern.createPattern( ifaceType, IJavaSearchConstants.IMPLEMENTORS );
		      final IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
		      final SearchEngine searchEngine = new SearchEngine();
		      final List<SearchMatch> results = new LinkedList<SearchMatch>();
		      searchEngine.search( ifacePattern, 
		      new SearchParticipant[]{ SearchEngine.getDefaultSearchParticipant() }, scope, new SearchRequestor() {

		        @Override
		        public void acceptSearchMatch( SearchMatch match ) throws CoreException
		        {
		          results.add( match );
		        }

		      }, null);
		      return results.size();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	
		return 0;
	}

}
