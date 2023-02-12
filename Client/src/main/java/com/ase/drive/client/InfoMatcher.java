package com.ase.drive.client;

import java.util.ArrayList;
import java.util.List;

import com.ase.drive.common.*;

public class InfoMatcher {
	private final DirInfoManager dirInfoManager;
	private final DriveClient httpClient;
	
	public InfoMatcher(
        DirInfoManager dirInfoManager,
        DriveClient httpClient
    ) {
		this.dirInfoManager = dirInfoManager;
		this.httpClient = httpClient;
    }

	/**
	 * find the difference between server and client json 
	 * @param a
	 * @param b
	 * @return
	 */
    List<FileMetaData> differenceOf(FileMetaData[] a, FileMetaData[] b) {
        List<FileMetaData> diff = new ArrayList<>();
        for (FileMetaData fa : a) {
            boolean found = false;
            for (FileMetaData fb : b) {
                if (fa.hasSamePathAs(fb)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                diff.add(fa);
            }
        }
        return diff;
    }

    /**
     * 
     * @param a
     * @param b
     * @return
     */
    List<FileMetaData> updatedInFirst(FileMetaData[] a, FileMetaData[] b) {
        List<FileMetaData> updated = new ArrayList<>();

        return updated;
    }

    public FileMetaData[] getFilesToBeAddedToServer() {
        List<FileMetaData> fileMetaDatas = new ArrayList<>();
        fileMetaDatas.addAll(
            differenceOf(
                dirInfoManager.getNewInfo(),
                httpClient.getExistingFiles()
            )
        );
        fileMetaDatas.addAll(
            updatedInFirst(
                dirInfoManager.getNewInfo(),
                httpClient.getExistingFiles()
            )
        );
        return fileMetaDatas.toArray(new FileMetaData[fileMetaDatas.size()]);
    }

    public FileMetaData[] getFilesToBeDeletedFromServer() {
        List<FileMetaData> fileMetaDatas = new ArrayList<>();
        fileMetaDatas.addAll(
            differenceOf(
                dirInfoManager.getSavedInfo(),
                dirInfoManager.getNewInfo()
            )
        );
        return fileMetaDatas.toArray(new FileMetaData[fileMetaDatas.size()]);
    }

    public FileMetaData[] getFilesToBeAddedToClient() {
        List<FileMetaData> fileMetaDatas = new ArrayList<>();
        fileMetaDatas.addAll(
            differenceOf(
                httpClient.getExistingFiles(),
                dirInfoManager.getNewInfo()
            )
        );
        fileMetaDatas.addAll(
            updatedInFirst(
                httpClient.getExistingFiles(),
                dirInfoManager.getNewInfo()
            )
        );
        return fileMetaDatas.toArray(new FileMetaData[fileMetaDatas.size()]);
    }

    public FileMetaData[] getFilesToBeDeletedFromClient() {
        return httpClient.getDeletedFiles();
    }
}