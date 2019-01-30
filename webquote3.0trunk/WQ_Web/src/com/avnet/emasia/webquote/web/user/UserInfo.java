package com.avnet.emasia.webquote.web.user;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.web.security.WQUserDetails;

/**
 * @author Lin, Tough(901518) Created on 2013-4-14
 */

public class UserInfo {
	private static Logger logger = Logger.getLogger(UserInfo.class.getName());
	public static User getUser() {

		try {
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if(auth!=null){
				
				WQUserDetails userDetails = (WQUserDetails) auth.getPrincipal();
				if (userDetails != null) {
					return userDetails.getUser();
				}
			}

		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error occured while reset, Reason for failure: "+MessageFormatorUtil.getParameterizedStringFromException(e), e);

		}

		return null;
	}

}
