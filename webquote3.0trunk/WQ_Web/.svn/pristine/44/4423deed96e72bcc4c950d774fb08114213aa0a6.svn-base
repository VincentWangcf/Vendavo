package com.avnet.emasia.webquote.web.quote.cache;

import java.io.Serializable;
import java.util.List;
import java.util.TreeMap;

import com.avnet.emasia.webquote.entity.VendorReport;

public class VendorReportCacheManager implements Serializable {

    private static final TreeMap VENDOR_REPORT_CACHE = new TreeMap();

    public static List<VendorReport> getVendorReportHistory(String partNumber) {
        return (List<VendorReport>) VENDOR_REPORT_CACHE.get(partNumber);
    }

    public static void putVendorReportHistory(String partNumber, List<VendorReport> vendorReportList) {
    	VENDOR_REPORT_CACHE.put(partNumber, vendorReportList);
    }
    
    public static boolean containsKey(String partNumber){
    	return VENDOR_REPORT_CACHE.containsKey(partNumber);
    }
    
    static {
    }

}
