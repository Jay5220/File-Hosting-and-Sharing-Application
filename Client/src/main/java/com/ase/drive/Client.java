package com.ase.drive;

import com.ase.drive.client.Sync;


public final class Client {


    public static void main(String[] args) {
        String serverFolder = "serverFileHolder";
//        String serverURL = "44.204.77.26:8080";
        String serverURL = "127.0.0.1:8080";
        String clientDirectory = "";
		if(args.length==1) {
			clientDirectory = args[0];
		}
		else {
	        System.err.println("Please enter the client directory path");
	        return;
		}
		System.out.println("Server URL: "+ serverURL);
		System.out.println("Client Folder: "+ clientDirectory);
        Sync synchronizer = new Sync(serverURL, clientDirectory, serverFolder);
        synchronizer.loopStarting();
    }
}
