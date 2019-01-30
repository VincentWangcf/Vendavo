package com.avnet.emasia.webquote.web.quote.cache;

import java.io.Serializable;
import java.util.TreeMap;
import java.util.List;

import com.avnet.emasia.webquote.entity.Customer;

public class CustomerCacheManager implements Serializable {

	private static final TreeMap SOLD_TO_VS_END_CUSTOMER_CACHE = new TreeMap();
	
    public static List<Customer> getEndCustomerBySoldToCustomer(String soldToCustomerNumber) {
        return (List<Customer>) SOLD_TO_VS_END_CUSTOMER_CACHE.get(soldToCustomerNumber);
    }

    public static void putSoldToVsEndCustomers(String soldToCustomerNumber, List<Customer> endCustomers) {
    	SOLD_TO_VS_END_CUSTOMER_CACHE.put(soldToCustomerNumber, endCustomers);
    }	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	
	private static final List<String> salesOrgs = SalesOrgCacheManager.getAllSalesOrgCode();

	private static final TreeMap customerCache = new TreeMap();
    private static final TreeMap customerNameToCustomerNumberList = new TreeMap();

    public static CustomerVO getCustomer(String customerNumber) {
        return (CustomerVO) customerCache.get(customerNumber);
    }

    public static void putCustomer(CustomerVO customer) {
    	CustomerVO customerInCache = null;
    	if(customerCache.containsKey(customer.getCustomerNumber())){
    		customerInCache = (CustomerVO) customerCache.get(customer.getCustomerNumber());
    		List<String> salesOrgList = customerInCache.getSalesOrgList();
        	if(salesOrgList.size() < salesOrgs.size()){
        		salesOrgList.add(salesOrgs.get(salesOrgList.size()));
        	}
        	customerInCache.setSalesOrgList(salesOrgList);
    		customerCache.put(customer.getCustomerNumber(), customerInCache);
    	} else {
    		List<String> salesOrgList = new ArrayList<String>();
    		salesOrgList.add(salesOrgs.get(salesOrgList.size()));
    		customer.setSalesOrgList(salesOrgList);
    		customerCache.put(customer.getCustomerNumber(), customer);
    	}
    }

    public static void putCustomerNameToCustomerNumberList(String customerNumber, String customerName) {
    	List<String> list = null;
    	if(customerNameToCustomerNumberList.containsKey(customerName)){
    		list = (List<String>) customerNameToCustomerNumberList.get(customerName);
    	} else {
    		list = new ArrayList<String>();
    	}
    	if(!list.contains(customerNumber)){
    		list.add(customerNumber);
    		customerNameToCustomerNumberList.put(customerName, list);
    	}
    }
    
    public static List<String> getCustomerNumberListByCustomerName(String customerName){
    	return (List<String>) customerNameToCustomerNumberList.get(customerName);
    }

    public static List<String> getMatchedCustomerNameList(String word){
    	Set<String> keys =  customerNameToCustomerNumberList.keySet();
    	List<String> suggested = new ArrayList<String>();
    	  
    	for (String key : keys) {
    		if(key.contains(word))
    			suggested.add(key);
    	}
    	
    	return suggested;
    }

    public static List<String> getMatchedCustomerNumberList(String word){
    	Set<String> keys =  customerCache.keySet();
    	List<String> suggested = new ArrayList<String>();
    	  
    	for (String key : keys) {
    		if(key.contains(word))
    			suggested.add(key);
    	}
    	
    	return suggested;
    }
    
	public static List<CustomerVO> getSearchedCustomerNameList(String customerTypeSearch, String customerNameSearch) {
    	Set<String> keys =  customerNameToCustomerNumberList.keySet();
    	List<CustomerVO> suggested = new ArrayList<CustomerVO>();
    	  
    	for (String key : keys) {
    		List<String> customerNumbers = null;
    		if((customerNameSearch.startsWith("*") && customerNameSearch.endsWith("*") && key.contains(customerNameSearch.replaceAll("\\*", "")))
        		 || (customerNameSearch.startsWith("*") && key.endsWith(customerNameSearch.replaceAll("\\*", "")))
        		 || (customerNameSearch.endsWith("*") && key.startsWith(customerNameSearch.replaceAll("\\*", "")))){
        		 customerNumbers = getCustomerNumberListByCustomerName(key);
    		}
    		if(customerNumbers != null){
    			for(String customerNumber : customerNumbers){
    				CustomerVO customer = getCustomer(customerNumber);
    				if(customerTypeSearch != null && customer.getCustomerType() != null && customer.getCustomerType().equals(customerTypeSearch)){
    					suggested.add(customer);
    				}
    			}
    		}
    	}
    	
    	return suggested;
	}    
    */
    static {
    }

}
