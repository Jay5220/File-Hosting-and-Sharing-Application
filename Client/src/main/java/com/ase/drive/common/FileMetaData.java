package com.ase.drive.common;

import java.io.File;
import java.nio.file.Path;

import org.json.JSONObject;


public class FileMetaData {
    String filepath;
    String hash;
    Long lastModified;

    /**
     * collect data from json
     * @param filepath
     * @param hash
     * @param lastModified
     */
    public FileMetaData(String filepath, String hash, Long lastModified) {
        this.filepath = filepath;
        this.hash = hash;
        this.lastModified = lastModified;
    }

    /**
     * 
     * @param filepath
     * @param rootPath
     */
    public FileMetaData(Path filepath, Path rootPath) {
        this.filepath = rootPath.relativize(filepath).toString();
        File f = filepath.toFile();
        lastModified = f.lastModified();
        hash = "";
    }

    public FileMetaData(JSONObject json) {
        filepath = json.getString("filepath");
        hash = json.getString("hash");
        lastModified = Long.parseLong(json.getString("lastModified"));
    }

    public JSONObject json() {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("filepath", filepath);
        jsonObj.put("hash", hash);
        jsonObj.put("lastModified", lastModified.toString());
        return jsonObj;
    }

    @Override
    public String toString() {
        return json().toString();
    }

    /**
     * 
     * @param other
     * @return
     */
    boolean isSameAs(FileMetaData other) {
        return (
            hasSamePathAs(other) &&
            hash.equals(other.hash) &&
            lastModified.equals(other.lastModified)
        );
    }

    public boolean hasSamePathAs(FileMetaData other) {
        return (
            filepath.equals(other.filepath)
        );
    }


    public String getFilepath() {
        return filepath;
    }


    public String getHash() {
        return hash;
    }


    public Long getLastModified() {
        return lastModified;
    }

    static FileMetaData example() {
        return new FileMetaData("foo/bar", "0x6469796547", Long.valueOf(1));
    }
}