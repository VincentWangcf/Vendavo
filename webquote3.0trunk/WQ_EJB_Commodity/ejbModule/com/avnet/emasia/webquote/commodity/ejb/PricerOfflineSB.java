package com.avnet.emasia.webquote.commodity.ejb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.avnet.emasia.webquote.commodity.constant.PricerConstant;
import com.avnet.emasia.webquote.entity.PricerOffline;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.quote.ejb.SystemCodeMaintenanceSB;

@Stateless
@LocalBean
public class PricerOfflineSB {
	  static final Logger LOG= Logger.getLogger(PricerOfflineSB.class.getSimpleName());
	  
	@PersistenceContext(unitName="Server_Source")
	EntityManager em;
	
	@EJB
	SystemCodeMaintenanceSB sysCodeMaintSB;
	
	public void saveToDb(User user,String action,String pricerType,String fileName){
		PricerOffline pricerOffline = new PricerOffline();
		pricerOffline.setBizUnit(user.getDefaultBizUnit().getName());
		pricerOffline.setCreatedOn(new Date());
		pricerOffline.setLastUpdatedOn(new Date());
		pricerOffline.setEmployeeId(user.getEmployeeId());
		pricerOffline.setEmployeeName(user.getName());
		pricerOffline.setAction(action);
		pricerOffline.setPricerType(pricerType);
		pricerOffline.setSendFlag(false);
		pricerOffline.setFileName(fileName);
		 em.persist(pricerOffline);;
	}
	
	public void setSendFlag(PricerOffline pricerOffline,boolean sendFlag){		
		pricerOffline.setSendFlag(sendFlag);
		pricerOffline.setLastUpdatedOn(new Date());
		em.merge(pricerOffline);
		em.flush();
	}
	
	public List<PricerOffline> findPricerOffline(){
		String sql = "select q from PricerOffline q where q.sendFlag =0";
		Query query = em.createQuery(sql, PricerOffline.class);
		return query.getResultList();
	}
	
	/*public List<PricerOffline> test_findPricerOfflineById(long id){
		return Arrays.asList(em.find(PricerOffline.class, id));
	}*/


	public void saveToDisk(String pricerUploadOfflinePath,String fileName, byte[] contents) {
		FileOutputStream fos = null;
		try { 
	           String address = InetAddress.getLocalHost().getHostName().toString();
	           if("cis2115vmts".equalsIgnoreCase(address)) { 
	        	   pricerUploadOfflinePath = "C:\\david\\sharefolder\\tempd"+File.separator;
	           }
	        } catch (Exception e) { 
	            e.printStackTrace(); 
	        } 
		File f = new File(pricerUploadOfflinePath +fileName  );
		try {
			fos = new FileOutputStream(f);
			fos.write(contents);
		} catch (IOException e) {
		LOG.log(Level.SEVERE, "Error in save to disk, at path : "+pricerUploadOfflinePath+" , file name : "+fileName+" , error message "+e.getMessage(), e);
		}  finally {
			try {
				fos.close();
			} catch (IOException e) {
				LOG.log(Level.SEVERE, "Error in closing FileOutputStream "+e.getMessage(), e);
			}
		}

	}
	
	public void markProcessedFile(String pricerUploadOfflinePath,String fileName){

		File f = new File(pricerUploadOfflinePath +fileName  );
		f.renameTo(new File(pricerUploadOfflinePath +PricerConstant.MARK_PROCESSED_UPLOAD_FILE+fileName  )); 
	}
	
	public void deleteFile(String pricerUploadOfflinePath){
		File dir = new File(pricerUploadOfflinePath);
		File[] files = dir.listFiles();
		long times = new Date().getTime();
		for(int i=0;i<files.length;i++){
			File file = files[i];
			if(file!=null&&file.getName().indexOf(PricerConstant.MARK_PROCESSED_UPLOAD_FILE)!=-1&&file.lastModified()<times -10*24*60*60*1000){
				file.delete();
			}
		}
	}

}
