package com.ase.drive.client;

import java.util.Arrays;

import com.ase.drive.common.FileMetaData;

public class Sync {
    String serverURL;
    String rootDirectory;
    String userName;
    InfoMatcher infoMatcher;
    DriveClient httpClient;
    DirInfoManager dirInfoManager;
    FileMetaData[] locaNewFileInfos;

    /**
     * Sync java Thread
     * @param serverURL
     * @param rootDirectory
     * @param userName
     */
    public Sync(String serverURL, String rootDirectory, String userName) {
        this.serverURL = serverURL;
        this.rootDirectory = rootDirectory;
        this.userName = userName;
        this.httpClient = new DriveClient(userName, rootDirectory, serverURL);
        dirInfoManager = new DirInfoManager(rootDirectory);
    }

    void loopIng() {
        locaNewFileInfos = dirInfoManager.getNewInfo();
        infoMatcher = new InfoMatcher(dirInfoManager, httpClient);
    }

    void filesaddedAtClient() {
        FileMetaData[] fileInfos = infoMatcher.getFilesToBeAddedToClient();
        System.out.println("Files Added At Client: " + Arrays.toString(fileInfos));
        System.out.println("          ");
        for (FileMetaData f : fileInfos) {
        	System.out.println("Uploading....");
            httpClient.downloadClientFile(f.getFilepath());
        }
    }
    void filesdeletedAtClient() {
        FileMetaData[] fileInfos = infoMatcher.getFilesToBeDeletedFromClient();
        System.out.println("Files Delete At Client: " + Arrays.toString(fileInfos));
        for (FileMetaData f : fileInfos) {
        	System.out.println("Deleting....");
            dirInfoManager.deletingFile(f.getFilepath());
        }
    }
    void filesaddedAtServer() {
        FileMetaData[] fileInfos = infoMatcher.getFilesToBeAddedToServer();
        System.out.println("Files Added At Server: " + Arrays.toString(fileInfos));
        for (FileMetaData f : fileInfos) {
        	System.out.println("Uploading....");
            httpClient.uploadServerFile(f.getFilepath());
        }
    }
    void filesdeletedAtServer() {
        FileMetaData[] fileInfos = infoMatcher.getFilesToBeDeletedFromServer();
        System.out.println("Files Delete At Server: " + Arrays.toString(fileInfos));
        for (FileMetaData f : fileInfos) {
        	System.out.println("Deleting....");
            httpClient.deleteServerFile(f.getFilepath());
        }
    }

    public void loopStarting() {
        Runnable runnable = () -> {
            while (true) {
                System.out.println("Scanning the folder");
                loopIng();
                filesdeletedAtServer();
                filesdeletedAtClient();
                filesaddedAtServer();
                filesaddedAtClient();
                dirInfoManager.saveFile(locaNewFileInfos);
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Thread t = new Thread(runnable);
        t.run();
    }
}