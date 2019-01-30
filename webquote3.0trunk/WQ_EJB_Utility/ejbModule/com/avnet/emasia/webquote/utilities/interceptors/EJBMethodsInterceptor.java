package com.avnet.emasia.webquote.utilities.interceptors;

import java.util.logging.Logger;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import com.avnet.emasia.webquote.exception.WebQuoteException;

public class EJBMethodsInterceptor 
{
	static final Logger LOGGER = Logger.getLogger("EJBMethodsInterceptor");

    public EJBMethodsInterceptor()
    {
    }

    @AroundInvoke
    public Object log(InvocationContext ctx) throws WebQuoteException 
    {
    	Object[] params = ctx.getParameters();
    	String paramStr = "";
    	if (params != null && params.length > 0)
    	{
    		paramStr = params[0].toString();
    		if (params.length > 1)
    		{
    			paramStr += ", ...";
    		}
    	}
        String methodName = ctx.getMethod().getDeclaringClass().getSimpleName() + "." + ctx.getMethod().getName() + 
        		"(" + paramStr + ")";
        LOGGER.info("### " + methodName + " called");
        long startTime = System.currentTimeMillis();
        try
        {
            return ctx.proceed();
        }
        catch (Exception e)
        {
            throw new WebQuoteException(e);
        }
        finally
        {
        	LOGGER.info("### " + methodName + " took " + (System.currentTimeMillis() - startTime) + " msec");
        }
    }
}