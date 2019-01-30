package com.avnet.webquote.quote.ejb.utility;

import java.util.Comparator;

import com.avnet.emasia.webquote.entity.ReferenceMarginSetting;

public class EJBQuoteComparator<Object> implements Comparator<Object>{

	@Override
	public int compare(Object object1, Object object2) {
		  long x1 = ((ReferenceMarginSetting) object1).getMaterialID();
          long x2 = ((ReferenceMarginSetting) object2).getMaterialID();
          
          int xComp = 0;
			if (x1 > x2) {
				xComp = 1;
			} else if (x1 < x2) {
				xComp = -1;
			}

          if (xComp != 0) {
             return xComp;
          } else {
             long s1 = ((ReferenceMarginSetting) object1).getProductGroupID();
             long s2 = ((ReferenceMarginSetting) object2).getProductGroupID();
             
             int sComp = 0;
				if (s1 > s2) {
					sComp = 1;
				} else if (s1 < s2) {
					sComp = -1;
				}
	            return sComp;
          }
	}

}
