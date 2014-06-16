/*
 * Copyright 2010 Srikanth Reddy Lingala  
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */

package in.swifiic.examapp;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class ExtractAllFiles {

	private String pathName;
	private String password;
	private String courseCode;

	public ExtractAllFiles(String pathName, String password, String courseCode) {
		this.pathName = pathName;
		this.password = password;
		this.courseCode = courseCode;
	}

	public boolean extract() {
		try {
			// Initiate ZipFile object with the path/name of the zip file.
			ZipFile zipFile = new ZipFile(pathName + courseCode + ".zip");
			if (zipFile.isEncrypted()) {
				zipFile.setPassword(password);
			}

			// Extracts all files to the path specified
			zipFile.extractAll(pathName + courseCode + "/");

			return true;
		} catch (ZipException e) {
			e.printStackTrace();
			return false;
		}
	}

}