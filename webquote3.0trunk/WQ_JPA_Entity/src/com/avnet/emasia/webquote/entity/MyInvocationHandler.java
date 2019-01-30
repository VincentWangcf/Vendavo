package com.avnet.emasia.webquote.entity;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyInvocationHandler implements InvocationHandler {
	private Object delegate;
	private static final Logger LOGGER = Logger.getLogger(MyInvocationHandler.class.getSimpleName());

	public Object bind(Object delegate) {
		this.delegate = delegate;
		return Proxy.newProxyInstance(this.delegate.getClass().getClassLoader(),
				this.delegate.getClass().getInterfaces(), this);
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object result = null;
		try {
			result = method.invoke(this.delegate, args);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error in invoking method "+method.getName(), e);
		}
		return result;

	}
}
