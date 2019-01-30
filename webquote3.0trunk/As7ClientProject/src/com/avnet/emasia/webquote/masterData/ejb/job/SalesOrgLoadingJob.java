package com.avnet.emasia.webquote.masterData.ejb.job;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.Timer;


import com.avnet.emasia.webquote.masterData.ejb.remote.SalesOrgRemote;

/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-01-23
 * Upload sequence:  1.
 */

@Stateless
@Remote(SalesOrgRemote.class)
public class SalesOrgLoadingJob implements SalesOrgRemote
{
	private static Logger logger = Logger.getLogger(SalesOrgLoadingJob.class.getName());
    

    @Override
    public int executeTask()
    {
    	return 1;
    }
    

}
