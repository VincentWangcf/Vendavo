package com.avnet.emasia.webquote.entity;
import java.io.Serializable;
import javax.persistence.*;
import java.util.logging.Logger;


/**
 * The persistent class for the REPORT_RECIPIENT database table.
 * 
 */
@Entity
@Table(name="REPORT_RECIPIENT")
public class ReportRecipient implements Serializable {

	private static final long serialVersionUID = 1L;
	static Logger logger = Logger.getLogger("ReportRecipient");
	@Id
	@SequenceGenerator(name="REPORT_RECIPIENT_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="REPORT_RECIPIENT_ID_GENERATOR")
	@Column(unique=true, nullable=false, precision=18)
	private long id;

	@Column(name="REPORT_TYPE")
	private String reportType;
	
	@ManyToOne
	@JoinColumn(name="USER_ID", nullable=false)
	private User user;
	
	@ManyToOne
	@JoinColumn(name="REGION", nullable=false)
	private BizUnit bzUnit;

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public String getReportType()
	{
		return reportType;
	}

	public void setReportType(String reportType)
	{
		this.reportType = reportType;
	}

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}

	public BizUnit getBzUnit()
	{
		return bzUnit;
	}

	public void setBzUnit(BizUnit bzUnit)
	{
		this.bzUnit = bzUnit;
	}	
}