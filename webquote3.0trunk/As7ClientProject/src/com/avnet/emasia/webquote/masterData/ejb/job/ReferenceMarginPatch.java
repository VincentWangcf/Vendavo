package com.avnet.emasia.webquote.masterData.ejb.job;


import java.util.logging.Logger;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import com.avnet.emasia.webquote.masterData.ejb.remote.DrmsAgpReaRemote;
import com.avnet.emasia.webquote.masterData.ejb.remote.ReferenceMarginPatchRemote;


/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-12-06
 * 
 */
@Stateless
@Remote(ReferenceMarginPatchRemote.class)
public class ReferenceMarginPatch implements ReferenceMarginPatchRemote
{
	private static Logger logger = Logger.getLogger(ReferenceMarginPatch.class.getName());
 
    @Override
    public int executeTask()
    {
          return 1;
    }

}
