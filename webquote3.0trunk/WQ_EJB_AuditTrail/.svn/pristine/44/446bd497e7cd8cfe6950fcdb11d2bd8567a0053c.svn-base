package com.avnet.emasia.webquote.audit.ejb;


import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.avnet.emasia.webquote.entity.AuditTrail;
import com.avnet.emasia.webquote.entity.AuditTrailRecord;
import com.avnet.emasia.webquote.entity.Auditable;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.exception.WebQuoteException;
import com.avnet.emasia.webquote.exception.WebQuoteRuntimeException;

//import org.springframework.security.core.context.SecurityContextHolder;

import com.avnet.emasia.webquote.util.ConfigLoader;
import com.avnet.emasia.webquote.util.Constants;
import com.avnet.emasia.webquote.utilities.DateUtils;

/**
 * @author Tonmy,Li(906893)
 * @Created on 2012-12-25
 * 
 */

public class AuditTrailInterceptor {
	static final Logger LOGGER = Logger.getLogger("AuditTrailInterceptor");
	
	@PersistenceContext(unitName = "Server_Source")
	EntityManager em;
	@Resource
    SessionContext sc;

	private User user;
    public AuditTrailInterceptor()
    {
    	if(user==null)
    	{
    		//TO-DO
    		user = new User();
    		user.setEmployeeId("999999");
    		user.setName("Tonmy Li");
    	}
    	
    }

    @AroundInvoke
    public Object log(InvocationContext ctx) throws WebQuoteException
    {
		String methodName = ctx.getMethod().getName();
		LOGGER.fine("*** AuditTrailInterceptor intercepting | mothodName: " + methodName);
		long start = System.currentTimeMillis();
		Object[] params = ctx.getParameters();
		String fullClassName = null;
		try {
			fullClassName = ConfigLoader.getPropertiesConfig(Constants.FILE_AUDIT_TRAIL_MAPPING,
					methodName + Constants.SIGN_DELIMITER + Constants.AUDIT_CLASS);
		} catch (IOException e1) {
			throw new WebQuoteException(e1);
		}
		String className = getClassName(fullClassName);

		Auditable eventObj = null;
		Auditable currObj = null;
		if (params != null && params.length > 0 && params[0] instanceof Auditable) {
			eventObj = (Auditable) params[0];
			if (methodName.startsWith(Constants.AUDIT_OPT_EVENT_TYPE_UPDATE)) {
				Query query = em.createNamedQuery(className + Constants.SIGN_DELIMITER + "findById");
				query.setParameter("id", eventObj.getId());
				// query = query.setHint("eclipselink.refresh", "true");
				currObj = (Auditable) query.getSingleResult();
				em.detach(currObj);
			}
		}

		try {
			return ctx.proceed();
		} catch (Exception e) {
			throw new WebQuoteException(e);
		} finally {
			long time = System.currentTimeMillis() - start;

			LOGGER.fine("*** AuditTrailInterceptor invocation of <" + methodName + "> took " + time + "ms");
			String action = null;
			try {
				action = ConfigLoader.getPropertiesConfig(Constants.FILE_AUDIT_TRAIL_MAPPING,
						methodName + Constants.SIGN_DELIMITER + Constants.AUDIT_ACTION);
			} catch (IOException e1) {
				LOGGER.log(Level.SEVERE,"IOException occurs for "+Constants.FILE_AUDIT_TRAIL_MAPPING + " " + methodName + Constants.SIGN_DELIMITER + Constants.AUDIT_ACTION+" "+ e1.getMessage(),e1
						);
			}
			LOGGER.fine("*** AuditTrailInterceptor intercepting | className: " + className + " | action : " + action);

			try {
				if (eventObj != null & action != null) {
					if (methodName.startsWith(Constants.AUDIT_OPT_EVENT_TYPE_CREATE)) {
						logChanges(eventObj, currObj, null, null, Constants.AUDIT_OPT_EVENT_TYPE_CREATE, className,
								action);
					} else if (methodName.startsWith(Constants.AUDIT_OPT_EVENT_TYPE_UPDATE)) {
						Serializable persistedObjectId = getObjectId(eventObj);
						logChanges(eventObj, currObj, null, persistedObjectId.toString(),
								Constants.AUDIT_OPT_EVENT_TYPE_UPDATE, className, action);
					} else if (methodName.startsWith(Constants.AUDIT_OPT_EVENT_TYPE_DELETE)) {
						Serializable persistedObjectId = getObjectId(eventObj);
						logChanges(eventObj, currObj, null, persistedObjectId.toString(),
								Constants.AUDIT_OPT_EVENT_TYPE_DELETE, className, action);
					}

				}
			} catch (Exception e) {
				throw new WebQuoteException(e);
			}

		}}

    
    public void persist(Object object)
    {
        try
        {
            em.persist(object);
        }
        catch (Exception e)
        {
           throw new WebQuoteRuntimeException(e);
        }
    }
    
    
    /**
     * Logs changes to persistent data
   * @param newObject the object being saved, updated or deleted
   * @param existingObject the existing object in the database.  Used only for updates
   * @param parentObject the parent object. Set only if passing a Component object as the newObject
   * @param persistedObjectId the id of the persisted object.  Used only for update and delete
   * @param event the type of event being logged.  Valid values are "update", "delete", "save"
   * @param className the name of the class being logged.  Used as a reference in the auditLogRecord
   * @throws IllegalArgumentException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  private void logChanges(Object newObject, Object existingObject, Object parentObject,
                          String persistedObjectId, String event, String className, String action)
       throws IllegalArgumentException, IllegalAccessException, InvocationTargetException  {     
	    
	  
	    LOGGER.fine("*** AuditTrailInterceptor logChanges | className: "+className+ " | action : "+  action + " | event : "+event );
	    Class<?> objectClass = newObject.getClass();

        //get an array of all fields in the class including those in superclasses if this is a subclass.
        Field[] fields = getAllFields(objectClass, null);
        AuditTrail at = createAuditTrailObject(className,event, action);
        
        // Iterate through all the fields in the object
        
        fieldIteration: for (int ii = 0; ii < fields.length; ii++) 
        {
            
            //make private fields accessible so we can access their values
            fields[ii].setAccessible(true);
            
            //if the current field is static, transient or final then don't log it as 
            //these modifiers are v.unlikely to be part of the data model.
            if(Modifier.isTransient(fields[ii].getModifiers())
               || Modifier.isFinal(fields[ii].getModifiers())
               || Modifier.isStatic(fields[ii].getModifiers())) {
                continue fieldIteration;
            }
            
            String fieldName = fields[ii].getName();
            if(! fieldName.equals("id")) 
            {
               
                Class<?> interfaces[] = fields[ii].getType().getInterfaces();
                for (int i = 0; i < interfaces.length;) 
                {
                    if (interfaces[i].getName().equals("java.util.Collection")) 
                    {
                        continue fieldIteration;
                        
                    //If the field is a class that is a component (Hibernate mapping type) then iterate through its fields and log them
                    } 
                    else if(interfaces[i].getName().equals("com.avnet.emasia.webquote.entity.Auditable"))
                    {

                        Object newComponent = fields[ii].get(newObject);
                        Object existingComponent = null;
                        
                        if(event.equalsIgnoreCase(Constants.AUDIT_OPT_EVENT_TYPE_UPDATE)) 
                        {
                            existingComponent = fields[ii].get(existingObject);
                            if(existingComponent == null && newComponent != null)
                            {
                                try {
                                    existingComponent = newComponent.getClass().newInstance();
                                } catch (InstantiationException | IllegalAccessException e) {
                                	  LOGGER.log(Level.SEVERE,"InstantiationException  occurs on getting Instance of "+existingComponent+" :: " +e.getMessage(), e);
                                } 
                           } else {
                               //if neither objects contain the component then don't log anything
                                continue fieldIteration;
                           }
                        }
                        
                        if(newComponent == null) {
                            continue fieldIteration;
                        }
                        
                        logChanges(newComponent, existingComponent, newObject, persistedObjectId, event, className , action);
                        continue fieldIteration;
                       
                    }
                    i++;
                }
   
                String propertyNewState;
                String propertyPreUpdateState;
   
                //get new field values
                try {
                    Object objPropNewState = fields[ii].get(newObject);
                    if (objPropNewState != null) {
                        propertyNewState = objPropNewState.toString();
                    } else {
                        propertyNewState = Constants.SIGN_BLANK;
                    }
   
                } catch (Exception e) {
                	LOGGER.log(Level.SEVERE,"Exception  occurs on loading object from an index ["+ii+"] :: " +e.getMessage(), e);
                    propertyNewState = Constants.SIGN_BLANK;
                }
                
                if(event.equals(Constants.AUDIT_OPT_EVENT_TYPE_UPDATE)) 
                {
                  
                	try {
	                        Object objPreUpdateState = fields[ii].get(existingObject);
	                        if (objPreUpdateState != null) {
	                            propertyPreUpdateState = objPreUpdateState.toString();
	                        } else {
	                            propertyPreUpdateState = Constants.SIGN_BLANK;
	                        }
	                    } catch (Exception e) {
	                        propertyPreUpdateState = Constants.SIGN_BLANK;
	                        LOGGER.log(Level.SEVERE,"Exception  occurs on event ["+Constants.AUDIT_OPT_EVENT_TYPE_UPDATE+"] :: " +e.getMessage(), e);
	                    }
	                    
	                    // Now we have the two property values - compare them
	                    if (propertyNewState.equals(propertyPreUpdateState)) 
	                    {
	                        continue; // Values haven't changed so loop to next property
	                    } 
	                    else  
	                    {
	                    	 
	                    	AuditTrailRecord atr = new AuditTrailRecord();
	                    	atr.setAuditTrail(at);
	                        atr.setFromValue(propertyPreUpdateState);
	                        atr.setToValue(propertyNewState);
	                        atr.setTargetColumn(fieldName);
                            at.getAuditTrailRecords().add(atr);
	                        
//	                        if(parentObject == null) {
//	                            logRecord.setEntity((Auditable) newObject);
//	                        } else {
//	                            logRecord.setEntity((Auditable) parentObject);
//	                        }
	   

	   
	                    }
                    
                    
                } 
                else if(event.equals(Constants.AUDIT_OPT_EVENT_TYPE_DELETE)) 
                {
                        
                	    Object returnValue = fields[ii].get(newObject);
                        
                        
                    	AuditTrailRecord atr = new AuditTrailRecord();
                    	atr.setAuditTrail(at);
                        atr.setToValue(Constants.AUDIT_REMARK_DELETE);
                        atr.setTargetColumn(fieldName);
                        
                        
                        if (returnValue != null)
                        	atr.setFromValue(returnValue.toString());
                        if (persistedObjectId != null)
                        	at.setTargetId(persistedObjectId);
   
                        at.getAuditTrailRecords().add(atr);
                        
//                        if(parentObject == null) {
//                        	at.setEntity((Auditable) newObject);
//                        } else {
//                        	at.setEntity((Auditable) parentObject);
//                        }
   

                        
                } 
                else if(event.equals(Constants.AUDIT_OPT_EVENT_TYPE_CREATE)) 
                {
                           
                	    Object returnValue = fields[ii].get(newObject);
   
                	    AuditTrailRecord atr = new AuditTrailRecord();
                	    atr.setAuditTrail(at);
                	    atr.setTargetColumn(fieldName);
                        atr.setFromValue(Constants.AUDIT_REMARK_DON_EXIST);
                        if (returnValue != null) 
                        {
                        	atr.setToValue(returnValue.toString());
                        } 
                        else
                        	atr.setToValue(Constants.SIGN_BLANK);
   
                        at.getAuditTrailRecords().add(atr);
                        
//                        if(parentObject == null) {
//                            logRecord.setEntity((Auditable) newObject);
//                        } else {
//                            logRecord.setEntity((Auditable) parentObject);
//                        }
   

   
                }
   
                
            }
        }
        
        persist(at);
  }

    /**
     * Returns an array of all fields used by this object from it's class and all superclasses.
     * @param objectClass the class 
     * @param fields the current field list
     * @return an array of fields
     */
    private Field[] getAllFields(Class<?> objectClass, Field[] fields) {
        
        Field[] newFields = objectClass.getDeclaredFields();
        
        int fieldsSize = 0;
        int newFieldsSize = 0;
        
        if(fields != null) {
            fieldsSize = fields.length;
        }
        
        if(newFields != null) {
            newFieldsSize = newFields.length;
        }
 
        Field[] totalFields = new Field[fieldsSize + newFieldsSize];
        
       if(fieldsSize > 0) {
           System.arraycopy(fields, 0, totalFields, 0, fieldsSize);
       }
       
       if(newFieldsSize > 0) { 
           System.arraycopy(newFields, 0, totalFields, fieldsSize, newFieldsSize);
       }
       
       Class<?> superClass = objectClass.getSuperclass();
       
       Field[] finalFieldsArray;
       
       if (superClass != null && ! superClass.getName().equals("java.lang.Object")) {
           finalFieldsArray = getAllFields(superClass, totalFields);
       } else {
           finalFieldsArray = totalFields;
       }
       
       return finalFieldsArray;
 
    }
    /**
     * Gets the id of the persisted object
     * @param obj the object to get the id from
     * @return object Id
     */
    private Serializable getObjectId(Object obj) {
        
        Class<?> objectClass = obj.getClass();
        Method[] methods = objectClass.getMethods();
 
        Serializable persistedObjectId = null;
        for (int ii = 0; ii < methods.length; ii++) {
            // If the method name equals 'getId' then invoke it to get the id of the object.
            if (methods[ii].getName().equals("getId")) 
            {
               try {
                    persistedObjectId = (Serializable)methods[ii].invoke(obj, null);
                    break;      
                } catch (Exception e) {
                	LOGGER.log(Level.SEVERE,"Audit Log Failed - Could not get persisted object id: " + e.getMessage());
                }
            }
        }
        return persistedObjectId;
    }
    
    private AuditTrail createAuditTrailObject(String className , String event , String action)
    {
    	AuditTrail at = new AuditTrail();
    	at.setTargetClass(className);
        at.setEmployeeName(user.getName());
        at.setCreatedBy(user.getEmployeeId());
        at.setCreatedOn(DateUtils.getCurrentAsiaDateObj());
        at.setDescription(event);
        at.setAction(action);

        return at;
    }
    private String getClassName(Object obj)
    {
    	try 
    	{
	    	Class<?> objectClass = obj.getClass();
	        String className = objectClass.getName();
	        LOGGER.fine("*** AuditTrailInterceptor getClassName | className: "+className);
	        String[] tokens = className.split("\\.");
	        int lastToken = tokens.length - 1;
	        className = tokens[lastToken];
	        return className;

	    } catch (IllegalArgumentException e) {
	        LOGGER.log(Level.SEVERE,"AuditTrailInterceptor getClassName | className: "+obj+" :::  "+e.getMessage(), e);
        }
    	return null;

    }
    
    
    private String getClassName(String fullClassName)
    {
    	try 
    	{
	        String className = fullClassName;
	        LOGGER.fine("*** AuditTrailInterceptor getClassName | className: "+className);
	        String[] tokens = className.split("\\.");
	        int lastToken = tokens.length - 1;
	        className = tokens[lastToken];
	        return className;

	    } catch (IllegalArgumentException e) {
	    	  LOGGER.log(Level.SEVERE,"AuditTrailInterceptor getClassName | className: "+fullClassName+" :::  "+e.getMessage(), e);
        }
    	return null;

    }
    


}
