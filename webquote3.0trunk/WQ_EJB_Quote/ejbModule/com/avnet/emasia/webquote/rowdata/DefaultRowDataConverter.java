/**
 * 
 */
package com.avnet.emasia.webquote.rowdata;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.avnet.emasia.webquote.rowdata.datasource.ResolveInfo;



/**
 * @author 042659
 *
 */
public class DefaultRowDataConverter<T extends Bean> implements RowDataConverter<T> {
	
	static final Logger logger = Logger.getLogger(DefaultRowDataConverter.class.getName());

//	private RowDataSource columnDataSource;
//	
//	private Validator validator;
//	
//	
//
//
//
//	/**
//	 * 
//	 */
//	public DefaultRowDataConverter() {
//		// TODO Auto-generated constructor stub
//		initValidator();
//	}
//
//
//
//	/**   
//	* @Description: init Validator
//	* @author 042659       
//	* @return void    
//	* @throws  
//	*/  
//	private void initValidator() {
//		// TODO Auto-generated method stub
//		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
//		 validator = factory.getValidator();
//		
//	}
//
//
//
//	public RowDataSource getColumnDataSource() {
//		return columnDataSource;
//	}
//
//
//
//	public void setColumnDataSource(RowDataSource columnDataSource) {
//		this.columnDataSource = columnDataSource;
//	}
//
//
//
//	/* (non-Javadoc)
//	 * @see com.avnet.emasia.webquote.columndata.ColumnDataConverter#convertAndValidate(java.io.InputStream, java.lang.String, com.avnet.emasia.webquote.columndata.BeanDefinition)
//	 */
//	@Override
//	public List<T> convertAndValidate(InputStream is, FileType type, BeanDefinition<T> beanDef) {
//		// TODO Auto-generated method stub
//		List<T> beanObjList = new ArrayList<T>();
//		this.columnDataSource = RowDataSource.create(is, type, null);
//		Class<T> beanCls = beanDef.getBeanClass();
//		while (this.columnDataSource.hasNext()) {
//			this.columnDataSource.next();
//			T tObj = GenerateInstance(beanCls);
//			for (FieldDefinition fieldDefinition : beanDef.getFieldDefinitions()) {
//				assignValue(tObj, fieldDefinition, columnDataSource);
//			}
//			validateBean(tObj);
//			beanObjList.add(tObj);
//		}
//		return beanObjList;
//	}
///**   
//	* @Description: validate bean and put error msg to validationErrors
//	* @author 042659
//	* @param tObj     extends DefaultBean  
//	* @return void    
//	* @throws  
//	*/  
//	private void validateBean(T tObj) {
//		StringBuilder sb = new StringBuilder();
//		if (validator == null) {
//			initValidator();
//		}
//		for (ConstraintViolation<T> constraintVioloation : validator.validate(tObj)) {
//			sb.append(constraintVioloation.getMessage());
//		}
//		tObj.addValidationError(sb.toString());
//
//	}
//
//	
//	/**   
//	* @Description: assign the values to bean reference  
//	* @author 042659
//	* @param beanObj
//	* @param fieldDefinition       
//	 * @param cDataSource 
//	* @return void    
//	*/  
//	private void assignValue(Bean beanObj, FieldDefinition fieldDefinition, RowDataSource cDataSource) {
//		// TODO Auto-generated method stub
//	//	printForTest(fieldDefinition, cDataSource);
///*		Object value = fieldDefinition.getConverter().convert(cDataSource.getColumnValue(fieldDefinition.getColumnName()));
//		setter(beanObj, fieldDefinition.getFieldName(), value, GenericUtil.getGenericType(fieldDefinition.getField()));
//*/
//		fieldDefinition.getConverter().convert(cDataSource.getColumnValue(fieldDefinition.getColumnName()), beanObj, fieldDefinition.getField());
//	}
//
//
//	public void setter(Object obj, String att, Object value, Class<?> type) {
//		try {
//			Method method = obj.getClass().getMethod("set" + turnFirstCharToUpperCase(att), type);
//			method.invoke(obj, value);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//
//	/**   
//	* @Description: TODO
//	* @author 042659
//	* @param fieldName  such as mfr
//	* @return String    Mfr
//	*/  
//	private String turnFirstCharToUpperCase(String fieldName) {
//		// TODO Auto-generated method stub
//		return fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
//	}
//
//
//
//	/**   
//	* @Description: TODO
//	* @author 042659
//	* @param beanCls Class
//	* @return Object    
//	* @throws  
//	*/  
//	private <T> T GenerateInstance(Class<T> beanCls) {
//		T t = null;
//		try {
//			t = beanCls.newInstance();
//		} catch (InstantiationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return t;
//	}
//
//	public List<T> convertAndValidate2(RowDataSource source, FileType type, BeanDefinition<T> beanDef) {
//		// TODO Auto-generated method stub
//		List<T> beanObjList = new ArrayList<T>();
//		this.columnDataSource = source;
//		beanDef.getBeanClass().getClass();
//		beanDef.getFieldDefinitions();
//		Class<T> beanCls = beanDef.getBeanClass();
//		while (this.columnDataSource.hasNext()) {
//			this.columnDataSource.next();
//			 T tObj = GenerateInstance(beanCls);
//			for (FieldDefinition fieldDefinition : beanDef.getFieldDefinitions()) {
//				assignValue(tObj, fieldDefinition, columnDataSource);
//			}
//			beanObjList.add(tObj);
//		}
//		return beanObjList;
//	}

	
	@Override
	public List<T> convertAndValidate(InputStream is, FileType type, ResolveInfo resolveInfo, Class<T> clazz) {
		
		logger.log(Level.INFO, "Start to convert file " + resolveInfo.getResolvedFileName() + " to " + clazz.getName() );
		
		BeanDefinition<T> beanDefinition = BeanDefinition.create(clazz);
		logger.log(Level.INFO, "BeanDefinition created");
		
		RowDataSource source = RowDataSource.create(is, type, resolveInfo);
		logger.log(Level.INFO, "RowDataSource created");
		
		List<T> beans = new ArrayList<>();
		while(source.hasNext()) {
			source.next();
			T bean =  null;
			try {
				bean = clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				logger.log(Level.SEVERE, "This should not happen when initiazing class " + clazz.getName(), e);
			}
			beans.add(bean);
			bean.setSeq(source.getSeq());
			for (FieldDefinition fd : beanDefinition.getFieldDefinitions()) {
				fd.getConverter().convert(source.getColumnValue(fd.getColumnName()), bean, fd.getField());
			}
		}
		
		logger.log(Level.INFO, "Convertion finished, start to validate.");
		
		ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
		Validator validator = validatorFactory.getValidator();
		
		for(T bean : beans) {
			Set<ConstraintViolation<T>> violations = validator.validate(bean);
			
			StringBuilder sb = new StringBuilder();
			for(ConstraintViolation<T> violation : violations) {
				if (sb.length() == 0) {
					sb.append("Row: " + bean.getSeq());
				}
				sb.append(violation.getMessage());
			}
			bean.setValidateViolationMessage(sb.length() == 0 ? null : sb.toString());
		}
		
		logger.log(Level.INFO, "Validation finished");
		return beans;
	}
}
