package com.fictionNote.controller;

import java.io.OutputStream;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fictionNote.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fictionNote.model.Result;
import com.fictionNote.model.User;
import com.fictionNote.repository.UserRepository;
import com.fictionNote.service.UserService;
import com.fictionNote.utils.ImageUtilDateBase;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;

@Controller
public class UserController {
	public static final String COLLECTION = "portrait";
	@Autowired
	UserService userService;
	@Autowired
	UserRepository userRepository;
	
	@RequestMapping(value="/loginUser",method={ RequestMethod.POST})
	@ResponseBody
	public Result<User> loginUser(HttpServletRequest request) {
		String name = request.getParameter("username");
		String password = request.getParameter("password");
		User user = new User();
		if(userRepository.findByUserName(name) != null){
			user = userRepository.findByUserNameAndPassword(name, password);
			if(user != null){
				//String loginTime[] = user.getLoginTime();
				//loginTime[loginTime.length] = DateUtils.dateToString(new Date(), DateUtils.patternA);
				return new Result<User>(100, "success", user);
			}else{
				return new Result<User>(101, "Please input right password", null);
			}
		}
		return new Result<User>(102, "Please input right username", null);
	}
	
	@RequestMapping(value="/getUserInfo",method={ RequestMethod.GET})
	@ResponseBody
	public User usrInfo(@RequestParam(value="name", required=true) String name) {
		User user = userRepository.findByUserName(name);
		user.setPassword("");
		return user;
	}
	/*@RequestMapping(value = "/findComp/{id}",method ={ RequestMethod.GET })
	@ResponseBody
	public Result findCompanyById(@PathVariable String id)*/

	@RequestMapping(value="/getPortrait", method={ RequestMethod.GET})
	public void getImg(@RequestParam(value="filename", required=false) String filename, HttpServletResponse response) throws Exception{
		OutputStream os = response.getOutputStream();
		String[] dbUrl = ImageUtilDateBase.getPropertise();
		int port = Integer.parseInt(dbUrl[1]);
		Mongo mg = new Mongo(dbUrl[0],port); //connect mongo
		try {
			DB db = mg.getDB(ImageUtilDateBase.DBNAME);
	        GridFS gridFS = new GridFS(db, COLLECTION);
	        GridFSDBFile gridFSDBFile = gridFS.findOne(filename);
	        if (gridFSDBFile != null) {           
	        	response.setContentType(gridFSDBFile.getContentType());
                // 获取原文件名  
                String name = (String) gridFSDBFile.get("filename");
                String fileName = new String(name.getBytes("GBK"), "ISO8859-1");                  
                // 设置下载文件名  
                response.addHeader("Content-Disposition", "attachment; filename=\"" + gridFSDBFile.get("contentType") + "\"");   
                // 向客户端输出文件  
                gridFSDBFile.writeTo(os);
                os.flush();  
                os.close();  
            }  
		} catch (Exception e) {
			System.out.println(e);
		}finally {
			mg.close();
		}
	}
	
	@RequestMapping(value="/uploadPortrait", method={ RequestMethod.POST})
	@ResponseBody
	public String saveImg(@RequestParam(value="upfile", required=false) MultipartFile upfile) throws Exception{
		return ImageUtilDateBase.saveImage(upfile, COLLECTION);
	}
}
