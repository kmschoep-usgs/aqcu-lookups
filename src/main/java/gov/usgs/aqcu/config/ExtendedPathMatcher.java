package gov.usgs.aqcu.config;


import java.util.Comparator;
import java.util.Map;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.RouteMatcher;
import org.springframework.web.util.pattern.PathPatternRouteMatcher;

public class ExtendedPathMatcher implements PathMatcher {
    
    /**
     * Enables matching via PathPatterns...
     * https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/util/pattern/PathPattern.html
     * ...instead of AntPathMatchers...
     * https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/util/AntPathMatcher.html
     * 
     * @param pattern
     * @param path
     * @return 
     */
    
    private PathPatternRouteMatcher matcher = new PathPatternRouteMatcher();

    @Override
    public Map<String, String> extractUriTemplateVariables(String pattern, String path) {
            RouteMatcher.Route route = matcher.parseRoute(path);
            return matcher.matchAndExtract(pattern, route);
    }

        ///////////////////////////////////////////////////
        // All other methods are passthrough delegates
        ///////////////////////////////////////////////////

        private final AntPathMatcher delegate = new AntPathMatcher();
        
        @Override
	public boolean isPattern(String path) {
		return this.delegate.isPattern(path);
	}

	@Override
	public boolean match(String pattern, String path) {
		return this.delegate.match(pattern, path);
	}

	@Override
	public boolean matchStart(String pattern, String path) {
		return this.delegate.matchStart(pattern, path);
	}

	@Override
	public String extractPathWithinPattern(String pattern, String path) {
		return this.delegate.extractPathWithinPattern(pattern, path);
	}

	@Override
	public Comparator<String> getPatternComparator(final String path) {
		return this.delegate.getPatternComparator(path);
	}
	
	@Override
	public String combine(String pattern1, String pattern2) {
		return this.delegate.combine(pattern1, pattern2);
	}
}