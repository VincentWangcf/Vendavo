package com.avnet.emasia.webquote.util;

import java.math.BigDecimal;
import java.util.Comparator;

import com.avnet.emasia.webquote.quote.vo.CostExtractSearchCriterial;

public class CostAndDateComparator implements Comparator<CostExtractSearchCriterial>{
	boolean costAscending;
	boolean dateAscending;

	public CostAndDateComparator(boolean costAscending, boolean dateAscending) {
		this.costAscending = costAscending;
		this.dateAscending = dateAscending;
	}

	public int compare(CostExtractSearchCriterial obj1, CostExtractSearchCriterial obj2) {
		CostExtractSearchCriterial myObj1 = obj1;
		CostExtractSearchCriterial myObj2 = obj2;
		int stringResult = 0;
		try{

			stringResult = BigDecimal.valueOf(myObj1.getCost()).compareTo(BigDecimal.valueOf(myObj2.getCost()));

			if (stringResult == 0) {
				// Strings are equal, sort by date
				if(this.dateAscending) {
					return ( myObj1.getQuoteEffectiveDate().getTime() > myObj2.getQuoteEffectiveDate().getTime() ? 1:-1);
				}else {
					if(myObj1.getQuoteEffectiveDate().getTime() == myObj2.getQuoteEffectiveDate().getTime()){
						return 0;
					}else{
						return ( myObj1.getQuoteEffectiveDate().getTime() > myObj2.getQuoteEffectiveDate().getTime() ? -1:1);
					}
				}
			}
			else {
				return stringResult;
			}

		}catch(Exception e){
			e.printStackTrace();
		}

		return stringResult;
	}

}
