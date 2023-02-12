package com.ase.drive.client;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;

import com.ase.drive.common.FileMetaData;

public class DirInfoManager {
	Path rootDirectory;
	Path existingFileInfoPath;
	String directoryInfoFolderName = ".directoryInfo";
	
	public DirInfoManager(String rootDirectoryString) {
		this.rootDirectory = Paths.get(rootDirectoryString);
		existingFileInfoPath = Paths.get(rootDirectoryString, directoryInfoFolderName, "existing.json");		
	}
	
	static private FileMetaData[] readFileInfo(Path infoFile) {
		FileMetaData[] fileInfoArray = new FileMetaData[0];
		try {
			String content = FileUtils.readFileToString(infoFile.toFile());
			JSONArray fileInfos = new JSONArray(content);
			fileInfoArray = new FileMetaData[fileInfos.length()];
			for (int i = 0; i < fileInfoArray.length; i++) {
				fileInfoArray[i] = new FileMetaData(fileInfos.getJSONObject(i));
			}
		} catch (Exception e) {
			
		}
		return fileInfoArray;
	}

	public FileMetaData[] getSavedInfo() {
		return readFileInfo(existingFileInfoPath);
	}

	public FileMetaData[] getNewInfo() {
        List<FileMetaData> fileMetaDatas = new ArrayList<>();
        try {
			fileMetaDatas = Files.walk(rootDirectory)
						.filter(f -> {
							boolean isFileFromDirectoryInfo = false;
							try {
								isFileFromDirectoryInfo = f.toFile().getCanonicalPath().contains(directoryInfoFolderName);
							} catch(Exception e) {
								e.printStackTrace();
							}
							return Files.isRegularFile(f) && !isFileFromDirectoryInfo;
						})
						.parallel().map(filepath -> new FileMetaData(filepath, rootDirectory))
						.collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileMetaDatas.toArray(new FileMetaData[fileMetaDatas.size()]);
	}

	public boolean saveFile(FileMetaData[] fileInfos) {
		File existingInfoFile = existingFileInfoPath.toFile();
		if (!existingInfoFile.exists()) {
			existingInfoFile.getParentFile().mkdirs();
			try {
				existingInfoFile.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		JSONArray array = new JSONArray();
		for (FileMetaData f : fileInfos) {
			array.put(f.json());
		}
		try {
			FileUtils.writeStringToFile(existingInfoFile, array.toString());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean deletingFile(String filepath) {
		boolean deleteSucessful = false;
		try {
			File file = rootDirectory.resolve(filepath).toFile();
			deleteSucessful = file.delete();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return deleteSucessful;
	}
}
