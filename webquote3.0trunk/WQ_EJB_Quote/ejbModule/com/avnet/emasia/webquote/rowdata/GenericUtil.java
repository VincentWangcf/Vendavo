package com.avnet.emasia.webquote.rowdata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.persistence.Column;

import org.eclipse.persistence.jpa.jpql.Assert;

import com.avnet.emasia.webquote.rowdata.datasource.Computable;
import com.avnet.emasia.webquote.rowdata.datasource.Memoizer;


//import org.eclipse.persistence.jpa.jpql.Assert;

public class GenericUtil {

	public static Class<?> getGenericType(java.lang.reflect.Field field) {
		ParameterizedType p;
		try {
			p = (ParameterizedType) field.getGenericType();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		return (Class<?>) p.getActualTypeArguments()[0];

	}
	private static final Computable<Class<?>, Map<String, java.lang.reflect.Field>> c =new 
			Computable<Class<?>, Map<String, java.lang.reflect.Field>>() {
				@Override
				public Map<String, java.lang.reflect.Field> compute(Class<?> arg) throws Exception {
					return collectNoneTransientFileds(arg, new HashMap<String, java.lang.reflect.Field>());
				}
	};
	private static final Computable<Class<?>, Map<String, java.lang.reflect.Field>> fieldCache = 
			new Memoizer<Class<?>, Map<String, java.lang.reflect.Field>>(c);
	
	@SuppressWarnings("unchecked")
	public static List<java.lang.reflect.Field> getNoneTransientFileds(Class<?> targetClass) {
		//Assert.isNull(targetClass);
		if(targetClass==null) return Collections.EMPTY_LIST;
		try {
			return fieldCache.compute(targetClass).values().stream().collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Collections.EMPTY_LIST;
		
	}
	
	
	@SuppressWarnings("unused")
	private static Map<String, java.lang.reflect.Field> collectNoneTransientFileds(Class<?> targetClass, 
			final Map<String, java.lang.reflect.Field> fieldMap) {
		Assert.isNotNull(fieldMap, "fieldMap is null");
		//Assert.isNotNull(targetClass, "targetClass is null");
		if(targetClass==null) return fieldMap;
		if(targetClass.getSuperclass() != null && targetClass.getSuperclass() != Object.class) {
			collectNoneTransientFileds(targetClass.getSuperclass(), fieldMap);
		}
		java.lang.reflect.Field[] fields = targetClass.getDeclaredFields();
		if(fields !=null) {
			Arrays.stream(fields).forEach(field -> {
				if(field.isAnnotationPresent(Transient.class)) {
					fieldMap.remove(field.getName());
				} else {
					fieldMap.put(field.getName(), field);
				}
			});
		}
		return fieldMap;
	}
}
