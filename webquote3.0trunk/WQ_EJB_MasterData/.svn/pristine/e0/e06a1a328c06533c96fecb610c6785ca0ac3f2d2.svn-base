package com.avnet.emasia.webquote.masterData.ejb;



import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import com.avnet.emasia.webquote.entity.LoadingJobTrack;

/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-03-14
 * 
 */
@Stateless
public class LoadingJobTrackSB {
	
    @PersistenceContext(unitName = "Server_Source")
    private EntityManager em;
    private static Logger logger = Logger.getLogger(LoadingJobTrackSB.class.getName());
    public LoadingJobTrackSB() {

	}
    

    
    /*
     * Persist Error LoadingJobCheck.
     */
    public void createLoadingJobCheck(LoadingJobTrack ljb)
    {
    	logger.info("call createLoadingJobCheck method");
         em.persist(ljb);
    }
    
   

  
}
