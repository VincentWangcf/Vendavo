package com.avnet.emasia.webquote.masterData.ejb.job;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.jboss.ejb3.annotation.TransactionTimeout;

import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.masterData.ejb.remote.ReferenceMarginPatchRemote;
import com.avnet.emasia.webquote.quote.ejb.MyQuoteSearchSB;
import com.avnet.emasia.webquote.quote.ejb.QuoteSB;


/**
 * @author Tonmy,Li(906893)
 * @Created on 2014-02-28
 * Reference Margin data patch
 */
@Stateless
@Remote(ReferenceMarginPatchRemote.class)
public class ReferenceMarginPatch implements ReferenceMarginPatchRemote
{
	private static Logger logger = Logger.getLogger(ReferenceMarginPatch.class.getName());
    @EJB
    private MyQuoteSearchSB myQuoteSearchSB;
    @EJB
    private QuoteSB quoteSB;
    
  //For control-M
    @TransactionTimeout(value = 30000, unit = TimeUnit.SECONDS) 
    @Override
    public int executeTask()
    {
        int returnInt = 1; 
    	try
        {
    		logger.info("ReferenceMarginPatch start to query the objects.");
    		
    		List<QuoteItem> results = myQuoteSearchSB.queryReferenceMarginPatchQI();
    		if(results!=null && results.size()>0)
    		{
    			logger.info("ReferenceMarginPatch total count: "+results.size());
    		}
    		logger.info("ReferenceMarginPatch start to calculate teh reference margin.");
    		
    		List<QuoteItem> newRsult = myQuoteSearchSB.updateReferenceMarginForSubmission(results);
    		
    		logger.info("ReferenceMarginPatch start to update objects.");
    		for(QuoteItem qi : newRsult)
    		{
    		  quoteSB.updateQuoteItem(qi);
    		}

    		
        }
    	catch(Exception e)
        {
    		logger.log(Level.SEVERE, "Exception occoured while executing task in ReferenceMarginPatch in ReferenceMarginePatch : " + e.getMessage(), e);
    		returnInt = 0 ;
        }
        return returnInt;
    }
    


	
}
