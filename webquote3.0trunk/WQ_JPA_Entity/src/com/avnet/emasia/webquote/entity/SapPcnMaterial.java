package com.avnet.emasia.webquote.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

@Entity
@Table(name = "SAP_PCN_MATERIAL")
public class SapPcnMaterial implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7751812196083673287L;
	@Id
	@Column(name = "MATNR")
	private String sapPartNumber;
	@Version
	@Column(name = "\"VERSION\"")
	private Integer version;
	@Column(name ="MTART")
	 private String mtART;
    @Column(name ="LVORM")
	 private String lvORM;
	@Column(name ="MSTAV")
	 private String msTAV;
     @Column(name ="MFRNR")  
	 private String mfRNR;
    @Column(name ="MFRPN")  
	 private String mfRPN;
    @Column(name ="MAKTX")   
	 private String maKTX;
    @Column(name ="ZZPROD_CAT") 
	 private String zzPRODCAT;
	 @Column(name ="PRDHA")   
	 private String prDHA;
	 @Column(name ="ZZCMC_KEY") 
	 private String zzCMCKEY;
	 @Column(name ="ZZCOMM") 
	 private String zzCOMM;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE")
	private Date createdDate;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "UPDATE_DATE")
	private Date updatedDate;
	
	@Column(name ="CREATED_BY")   
	private String createdBy;
	  
	@Column(name ="UPDATED_BY")   
    private String updatedBy;
	  
	public String getSapPartNumber() {
		return sapPartNumber;
	}
	public void setSapPartNumber(String sapPartNumber) {
		this.sapPartNumber = sapPartNumber;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public String getMTART() {
		return mtART;
	}
	public void setMTART(String mTART) {
		mtART = mTART;
	}
	public String getLVORM() {
		return lvORM;
	}
	public void setLVORM(String lVORM) {
		lvORM = lVORM;
	}
	public String getMSTAV() {
		return msTAV;
	}
	public void setMSTAV(String mSTAV) {
		msTAV = mSTAV;
	}
	public String getMFRNR() {
		return mfRNR;
	}
	public void setMFRNR(String mFRNR) {
		mfRNR = mFRNR;
	}
	public String getMFRPN() {
		return mfRPN;
	}
	public void setMFRPN(String mFRPN) {
		mfRPN = mFRPN;
	}
	public String getMAKTX() {
		return maKTX;
	}
	public void setMAKTX(String mAKTX) {
		maKTX = mAKTX;
	}
	public String getZZPRODCAT() {
		return zzPRODCAT;
	}
	public void setZZPRODCAT(String zZPRODCAT) {
		zzPRODCAT = zZPRODCAT;
	}
	public String getPRDHA() {
		return prDHA;
	}
	public void setPRDHA(String pRDHA) {
		prDHA = pRDHA;
	}
	public String getZZCMCKEY() {
		return zzCMCKEY;
	}
	public void setZZCMCKEY(String zZCMCKEY) {
		zzCMCKEY = zZCMCKEY;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public Date getUpdatedDate() {
		return updatedDate;
	}
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	public String getZZCOMM() {
		return zzCOMM;
	}
	public void setZZCOMM(String zZCOMM) {
		zzCOMM = zZCOMM;
	}
	
	
	
}
