package gov.usgs.aqcu.config;

import java.util.Comparator;
import java.util.Map;
import org.springframework.http.server.PathContainer;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.RouteMatcher;
import org.springframework.web.util.pattern.PathPatternParser;
import org.springframework.web.util.pattern.PathPatternRouteMatcher;
import org.springframework.web.util.pattern.PatternParseException;

/**
 * Enables path matching via the more fully-featured PathPatterns...
 * https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/util/pattern/PathPattern.html
 * ...instead of the more limited AntPathMatchers...
 * https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/util/AntPathMatcher.html
 */
public class ExtendedPathMatcher implements PathMatcher {

    private final PathPatternRouteMatcher matcher;
    private final AntPathMatcher antMatcher = new AntPathMatcher();

    public ExtendedPathMatcher() {
        PathPatternParser parser = new PathPatternParser();
        parser.setPathOptions(PathContainer.Options.HTTP_PATH);
        parser.setMatchOptionalTrailingSeparator(false);
        matcher = new PathPatternRouteMatcher(parser);
    }

    @Override
    public Map<String, String> extractUriTemplateVariables(String pattern, String path) {
        RouteMatcher.Route route = matcher.parseRoute(path);
        return matcher.matchAndExtract(pattern, route);
    }

    @Override
    public boolean isPattern(String path) {
        return matcher.isPattern(path);
    }

    @Override
    public boolean match(String pattern, String path) {
        RouteMatcher.Route route = matcher.parseRoute(path);
        return matcher.match(pattern, route);
    }

    @Override
    public Comparator<String> getPatternComparator(final String path) {
        RouteMatcher.Route route = matcher.parseRoute(path);
        return matcher.getPatternComparator(route);
    }

    @Override
    public String combine(String pattern1, String pattern2) {
        return matcher.combine(pattern1, pattern2);
    }

    @Override
    public boolean matchStart(String pattern, String path) {
        /**
         * It's ok to delegate this to the antMatcher because the result 
         * is the same regardless of whether patterns are compatible with 
         * AntMatcher or PathPattern
         */
        return this.antMatcher.matchStart(pattern, path);
    }

    @Override
    public String extractPathWithinPattern(String pattern, String path) {
        /**
         * It's ok to delegate this to the antMatcher because the result 
         * is the same regardless of whether patterns are compatible with 
         * AntMatcher or PathPattern
         */
        return this.antMatcher.extractPathWithinPattern(pattern, path);
    }
}
