package gov.usgs.aqcu.util;

public class PathUtils {

	public static String cleanAndValidateNewFolder(String newFolder) {
		return newFolder;
	}

	public static String mergePaths(String path1, String path2) {
		return trimPath(path1) + trimPath(path2);
	}

	public static String getParentPath(String path) {
		path = path.endsWith("/") ? path.substring(0, path.length()-1) : path;

		if(path.isEmpty() || !path.contains("/")) {
			return "";
		} else {
			return path.substring(0, path.lastIndexOf("/")) + "/";
		}
	}

	public static String getFolderName(String path) {
		if(path.isEmpty() || !path.contains("/")) {
			return path;
		} else if(path.endsWith("/")) {
			return path.substring(0, path.length()).substring(path.lastIndexOf("/"));
		} else {
			return path.substring(path.lastIndexOf("/"));
		}
	}

	public static String trimPath(String path) {
		// 1. Handle null
		path = path == null ? "" : path.trim().toLowerCase();

		// 2. Remove leading slashes
		while(path.startsWith("/")) {
			if(path.length() > 1) {
				path = path.substring(1).trim();
			} else {
				path = "";
			}			
		}

		// 3. Remove trailing slashes
		while(path.endsWith("/")) {
			path = path.substring(0, path.length() - 1).trim();
		}
		
		// 4. Add single trailing slash if not empty
		path = path.isEmpty() || path.endsWith("/") ?
			path
			: path + "/";

		return path;
	}
}