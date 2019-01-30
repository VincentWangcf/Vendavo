package com.avnet.emasia.webquote.masterData.ejb.job;



import java.util.logging.Logger;


import javax.ejb.Remote;
import javax.ejb.Stateless;
import com.avnet.emasia.webquote.masterData.ejb.remote.MaterialRemote;

/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-01-23
 * Upload sequence:  3.
 */
@Stateless
@Remote(MaterialRemote.class)
public class MaterialLoadingJob implements MaterialRemote
{
	private static Logger logger = Logger.getLogger(MaterialLoadingJob.class.getName());
   
    @Override
    public int executeTask()
    {
    	return 1;
    }
  
	
}