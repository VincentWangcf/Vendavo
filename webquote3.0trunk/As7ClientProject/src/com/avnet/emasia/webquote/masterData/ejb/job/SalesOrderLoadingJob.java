package com.avnet.emasia.webquote.masterData.ejb.job;


import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.Remote;
import javax.ejb.Stateless;


import com.avnet.emasia.webquote.masterData.ejb.remote.SalesOrderRemote;

/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-03-15
 * Upload sequence:  1.
 */

@Stateless
@Remote(SalesOrderRemote.class)
public class SalesOrderLoadingJob implements SalesOrderRemote
{
	private static Logger logger = Logger.getLogger(SalesOrderLoadingJob.class.getName());

    @Override
    public int executeTask()
    {
         return 1;
    }
    
}
