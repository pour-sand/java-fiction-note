package com.fictionNote.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

public class ImageUtilDateBase {
	private static final Logger logger = LoggerFactory.getLogger(ImageUtilDateBase.class);
	public static final String DBNAME = "testdatabase";
	public ImageUtilDateBase() {
		
	}
	
	public static String[] getPropertise() throws IOException {
		Properties prop = new Properties();   
		InputStream in = new BufferedInputStream (new FileInputStream("src/main/resources/imagedb.properties"));
		prop.load(in);
		String[] result = new String[2];
		result[0] = prop.getProperty("ip");
		result[1] = prop.getProperty("port");
		return result;
		
	}
	  
	
	public static String saveImage(MultipartFile file, String collection) throws Exception {
		
		String[] dbUrl = getPropertise();
		int port = Integer.parseInt(dbUrl[1]);
		int size = file.getInputStream().available();
		String fileName = new Date().getTime() + "";//some path to save image in server
		Mongo mg = new Mongo(dbUrl[0],port);
		DB db = mg.getDB(DBNAME);
        GridFS gridFS = new GridFS(db, collection);
    	GridFSInputFile gfs;
		try {
			gfs = gridFS.createFile(file.getInputStream());
			gfs.setContentType(file.getContentType());
			gfs.setFilename(fileName);
			gfs.put("aliases", file.getOriginalFilename());
			gfs.save();
		} catch (IOException e) {
			logger.info(e.getMessage());
		}finally {
			mg.close();
		}
		if(size ==0) {
			return "";
		}
		return fileName;//return the fileName
	   
	}
	
	public static String[] saveImages(MultipartFile[] file, HttpServletRequest request, String collection) throws Exception {
		String[] dbUrl = getPropertise();
		int port = Integer.parseInt(dbUrl[1]);
		//some path to save image in server
		Mongo mg = new Mongo(dbUrl[0],port);
		DB db = mg.getDB(DBNAME);
        GridFS gridFS = new GridFS(db, collection);
    	GridFSInputFile gfs;
    	String[] introPhoto = new String[file.length];
    	int i = 0;
		try {
			for(MultipartFile photo : file) {
			    if(photo.getBytes().length != 0){
    				gfs = gridFS.createFile(photo.getInputStream());
    				String fileName = new Date().getTime() + "";
    				Thread.sleep(1);
    				gfs.put("filename", fileName);
    				gfs.save();
    				introPhoto[i] = fileName;
			    }
				i++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.info(e.getMessage());
		}finally {
			mg.close();
		}

			return introPhoto;//return the fileName
	   
	}
	
	public static boolean delImage(String fileName, String collection) throws Exception{
		String[] dbUrl = getPropertise();
		int port = Integer.parseInt(dbUrl[1]);
		Mongo mg = new Mongo(dbUrl[0],port); //connect mongo
		DB db = mg.getDB(DBNAME);
		GridFS gridFS = new GridFS(db, collection);
		if(getImage(fileName, collection)!=null) {
			gridFS.remove(fileName);
			return true;
		}else return false;
	}
	public static GridFSDBFile getImg(String filename, String collection) throws Exception {
		String[] dbUrl = getPropertise();
		int port = Integer.parseInt(dbUrl[1]);
		Mongo mg = new Mongo(dbUrl[0],port); //connect mongo
		try {
			DB db = mg.getDB(DBNAME);
	        GridFS gridFS = new GridFS(db, collection);
	        System.out.println("oooooo"+gridFS.getFileList().size());
	        return gridFS.findOne(filename);
		} catch (Exception e) {
			logger.info(e.getMessage());
		}finally {
			mg.close();
		}
        return null;
	}
	public static String getImage(String filename, String collection) throws Exception {
		String[] dbUrl = getPropertise();
		int port = Integer.parseInt(dbUrl[1]);
		Mongo mg = new Mongo(dbUrl[0],port); //connect mongo
		DB db = mg.getDB(DBNAME);
        GridFS gridFS = new GridFS(db, collection);
        GridFSDBFile fsdFile = gridFS.findOne(filename);
        String path = System.getProperty("user.dir")+"/src/main/resources/static/images/"+filename;
        File file = new File(path);
        try {
        	if(file.exists()) {
        		return "/wahaha/"+filename;
        	}else if(fsdFile!=null) {
				fsdFile.writeTo(file);
				Thread.sleep(20);
				return "/wahaha/"+filename;
        	}
		} catch (IOException e) {
			logger.info(e.getMessage());
		}finally {
			mg.close();
		}
        return null;
	}
	
	public static String[] getImages(String[] filename, String collection) throws Exception {
		String[] res = new String[filename.length]; 
		int i = 0;
		for(String a : filename) {
			res[i] = getImage(a, collection);
			i++;
		}
		return res;
	}
}
