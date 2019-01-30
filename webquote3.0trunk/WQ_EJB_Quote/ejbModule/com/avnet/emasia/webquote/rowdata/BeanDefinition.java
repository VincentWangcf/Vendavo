package com.avnet.emasia.webquote.rowdata;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BeanDefinition <T extends Bean>{
	
	private Class<T> beanClass;
	
	private List<FieldDefinition> fieldDefinitions = new ArrayList<>();

	public Class<T> getBeanClass() {
		return beanClass;
	}
	
	public void setBeanClass(Class<T> beanClass) {
		this.beanClass = beanClass;
	}
	
	public List<FieldDefinition> getFieldDefinitions() {
		return fieldDefinitions;
	}

	public void setFieldDefinitions(List<FieldDefinition> fieldDefinitions) {
		this.fieldDefinitions = fieldDefinitions;
	}

	public boolean addFieldDefinition(FieldDefinition fieldDefinition) {
		return fieldDefinitions.add(fieldDefinition);
	}



	public Optional<FieldDefinition> getFieldDefinition (String fieldName) {
		return 	fieldDefinitions.
				stream().
				filter(t -> t.getColumnName().equals(fieldName)).
				findFirst();
	}
	
	public Optional<FieldDefinition> getFieldDefinition (int fieldPosition) {
		return 	fieldDefinitions.
				stream().
				filter(t -> t.getColumnPosition()== fieldPosition).
				findFirst();
	}
	
	public static <U extends Bean> BeanDefinition<U> create(Class<U> clazz) {
		
		BeanDefinition<U> beanDefinition = new BeanDefinition<>();
		
		beanDefinition.beanClass = clazz;
		
		for (Field field : GenericUtil.getNoneTransientFileds(clazz)) {
			field.setAccessible(true);
			
			FieldDefinition fieldDefinition = FieldDefinition.create(field);
			if (fieldDefinition != null) {
				beanDefinition.fieldDefinitions.add(fieldDefinition);
			}
		}
		
		return beanDefinition;
	}
	

}

