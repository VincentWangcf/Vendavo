package com.avnet.emasia.webquote.web.quote.vo;

import java.io.Serializable;
import java.util.List;

import com.avnet.emasia.webquote.vo.RfqHeaderVO;
import com.avnet.emasia.webquote.vo.RfqItemVO;

public class RfqVO implements Serializable {
	
	private RfqHeaderVO rfqHeader;	
	private List<RfqItemVO> rfqItems;
	
	public RfqHeaderVO getRfqHeader() {
		return rfqHeader;
	}
	public void setRfqHeader(RfqHeaderVO rfqHeader) {
		this.rfqHeader = rfqHeader;
	}
	public List<RfqItemVO> getRfqItems() {
		return rfqItems;
	}
	public void setRfqItems(List<RfqItemVO> rfqItems) {
		this.rfqItems = rfqItems;
	}



	
}
