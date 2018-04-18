package com.fictionNote.controller;


import java.io.OutputStream;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fictionNote.model.Book;
import com.fictionNote.repository.BookRepository;
import com.fictionNote.service.BookService;
import com.fictionNote.utils.DateUtils;
import com.fictionNote.utils.ImageUtilDateBase;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;

@Controller
public class BookController {
	public static final String COLLECTION = "bookitem";
	@Autowired
	BookRepository bookRepository;
	@Autowired
	BookService bookService;
	
	@RequestMapping(value="/admin/allBooks",method={ RequestMethod.GET})
	@ResponseBody
	public List<Book> getBooks(){
		return bookRepository.findAll();
	}
	
	@RequestMapping(value="/allBooks",method={ RequestMethod.POST})
	@ResponseBody
	public List<Book> getCheckedBooks(@RequestParam(value="uid") String uid){
		return bookService.checkList(bookRepository.findByIscheck("1"), uid);
	}
	
	@RequestMapping(value="/uploadImgs", method={ RequestMethod.POST})
	@ResponseBody
	public String saveImgs(@RequestParam(value="upfiles", required=false) MultipartFile[] upflies){
		Map<String, String> map = new HashMap<String, String>();
		if(upflies != null && upflies.length > 0){
			for(int i=0; i<upflies.length; i++){
				MultipartFile upfile = upflies[i];
				
			}
		}
		return null;
	}
	@RequestMapping(value="/uploadImg", method={ RequestMethod.POST})
	@ResponseBody
	public String saveImg(@RequestParam(value="upfile", required=false) MultipartFile upfile) throws Exception{
		System.out.println(upfile == null);
		return ImageUtilDateBase.saveImage(upfile, COLLECTION);
	}
	
	@RequestMapping(value="/getImg", method={ RequestMethod.GET})
	public void getImg(@RequestParam(value="filename", required=false) String filename, HttpServletResponse response) throws Exception{
		OutputStream os = response.getOutputStream();
		String[] dbUrl = ImageUtilDateBase.getPropertise();
		int port = Integer.parseInt(dbUrl[1]);
		Mongo mg = new Mongo(dbUrl[0],port); //connect mongo
		try {
			DB db = mg.getDB(ImageUtilDateBase.DBNAME);
	        GridFS gridFS = new GridFS(db, COLLECTION);
	        System.out.println("====="+gridFS.getFileList().size());
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
	
	@RequestMapping(value="/delBook", method={ RequestMethod.POST})
	@ResponseBody
	public String del(@RequestParam(value="id") String id) throws Exception{
		Book book = bookRepository.findById(id);
		bookRepository.delete(book);
		return null;
	}

	@RequestMapping(value="/addBook", method={ RequestMethod.POST})
	@ResponseBody
	public String addBook(@RequestBody Book book) throws Exception{
		//System.out.println("===="+book+"====");
		book.setTime(DateUtils.dateToString(new Date(), DateUtils.patternA));
		book.setDtime(new Date());
		book.setIscheck("0");
		bookRepository.save(book);/**/
		return "Success";
	}

	@RequestMapping(value="/getBook", method={ RequestMethod.POST})
	@ResponseBody
	public Book get(@RequestParam(value="bid") String id) throws Exception{
		return bookRepository.findById(id);
	}
}
