/*
 * Copyright 2010 Srikanth Reddy Lingala  
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 */

package in.swifiic.examApp;

import java.io.File;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

/**
 * adding a folder to zip file
 */
public class AddFolder {

	public AddFolder(String folder, String zipPath, String zipName, String pass) {

		try {
			// Initiate ZipFile object with the path/name of the zip file.
			ZipFile zipFile = new ZipFile(zipPath + zipName + ".zip");

			// Folder to add
			String folderToAdd = folder;

			// Initiate Zip Parameters which define various properties such
			// as compression method, etc.
			ZipParameters parameters = new ZipParameters();

			// set compression method to store compression
			parameters. setCompressionMethod(Zip4jConstants.COMP_DEFLATE);

			// Set the compression level
			parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
			// Set the encryption flag to true
			// If this is set to false, then the rest of encryption properties
			// are ignored
			if (pass != "" || pass != null)
				parameters.setEncryptFiles(true);
			else
				parameters.setEncryptFiles(false);

			// Set the encryption method to 256 bit AES Encryption
			parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
			parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);

			// Set password
			parameters.setPassword(pass);

			// Add folder to the zip file
			zipFile.addFolder(folderToAdd, parameters);

			File fpath = new File(folder);
			deleteDirectory(fpath);

		} catch (ZipException e) {
			e.printStackTrace();
		}
	}

	static public boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

}
