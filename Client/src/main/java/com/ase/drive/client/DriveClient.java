package com.ase.drive.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import com.ase.drive.common.FileMetaData;


public class DriveClient {
    private String rootDirectory;
    private CloseableHttpClient httpClient = HttpClients.createDefault();
    private String serverURLForUser;
    public DriveClient(String username, String rootDirectory, String serverAddress) {
        this.rootDirectory = rootDirectory;
        serverURLForUser = String.format("http://%s/%s", serverAddress, username);
    }

    FileMetaData[] getInfoFiles(String infoType) {
        HttpGet get = new HttpGet(serverURLForUser + String.format("/directoryinfo/%s", infoType));
        FileMetaData[] fileInfos = null;
        try {
            CloseableHttpResponse response = httpClient.execute(get);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                String jsonText = EntityUtils.toString(entity);
                JSONArray json = new JSONArray(jsonText);
                fileInfos = new FileMetaData[json.length()];
                for (int i = 0; i < json.length(); i++) {
                    fileInfos[i] = new FileMetaData(json.getJSONObject(i));
                }
            }
            response.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileInfos;
    }
    
    public DatagramSocket connectServ(String filepath) {
		try {
			File file=new File(filepath);
			if (!file.exists()) return null;
			InetAddress serverIp=InetAddress.getByName("localhost");			
			Socket tcpSocket = new Socket(serverIp, 8080);
			String action="SEND REQUEST";
		    int serverPort=getServerPort(tcpSocket,action,filepath,1025);
			if (serverPort==0) {return null;}			
			DatagramSocket udpClientSocket=new DatagramSocket(1025);
			return udpClientSocket;
			
		}catch(Exception e) {e.printStackTrace();}
		return null;
    }
    
    public static int getServerPort(Socket tcpSocket,String action,String fileName,int udpPort) {
		int serverPort=0;
		try {
			Scanner inSocket =  new Scanner(tcpSocket.getInputStream());
			PrintWriter outSocket = new PrintWriter(tcpSocket.getOutputStream(), true);
		    outSocket.println("Finish");
		    String line=inSocket.nextLine();	    
		    while(!line.equals("Finish")) {
		    	if (line.isEmpty()) {line=inSocket.nextLine();continue;}
		    	if(line.startsWith(action)){
					String [] items=line.split(":");					
					serverPort=Integer.parseInt(items[items.length-1]);
					break;
				}
		    	line=inSocket.nextLine();
		    }
			 inSocket.close();
		     outSocket.close();
		}catch(Exception e) {e.printStackTrace();}
		return serverPort;
	}

    public FileMetaData[] getExistingFiles() {
        return getInfoFiles("existing");
    }

    public FileMetaData[] getDeletedFiles() {
        return getInfoFiles("deleted");
    }


    Path localPathForFile(String filepath) {
        return Paths.get(rootDirectory, filepath);
    }


    public boolean uploadServerFile(String filepath) {
        boolean currentStatus = false;
        HttpPost post = new HttpPost(serverURLForUser + String.format("/file/%s", encode(filepath)));
        try {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            File file = localPathForFile(filepath).toFile();
            if (file.exists()) {
                builder.addPart("file", new FileBody(file));
                post.setEntity(builder.build());
                CloseableHttpResponse response = httpClient.execute(post);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_ACCEPTED) {
                	currentStatus = true;
                }
                response.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentStatus;
    }

    String encode(String filepath) {
        Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(filepath.getBytes());
    }


    public boolean downloadClientFile(String filepath) {
        boolean currentStatus = false;
        HttpGet get = new HttpGet(serverURLForUser + String.format("/file/%s", encode(filepath)));
        try {
            CloseableHttpResponse response = httpClient.execute(get);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                BufferedInputStream bis = new BufferedInputStream(entity.getContent());
                File file = localPathForFile(filepath).toFile();
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                int inByte;
                while((inByte = bis.read()) != -1) bos.write(inByte);
                bis.close();
                bos.close();
                currentStatus = true;
            }
            response.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentStatus;
    }


    public boolean deleteServerFile(String filepath) {
        boolean currentStatus = false;
        HttpDelete get = new HttpDelete(serverURLForUser + String.format("/file/%s", encode(filepath)));
        try {
            CloseableHttpResponse response = httpClient.execute(get);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                currentStatus = true;
            }
            response.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentStatus;
    }
}