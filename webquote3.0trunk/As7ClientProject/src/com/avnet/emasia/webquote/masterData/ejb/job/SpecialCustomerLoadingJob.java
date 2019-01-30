package com.avnet.emasia.webquote.masterData.ejb.job;


import java.util.logging.Logger;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import com.avnet.emasia.webquote.masterData.ejb.remote.SpecialCustomerRemote;


/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-12-06
 * 
 */
@Stateless
@Remote(SpecialCustomerRemote.class)
public class SpecialCustomerLoadingJob implements SpecialCustomerRemote
{
	private static Logger logger = Logger.getLogger(SpecialCustomerLoadingJob.class.getName());
 
    @Override
    public int executeTask()
    {
          return 1;
    }

}
