package com.avnet.emasia.webquote.masterData.ejb.job;


import java.util.logging.Logger;


import javax.ejb.Remote;
import javax.ejb.Stateless;


import com.avnet.emasia.webquote.masterData.ejb.remote.MfrRemote;


/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-01-23
 * 
 */
@Stateless
@Remote(MfrRemote.class)
public class MfrLoadingJob implements MfrRemote
{
	private static Logger logger = Logger.getLogger(MfrLoadingJob.class.getName());

    
    @Override
    public int executeTask()
    {
    	return 1;
    }
    
   
}
