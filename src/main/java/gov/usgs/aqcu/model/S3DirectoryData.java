package gov.usgs.aqcu.model;

import java.util.List;

public class S3DirectoryData {
    private String rootPath;
    private List<String> subPaths;
    private S3DirectoryConfig config;

    public String getRootPath() {
        return rootPath;
    }

    public S3DirectoryConfig getConfig() {
        return config;
    }

    public void setConfig(S3DirectoryConfig config) {
        this.config = config;
    }

    public List<String> getSubPaths() {
        return subPaths;
    }

    public void setSubPaths(List<String> subPaths) {
        this.subPaths = subPaths;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }
}