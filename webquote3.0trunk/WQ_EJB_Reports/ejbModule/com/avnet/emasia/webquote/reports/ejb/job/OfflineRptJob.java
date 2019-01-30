package com.avnet.emasia.webquote.reports.ejb.job;

import java.util.logging.Logger;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import com.avnet.emasia.webquote.reports.ejb.remote.OfflineRptRemote;


/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-01-23
 * Upload sequence:  2.
 */
@Stateless
@Remote(OfflineRptRemote.class)
public class OfflineRptJob implements OfflineRptRemote
{
	private static Logger logger = Logger.getLogger(OfflineRptJob.class.getName());
  
    @Override
    public int executeTask(long requestId)
    {
          return 1;
    }



	
}
