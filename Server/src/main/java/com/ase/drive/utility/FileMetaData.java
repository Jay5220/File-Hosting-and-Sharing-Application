package com.ase.drive.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import org.json.JSONObject;


public class FileMetaData {
    String filepath;
    String hash;
    Long lastModified;

    public FileMetaData(String filepath, String hash, Long lastModified) {
        this.filepath = filepath;
        this.hash = hash;
        this.lastModified = lastModified;
    }

    public FileMetaData(Path filepath, Path userlocation) {
        this.filepath = userlocation.relativize(filepath).toString();
        File f = filepath.toFile();
        lastModified = f.lastModified();
        hash = generateMD5(filepath);
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
    
    public PrintWriter connectClient() {
    	try {
			ServerSocket serverSocket=null;			
			try {
				serverSocket = new ServerSocket(8080);	
				System.out.println(serverSocket);
			} catch (IOException ioEx) {
				System.exit(1);
			}
			do {
				try {
					Socket client = serverSocket.accept();										
					Scanner inSocket = new Scanner(client.getInputStream());							
					PrintWriter outSocket = new PrintWriter(client.getOutputStream(), true);
					String line=inSocket.nextLine();
					System.out.println(inSocket);
				    while(!line.equals("FINISH")) {
				    	line=inSocket.nextLine();
				    }
				    return outSocket;    
				}catch(IOException io) {
					System.exit(1);
				}
		} while (true);			
	
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
    }

    @Override
    public String toString() {
        return json().toString();
    }

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

    private static String generateMD5(Path path) {
        MessageDigest md;
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(path.toFile());
            md = MessageDigest.getInstance("MD5");
            FileChannel channel = inputStream.getChannel();
            ByteBuffer buff = ByteBuffer.allocate(2048);
            while (channel.read(buff) != -1) {
                buff.flip();
                md.update(buff);
                buff.clear();
            }
            byte[] hashValue = md.digest();
            return new String(hashValue);
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (IOException e) {
            return null;
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {

            }
        }
    }

    
    public String getfilepath() {
        return filepath;
    }

   
    public String getHash() {
        return hash;
    }

    
    public String getLastModified() {
        return lastModified.toString();
    }
}