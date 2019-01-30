package com.avnet.emasia.webquote.masterData.ejb.job;

import javax.ejb.Remote;
import javax.ejb.Stateless;

import com.avnet.emasia.webquote.masterData.ejb.remote.SapDpaCustomerRemote;

@Stateless
@Remote(SapDpaCustomerRemote.class)
public class SapDpaCustomerLoadingJob implements SapDpaCustomerRemote
{

	@Override
	public int executeTask() {
		return 1;
	}

}
