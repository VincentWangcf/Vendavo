package com.avnet.emasia.webquote.user.ejb;

/**
 * @author Lin, Tough(901518)
 * Created on 2013-2-4
 */

public class RoleNameExistException extends Exception{
	
	public RoleNameExistException(){
		super();
	}
	
	public RoleNameExistException(String message){
		super(message);
	}
	
}
