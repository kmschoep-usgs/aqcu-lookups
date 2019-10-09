package gov.usgs.aqcu.util;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)

public class PathUtilsTest {

    @Test
    public void trimPathTest() {
        String input = "test";
        assertEquals("test/", PathUtils.trimPath(input));
        input = "/test";
        assertEquals("test/", PathUtils.trimPath(input));
        input = "/test/";
        assertEquals("test/", PathUtils.trimPath(input));
        input = "test/";
        assertEquals("test/", PathUtils.trimPath(input));
        input = "////test";
        assertEquals("test/", PathUtils.trimPath(input));
        input = "test////";
        assertEquals("test/", PathUtils.trimPath(input));
        input = "////test////";
        assertEquals("test/", PathUtils.trimPath(input));
        input = "   test////";
        assertEquals("test/", PathUtils.trimPath(input));
        input = "   test   ";
        assertEquals("test/", PathUtils.trimPath(input));
        input = " / // ///  test  / //// //  ";
        assertEquals("test/", PathUtils.trimPath(input));
        input = "/";
        assertEquals("", PathUtils.trimPath(input));
        input = "//////";
        assertEquals("", PathUtils.trimPath(input));
        input = "  /   // //  /";
        assertEquals("", PathUtils.trimPath(input));
        input = "";
        assertEquals("", PathUtils.trimPath(input));
        input = null;
        assertEquals("", PathUtils.trimPath(input));
    }

    public void getParentPathTest() {
        String input = "test/";
        assertEquals("", PathUtils.trimPath(input));
        input = "test1/test2";
        assertEquals("test1/", PathUtils.trimPath(input));
        input = "test1/test2/";
        assertEquals("test1/", PathUtils.trimPath(input));
        input = "test1/test2/test3";
        assertEquals("test1/test2/", PathUtils.trimPath(input));
        input = "/";
        assertEquals("", PathUtils.trimPath(input));
        input = "";
        assertEquals("", PathUtils.trimPath(input));
    }

    public void mergePathsTest() {
        String root = "root";
        String child = "child";
        assertEquals("root/child/", PathUtils.mergePaths(root, child));
        root = "root/";
        child = "child/";
        assertEquals("root/child/", PathUtils.mergePaths(root, child));
        root = "/root/";
        child = "/child/";
        assertEquals("root/child/", PathUtils.mergePaths(root, child));
        root = "super/root/";
        child = "/child/leaf";
        assertEquals("super/root/child/leaf", PathUtils.mergePaths(root, child));
    }
}