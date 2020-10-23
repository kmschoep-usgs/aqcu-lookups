package gov.usgs.aqcu.config;

import java.util.Collections;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ExtendedPathMatcherTest {
    private ExtendedPathMatcher instance;
    
    @Before
    public void setUp() {
        instance = new ExtendedPathMatcher();
    }

    @Test
    public void testExtractUriTrailingTemplateVariable() {
        Map<String, String> actual = instance.extractUriTemplateVariables("/test/{*trailing}", "/test/foo");
        assertEquals(Collections.singletonMap("trailing", "/foo"), actual);
        
        actual = instance.extractUriTemplateVariables("/test/{*trailing}", "/test/foo/bar");
        assertEquals(Collections.singletonMap("trailing", "/foo/bar"), actual);
        
        actual = instance.extractUriTemplateVariables("/test/{*trailing}", "/test/");
        assertEquals(Collections.singletonMap("trailing", "/"), actual);
        
        actual = instance.extractUriTemplateVariables("/test/{*trailing}", "/test");
        assertEquals(Collections.singletonMap("trailing", ""), actual);
    }

    @Test
    public void testMatch() {
        assertTrue(instance.match("/test/{*trailing}", "/test/foo"));
        assertTrue(instance.match("/test/{*trailing}", "/test/foo/bar"));
        assertTrue(instance.match("/test/{*trailing}", "/test/foo/bar/baz"));
        assertTrue(instance.match("/test/{*trailing}", "/test/"));
        assertTrue(instance.match("/test/{*trailing}", "/test"));
    }
    
}
