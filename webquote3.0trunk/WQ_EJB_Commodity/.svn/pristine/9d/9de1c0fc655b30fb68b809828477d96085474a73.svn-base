package com.avnet.emasia.webquote.commodity.util;

/**
 * @author 906893
 *
 * business calculation logic utilities which get from Webquote 1.
 */
public class CaculationLogicUtils {

	//condition: if MOQ is null,run this method to generate it.
	//input: MOV MPQ Cost
	//ouput: if no MOQ, it will return Constantss.
	public static int generateMOQ(int mov, int mpq, double cost)
	{
		int returnMOQ  = Constants.DEFAULT_INTEGER_VALUE;
		double tempD = 0d;
		if(mov!=Constants.DEFAULT_INTEGER_VALUE && mov>0 && mpq!=Constants.DEFAULT_INTEGER_VALUE && mpq>0 && cost!=Constants.DEFAULT_FLOAT_VALUE)
		{
			tempD = (new Double(mov)).doubleValue() / cost;
			returnMOQ = (int)tempD;
			if(returnMOQ<mpq)
			{
				 returnMOQ = mpq;
			}
			else
			{
				int tempMPQ = mpq;
				while(returnMOQ>tempMPQ)
		    	{
					tempMPQ=tempMPQ+mpq;
		    	}

    			returnMOQ = tempMPQ;
			}
		}
	    return returnMOQ;
	}
	
	
//	Here is the Avnet Quoted Qty Logic:
//	If Required Qty =< MOQ, then Avnet Quoted Qty = MOQ
//	Else if the reminder of Required Qty / MPQ <> 0 then Avnet Quoted Qty = Round (Required Qty) by MPQ
//	Afterwards, if Avnet Quoted Qty * Cost < MOV, then Avnet Quoted Qty + MPQ until Avnet Quoted Qty * Cost >= MOV.

	
	public static int generateAvnetQuotedQty(int moq ,int mpq ,int mov ,int requiredQty ,double cost)
	{
		int returnAvnetQuotedQty = Constants.DEFAULT_INTEGER_VALUE;
		
		if(moq!=Constants.DEFAULT_INTEGER_VALUE)
		{
		    double totalcost = 0;
		    if(requiredQty<=moq)
		    {
		    	returnAvnetQuotedQty=moq;
		    }
		    else
		    {
		    	if(mpq!=Constants.DEFAULT_INTEGER_VALUE)
		    	{
			    	if(requiredQty/mpq!=0)
			    	{
			    		int tempMPQ = mpq;
			    		while(requiredQty>tempMPQ)
				    	{
			    			tempMPQ = tempMPQ +mpq;
				    	}
			    		returnAvnetQuotedQty=tempMPQ;
			    	}
		    	}
		    }
		    if(mov!=Constants.DEFAULT_INTEGER_VALUE && cost!=Constants.DEFAULT_FLOAT_VALUE)
		    {
			    totalcost = returnAvnetQuotedQty * cost;
			    while(totalcost<mov)
		    	{
			    	returnAvnetQuotedQty = returnAvnetQuotedQty + mpq;
		    		totalcost = returnAvnetQuotedQty * cost;
		    	}
		    }
		}
	    return returnAvnetQuotedQty;
	}
	
	
	
	
	public static String generatePMOQ(int moq ,int mpq ,int mov ,int quotedQty,double cost,String qtyIndicator)
	{
		String returnPMOQ = null;
		
		// reqeusted by victor to hardcode qtyIndicator to "MOQ".
		qtyIndicator ="MOQ";
		
		if(quotedQty!=Constants.DEFAULT_INTEGER_VALUE && qtyIndicator!=null)
		{

	    	if(qtyIndicator.equalsIgnoreCase("MOQ"))
	    	{
	    		quotedQty = moq;
	    	}
	    	else if(qtyIndicator.equalsIgnoreCase("MPQ"))
	    	{
	    		quotedQty = mpq;
	    	}
	    	else if(qtyIndicator.equalsIgnoreCase("Exact"))
	    	{
	    		
	    	}
	    	else
	    	{
	    		if(StringUtils.isNumber(qtyIndicator))
				{
	    		   quotedQty = quotedQty*Integer.valueOf(qtyIndicator).intValue();
	    	    }
	    	}
	    	
	    	if(!qtyIndicator.equalsIgnoreCase("Exact"))
	    	{
	    		if(moq!=Constants.DEFAULT_INTEGER_VALUE && mpq!=Constants.DEFAULT_INTEGER_VALUE) 
	    		{
		            quotedQty = (quotedQty/mpq) * mpq;
	    		}      
	    		returnPMOQ = String.valueOf(quotedQty) + "+";
	    		
	    	}
	    	else
	    	{
	    		returnPMOQ = String.valueOf(quotedQty);
	    	}
	    	
		}
	    return returnPMOQ;
	}
	
	public static int roundUp(int mpq, int first, int max)
	{
		int temp= first;
		while(max>temp+mpq)
    	{
			temp = temp +mpq;
    	}
		return temp;
	}
	
	
	
}
