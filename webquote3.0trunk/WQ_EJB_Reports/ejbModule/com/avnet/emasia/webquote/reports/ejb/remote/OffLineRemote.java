package com.avnet.emasia.webquote.reports.ejb.remote;


import java.rmi.RemoteException;

import com.avnet.emasia.webquote.vo.OfflineReportParam;

public interface OffLineRemote {

	    void sendOffLineReport(OfflineReportParam param) throws Exception;
}
