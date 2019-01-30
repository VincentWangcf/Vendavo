package com.avnet.emasia.webquote.masterData.ejb.job;

import java.util.logging.Logger;

import javax.ejb.Remote;
import javax.ejb.Stateless;

import com.avnet.emasia.webquote.masterData.ejb.remote.SalesCostPricerToSapRemote;

/**
 * @author WingHong, Wong
 * @Created on 2017-11-21
 */
@Stateless
@Remote(SalesCostPricerToSapRemote.class)
public class SalesCostPricerToSapJob implements SalesCostPricerToSapRemote {
	private static Logger logger = Logger.getLogger(SalesCostPricerToSapJob.class.getName());

	@Override
	public int executeTask() {
		return 1;
	}

}
