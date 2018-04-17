package com.fictionNote.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Result<T> {
    /** err code. */
    private Integer code;
    /** message */
    private String msg;
    /** detail content */
    private T data;
    
    public Result() {}
    
    public Result(Integer code,String msg) {
    	this.msg = msg;
    	this.code = code;
    }
    
    public Result(Integer code,String msg,T data) {
    	this.msg = msg;
    	this.code = code;
    	this.data = data;
    }

}
