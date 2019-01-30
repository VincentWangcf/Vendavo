package com.avnet.emasia.webquote.rowdata.converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import com.avnet.emasia.webquote.rowdata.Bean;
import com.avnet.emasia.webquote.rowdata.Converter;

public abstract class DefaultConverter<A extends Annotation> implements Converter<A>{

	protected A a;
	
	@Override
	public void initialize(A a) {
		this.a = a;
	
	}

}
