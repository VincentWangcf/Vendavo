package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.apache.commons.lang.StringUtils;

import com.avnet.emasia.webquote.dp.xml.requestquote.WebQuoteLineRequest;
import com.avnet.emasia.webquote.dp.xml.requestquote.WebRequestObject;


/**
 * The persistent class for the DP_RFQ database table.
 * 
 */
@Entity
@Table(name="DP_RFQ")
public class DpRfq implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOG = Logger.getLogger(DpRfq.class.getName());

	@Id
	@SequenceGenerator(name="DPRFQ_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="DPRFQ_ID_GENERATOR")
	private long id;

	@Column(name="CONTACT_EMAIL_ADDRESS")
	private String contactEmailAddress;

	@Column(name="CONTACT_NAME")
	private String contactName;

	@Column(name="CONTACT_PHONE_NO")
	private String contactPhoneNo;
	
	@Temporal(TemporalType.DATE)
	@Column(name="CREATION_TIME")
	private Date creationTime;

	@Column(name="CUSTOMER_TYPE")
	private String customerType;

	@Temporal(TemporalType.DATE)
	@Column(name="LAST_UPDATE_TIME")
	private Date lastUpdateTime;

	@Column(name="\"OPERATION\"")
	private String operation;

	
	@OneToOne
	@JoinColumn(name="QUOTE_ID")
	private Quote quote;

	@Column(name="QUOTE_TYPE")
	private String quoteType;

	@Column(name="REFERENCE_ID")
	private String referenceId;

	@Column(name="SALES_OFFICE")
	private String salesOffice;

	@Column(name="SALES_ORGANIZATION")
	private String salesOrganization;

	@Column(name="SALES_PERSON_ID")
	private String salesPersonId;

	@Column(name="\"SEGMENT\"")
	private String segment;

	@Column(name="SOLD_TO_CUSTOMER_ACCOUNT_NO")
	private String soldToCustomerAccountNo;

	@Column(name="SOLD_TO_CUSTOMERNAME")
	private String soldToCustomername;

	@Column(name="SOURCE")
	private String source;

	@Column(name="RFQ_CREATION_TIMESTAMP")
	private Timestamp rfqCreationTimestamp;

	//bi-directional many-to-one association to DpMessage
	@OneToOne
	@JoinColumn(name="DP_MESSAGE_ID")
	private DpMessage dpMessage;

	//bi-directional many-to-one association to DpRfqItem
	@OneToMany(mappedBy="dpRfq", cascade=CascadeType.ALL)
	private List<DpRfqItem> dpRfqItems;
	
	@Version
	@Column(name="\"VERSION\"")
	private Integer version;

	public DpRfq() {
	}
	
	
	public static DpRfq createInstance(WebRequestObject request){
		
		if(request == null){
			return null;
		}
		
		DpRfq dpRfq = new DpRfq();
		
		Date date = new Date();
		
		dpRfq.setContactEmailAddress(request.getContactEmailAddress());
		dpRfq.setContactName(request.getContactName());
		dpRfq.setContactPhoneNo(request.getContactPhoneNo());
		dpRfq.setCreationTime(date);
		
		dpRfq.setCustomerType(request.getCustomerType());
		dpRfq.setLastUpdateTime(date);
		dpRfq.setOperation(request.getOperation());
		dpRfq.setQuote(null);
		dpRfq.setQuoteType(request.getQuoteType());
		dpRfq.setReferenceId(request.getReferenceID());
		dpRfq.setSalesOffice(request.getSalesOffice());
		dpRfq.setSalesOrganization(request.getSalesOrganization());
		dpRfq.setSalesPersonId(request.getSalesPersonId());
		dpRfq.setSegment(request.getSegment());
		dpRfq.setSoldToCustomerAccountNo(StringUtils.stripStart(request.getSoldToCustomerAccountNo(),"0"));
		dpRfq.setSoldToCustomername(request.getSoldToCustomerName());
		dpRfq.setSource(request.getSource());
		
		if(request.getRfqCreationTimestamp() != null){
			try{
				dpRfq.setRfqCreationTimestamp(Timestamp.valueOf(request.getRfqCreationTimestamp()));
			}catch(Exception e){
				LOG.log(Level.WARNING, "cannot convert rfqCreationTimestamp field to TimeStamp:" + request.getRfqCreationTimestamp() + 
						" for reference id" + dpRfq.getReferenceId());
			}
		}
		
		List<DpRfqItem> items = new ArrayList<DpRfqItem>();
		
		if(request.getWebQuoteLineRequest() != null && !request.getWebQuoteLineRequest().isEmpty()){
			for(WebQuoteLineRequest createRfqItemXmlObject : request.getWebQuoteLineRequest()){
				DpRfqItem item = DpRfqItem.createInstance(createRfqItemXmlObject);
				item.setDpRfq(dpRfq);
				items.add(item);
			}
		}
		dpRfq.setDpRfqItems(items);
		
		return dpRfq;
		
	}	

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getContactEmailAddress() {
		return this.contactEmailAddress;
	}

	public void setContactEmailAddress(String contactEmailAddress) {
		this.contactEmailAddress = contactEmailAddress;
	}

	public String getContactName() {
		return this.contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactPhoneNo() {
		return this.contactPhoneNo;
	}

	public void setContactPhoneNo(String contactPhoneNo) {
		this.contactPhoneNo = contactPhoneNo;
	}

	public Date getCreationTime() {
		return this.creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	public String getCustomerType() {
		return this.customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public Date getLastUpdateTime() {
		return this.lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getOperation() {
		return this.operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public Quote getQuote() {
		return this.quote;
	}

	public void setQuote(Quote quote) {
		this.quote = quote;
	}

	public String getQuoteType() {
		return this.quoteType;
	}

	public void setQuoteType(String quoteType) {
		this.quoteType = quoteType;
	}

	public String getReferenceId() {
		return this.referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getSalesOffice() {
		return this.salesOffice;
	}

	public void setSalesOffice(String salesOffice) {
		this.salesOffice = salesOffice;
	}

	public String getSalesOrganization() {
		return this.salesOrganization;
	}

	public void setSalesOrganization(String salesOrganization) {
		this.salesOrganization = salesOrganization;
	}

	public String getSalesPersonId() {
		return this.salesPersonId;
	}

	public void setSalesPersonId(String salesPersonId) {
		this.salesPersonId = salesPersonId;
	}

	public String getSegment() {
		return this.segment;
	}

	public void setSegment(String segment) {
		this.segment = segment;
	}

	public String getSoldToCustomerAccountNo() {
		return this.soldToCustomerAccountNo;
	}

	public void setSoldToCustomerAccountNo(String soldToCustomerAccountNo) {
		this.soldToCustomerAccountNo = soldToCustomerAccountNo;
	}

	public String getSoldToCustomername() {
		return this.soldToCustomername;
	}

	public void setSoldToCustomername(String soldToCustomername) {
		this.soldToCustomername = soldToCustomername;
	}

	public String getSource() {
		return this.source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public DpMessage getDpMessage() {
		return this.dpMessage;
	}

	public void setDpMessage(DpMessage dpMessage) {
		this.dpMessage = dpMessage;
	}

	public List<DpRfqItem> getDpRfqItems() {
		return this.dpRfqItems;
	}

	public void setDpRfqItems(List<DpRfqItem> dpRfqItems) {
		this.dpRfqItems = dpRfqItems;
	}

	public Timestamp getRfqCreationTimestamp() {
		return this.rfqCreationTimestamp;
	}

	public void setRfqCreationTimestamp(Timestamp rfqCreationTimestamp) {
		this.rfqCreationTimestamp = rfqCreationTimestamp;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
}