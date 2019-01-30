package com.avnet.emasia.webquote.masterData.ejb;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Timer;

import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.DataSyncError;
import com.avnet.emasia.webquote.entity.LoadingJobTrack;
import com.avnet.emasia.webquote.entity.SalesOrg;
import com.avnet.emasia.webquote.entity.SapDpaCust;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.exception.CommonConstants;
import com.avnet.emasia.webquote.exception.WebQuoteException;
import com.avnet.emasia.webquote.masterData.dto.Config;
import com.avnet.emasia.webquote.masterData.dto.ControlFile;
import com.avnet.emasia.webquote.masterData.dto.DataFile;
import com.avnet.emasia.webquote.masterData.dto.FileContext;
import com.avnet.emasia.webquote.masterData.exception.CheckedException;
import com.avnet.emasia.webquote.masterData.exception.IOInteractException;
import com.avnet.emasia.webquote.masterData.kit.TxtLoadingUnit;
import com.avnet.emasia.webquote.masterData.util.Constants;
import com.avnet.emasia.webquote.masterData.util.FileUtils;
import com.avnet.emasia.webquote.masterData.util.FilenameFilterImp;
import com.avnet.emasia.webquote.masterData.util.FunctionUtils;
import com.avnet.emasia.webquote.masterData.util.ResourceLoader;
import com.avnet.emasia.webquote.masterData.util.StringUtils;
import com.avnet.emasia.webquote.quote.ejb.PosSB;
import com.avnet.emasia.webquote.utilities.DateUtils;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.utilities.bean.MailInfoBean;
import com.avnet.emasia.webquote.utilities.ejb.MailUtilsSB;
import com.avnet.emasia.webquote.utilities.schedule.HATimerService;
import com.avnet.emasia.webquote.utilities.schedule.IScheduleTask;

/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-01-23
 * 
 */
public class StandardJob implements IScheduleTask {

	private static Logger logger = Logger.getLogger(StandardJob.class.getName());
	@EJB
	private ScheduleConfigSB schedConfSB;
	@EJB
	private DataSyncErrorSB errorSB;
	@EJB
	private MailUtilsSB mailUtilsSB;
	@EJB
	private LoadingJobTrackSB ladingJobTrackSB;

	@EJB
	private MasterDataSB masterSB;

	private LoadingJobTrack ljt;

	private String type;

	private FileContext fileCon = null;
	protected List<DataSyncError> errorList;
	private String[] tables = { "POS" };
	@EJB
	private PosSB posSB;

	@Override
	public void executeTask(Timer timer) {
		// TODO Auto-generated method stub

	}

	public boolean isRanOnThisServer(String serviceName) {
		boolean returnB = false;
		String hostName = null;
		try {
			InetAddress addr = InetAddress.getLocalHost();
			hostName = addr.getHostName().toString();
			logger.info("hostName: " + hostName);
			if (serviceName != null && hostName != null && serviceName.contains(hostName)) {
				returnB = true;
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Exception occured for service: " + serviceName, e);
			return false;
		}

		return returnB;

	}

	public void run() throws CheckedException {
		try {
//			ServiceController<?> service = CurrentServiceContainer.getServiceContainer()
//					.getService(HATimerService.SINGLETON_SERVICE_NAME);
//			logger.info("SERVICE " + service);
//			if (service != null) {
//				logger.info("service.getValue(): " + service.getValue());
//				if (isRanOnThisServer((String) service.getValue())) {
					ljt = new LoadingJobTrack();
					ljt.setType(getType());
					ljt.setStartTime(DateUtils.getCurrentAsiaDateObj());
					try {
						if (errorList != null)
							errorList.clear();
						errorList = new ArrayList<DataSyncError>();
						loadingAction();
						ljt.setStatus("S");
					} catch (CheckedException e) {
						ljt.setStatus("F");
						sendErrorEmail(e.getMessage(),Constants.MASTER_LOADING_ERROR_EMAIL_SUBJECT);
						throw new CheckedException(e);
					} finally {
						ljt.setEndTime(DateUtils.getCurrentAsiaDateObj());
						ladingJobTrackSB.createLoadingJobCheck(ljt);
					}
//				}
//			} else {
//				throw new WebQuoteRuntimeException(CommonConstants.COMMON_SERVICE_NOT_FOUND,
//						new Object[] { HATimerService.SINGLETON_SERVICE_NAME });
//			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error occured on run for task: " + this.type, e);
		}

	}

	public void init() {
		fileCon = new FileContext(tables);
		Config[] config = new Config[tables.length];
		for (int i = 0; i < tables.length; i++) {
			config[i] = getConfig(tables[i]);
		}
		fileCon.setConfig(config);
	}

	/*
	 * loading actions
	 */
	public void loadingAction() throws CheckedException {
		init();
		fileCon = findFiles(fileCon);
		if (!validationForFileIsExist(fileCon))
			return;
		fileCon = loadFiles(fileCon);
		if (!validationForIsCountMatched(fileCon))
			return;
		// re-initial the error record list
		if (errorList != null)
			errorList.clear();
		errorList = new ArrayList<DataSyncError>();
		saveToDB(fileCon);
		backupFiles(fileCon);
		boolean switcher = schedConfSB.getSwitch("POS");
		if (switcher) {
			try {
				posSB.summarizePos();
			} catch (Exception ex) {
				logger.log(Level.SEVERE, ex.getMessage(), ex);
			}
		}
	}

	public Config getConfig(String type) {
		return schedConfSB.getConfig(type);
	}

	/**
	 * @param dataList
	 * @param config
	 * @param functionName
	 * @param fileName
	 * @param masterSB
	 * @param errorList
	 * @return
	 * @throws CheckedException
	 */
	public List<Object> transferPosLoadingAndPosSchedule(List<String> dataList, Config config, String functionName,
			String fileName, MasterDataSB masterSB, List<DataSyncError> errorList) throws CheckedException {

		List<Object> returnList = new ArrayList<Object>();
		List<String> fields = FunctionUtils.splitLineData((String) dataList.remove(0), Constants.SEPARATOR, true);
		int size = dataList.size();
		List<String> rowData = null;
		for (int i = 0; i < size; i++) {

			rowData = FunctionUtils.splitLineData((String) dataList.get(i), Constants.SEPARATOR, true);
			Object obj = null;
			Object pkObj = null;
			Object fkObj = null;
			Object fk1Obj = null;
			try {
				obj = ResourceLoader.getInstance().getClassInstance(config.getClassName());
				if (config.getPkClass() != null) {
					pkObj = ResourceLoader.getInstance().getClassInstance(config.getPkClass());
				}
				if (config.getFkClass() != null) {
					fkObj = ResourceLoader.getInstance().getClassInstance(config.getFkClass());
				}
				if (config.getFk1Class() != null) {
					fk1Obj = ResourceLoader.getInstance().getClassInstance(config.getFk1Class());
				}
			}catch(ClassNotFoundException e)
				{
					throw new CheckedException("Method=transfer param=[className="+config.getClassName()+"] ClassNotFoundException ["+e.getMessage()+"]" );
			} catch (IllegalAccessException e) {
					throw new CheckedException("Method=transfer param=[className="+config.getClassName()+"]  IllegalAccessException ["+e.getMessage()+"]" );
			} catch (InstantiationException e) {
					throw new CheckedException("Method=transfer param=[className="+config.getClassName()+"]  InstantiationException ["+e.getMessage()+"]" );
			}
			String[] methods = config.getMethods();
			String[] paramTypes = config.getParamTypes();

			String[] pkClassParamTypes = config.getPkClassParamTypes();

			String[] fkClassMethods = config.getFkClassMethods();
			String[] fkClassParamTypes = config.getFkClassParamTypes();

			String[] fk1ClassMethods = config.getFk1ClassMethods();
			String[] fk1ClassParamTypes = config.getFk1ClassParamTypes();

			try {
				for (int j = 0; j < rowData.size(); j++) {

					if (Constants.TYPE_NA.equals(paramTypes[j]))
						continue;

					String rowDataStr = rowData.get(j);
					if ("setCustomerNumber".endsWith(methods[j]) && rowDataStr != null
							&& StringUtils.isNumeric(rowDataStr)) {
						Integer tempCustNum = Integer.parseInt(rowDataStr);
						rowDataStr = String.valueOf(tempCustNum);
					}

					if (Constants.TYPE_PK.equals(paramTypes[j])) {
						pkObj = setValue(pkObj, methods[j], getMapingClass(pkClassParamTypes[j]),
								getParamValue(pkClassParamTypes[j], rowDataStr));
					} else if (Constants.TYPE_FK.equals(paramTypes[j])) {
						fkObj = setValue(fkObj, methods[j], getMapingClass(fkClassParamTypes[0]),
								getParamValue(fkClassParamTypes[0], rowDataStr));
					} else if (Constants.TYPE_FK1.equals(paramTypes[j])) {
						fk1Obj = setValue(fk1Obj, methods[j], getMapingClass(fk1ClassParamTypes[0]),
								getParamValue(fk1ClassParamTypes[0], rowDataStr));
					} else {
						obj = setValue(obj, methods[j], getMapingClass(paramTypes[j]),
								getParamValue(paramTypes[j], rowDataStr));
					}

				}

				// update common fields

				obj = appendCommonDateField(obj);
				obj = appendOriginalData(obj, (String) dataList.get(i));

				if (pkObj != null) {
					obj = setValue(obj, "setId", pkObj.getClass(), pkObj);
				}

				if (fkObj != null) {
					obj = setValue(obj, fkClassMethods[0], fkObj.getClass(), fkObj);
				}

				if (fk1Obj != null) {
					obj = setValue(obj, fk1ClassMethods[0], fk1Obj.getClass(), fk1Obj);
				}

				returnList.add(obj);
			} catch (Exception es) {
				logger.log(Level.SEVERE,
						"Exception occured in method transferPosLoadingAndPosSchedule() while transfer for file name: "
								+ fileName + ", Reason for failure: "
								+ MessageFormatorUtil.getParameterizedStringFromException(es),
						es);
				errorList = masterSB.addError(errorList,
						"|initial data error|" + es.getMessage() + "|" + (String) dataList.get(i), fileName,
						functionName, 0);
				// throw new CheckedException("Method=setValue Exception
				// ["+es.getMessage()+"]" );
			}
		}
		return returnList;

	}

	/**
	 * @param dataList
	 * @param config
	 * @param functionName
	 * @param fileName
	 * @return
	 * @throws CheckedException
	 */
	public List<Object> transferMasterAndMaterial(List<String> dataList, Config config, String functionName,
			String fileName) throws CheckedException {
		List<Object> returnList = new ArrayList<Object>();
		List<String> fields = FunctionUtils.splitLineData((String) dataList.remove(0), Constants.SEPARATOR, true);
		int size = dataList.size();
		List<String> rowData = null;

		for (int i = 0; i < size; i++) {

			rowData = FunctionUtils.splitLineDataWithoutTrim((String) dataList.get(i), Constants.SEPARATOR, true);
			Object obj = null;
			Object pkObj = null;
			Object fkObj = null;
			Object fk1Obj = null;
			Object fk2Obj = null;

			try {
				obj = ResourceLoader.getInstance().getClassInstance(config.getClassName());
				if (config.getPkClass() != null) {
					pkObj = ResourceLoader.getInstance().getClassInstance(config.getPkClass());
				}
				if (config.getFkClass() != null) {
					fkObj = ResourceLoader.getInstance().getClassInstance(config.getFkClass());
				}
				if (config.getFk1Class() != null) {
					fk1Obj = ResourceLoader.getInstance().getClassInstance(config.getFk1Class());
				}
				if (config.getFk2Class() != null) {
					fk2Obj = ResourceLoader.getInstance().getClassInstance(config.getFk2Class());
				}
			} catch (ClassNotFoundException e) {
				throw new CheckedException("Method=transfer param=[className="+config.getClassName()+"] ClassNotFoundException ["+e.getMessage()+"]" );
			} catch (IllegalAccessException e1) {
				throw new CheckedException("Method=transfer param=[className="+config.getClassName()+"]  IllegalAccessException ["+e1.getMessage()+"]" );
			} catch (InstantiationException e2) {
				throw new CheckedException("Method=transfer param=[className="+config.getClassName()+"]  InstantiationException ["+e2.getMessage()+"]" );
			}
			String[] methods = config.getMethods();
			String[] paramTypes = config.getParamTypes();

			String[] pkClassMethods = config.getPkClassMethods();
			String[] pkClassParamTypes = config.getPkClassParamTypes();

			String[] fkClassMethods = config.getFkClassMethods();
			String[] fkClassParamTypes = config.getFkClassParamTypes();

			String[] fk1ClassMethods = config.getFk1ClassMethods();
			String[] fk1ClassParamTypes = config.getFk1ClassParamTypes();

			String[] fk2ClassMethods = config.getFk2ClassMethods();
			String[] fk2ClassParamTypes = config.getFk2ClassParamTypes();

			for (int j = 0; j < rowData.size(); j++) {

				// logger.fine("class ["+ obj.getClass().getName()+"] paramTypes
				// ["+ paramTypes[j]+"]" );
				// logger.fine("value ["+ rowData.get(j) +"] methods ["+
				// methods[j]+"] i ["+i+ "] j ["+ j+ "]" );
				if (Constants.TYPE_NA.equals(paramTypes[j]))
					continue;

				String value = rowData.get(j);
				if (Constants.TYPE_PK.equals(paramTypes[j])) {
					pkObj = setValue(pkObj, methods[j], getMapingClass(pkClassParamTypes[j]),
							getParamValue(pkClassParamTypes[j], value));
				} else if (Constants.TYPE_FK.equals(paramTypes[j])) {
					fkObj = setValue(fkObj, methods[j], getMapingClass(fkClassParamTypes[0]),
							getParamValue(fkClassParamTypes[0], value));
				} else if (Constants.TYPE_FK1.equals(paramTypes[j])) {
					if (value != null && !value.isEmpty())
						fk1Obj = setValue(fk1Obj, methods[j], getMapingClass(fk1ClassParamTypes[0]),
								getParamValue(fk1ClassParamTypes[0], value));
				} else if (Constants.TYPE_FK2.equals(paramTypes[j])) {
					if (value != null && !value.isEmpty())
						fk2Obj = setValue(fk2Obj, methods[j], getMapingClass(fk2ClassParamTypes[0]),
								getParamValue(fk2ClassParamTypes[0], value));
				} else {
					obj = setValue(obj, methods[j], getMapingClass(paramTypes[j]), getParamValue(paramTypes[j], value));
				}

			}

			// obj = appendCommonDateFieldForSapMaterial(obj);

			if (pkObj != null) {
				obj = setValue(obj, pkClassMethods[0], pkObj.getClass(), pkObj);
			}
			if (fkObj != null) {
				obj = setValue(obj, fkClassMethods[0], fkObj.getClass(), fkObj);
			}

			if (fk1Obj != null) {
				obj = setValue(obj, fk1ClassMethods[0], fk1Obj.getClass(), fk1Obj);
			}

			if (fk2Obj != null) {
				obj = setValue(obj, fk2ClassMethods[0], fk2Obj.getClass(), fk2Obj);
			}

			returnList.add(obj);
		}

		return returnList;
	}

	/**
	 * @param dataList
	 * @param config
	 * @param functionName
	 * @param fileName
	 * @return
	 * @throws CheckedException
	 */
	public List<Object> transferDrmsAgpAndMfr(List<String> dataList, Config config, String functionName,
			String fileName) throws CheckedException {
		List<Object> returnList = new ArrayList<Object>();
		List<String> fields = FunctionUtils.splitLineData((String) dataList.remove(0), Constants.SEPARATOR, true);
		int size = dataList.size();
		List<String> rowData = null;
		for (int i = 0; i < size; i++) {
			rowData = FunctionUtils.splitLineData((String) dataList.get(i), Constants.SEPARATOR, true);
			Object obj = null;
			try {
				obj = ResourceLoader.getInstance().getClassInstance(config.getClassName());
			} catch (ClassNotFoundException e) {
				throw new CheckedException("Method=transfer param=[className="+config.getClassName()+"] ClassNotFoundException ["+e.getMessage()+"]" );
			} catch (IllegalAccessException e1) {
				throw new CheckedException("Method=transfer param=[className="+config.getClassName()+"]  IllegalAccessException ["+e1.getMessage()+"]" );
			} catch (InstantiationException e2) {
				throw new CheckedException("Method=transfer param=[className="+config.getClassName()+"]  InstantiationException ["+e2.getMessage()+"]" );
			}
			String[] methods = config.getMethods();
			String[] paramTypes = config.getParamTypes();
			try {
//				logger.info("In Mehtod transferDrmsAgpAndMfr ::: rowData Size :: " + rowData.size());
				for (int j = 0; j < rowData.size(); j++) {

					if (Constants.TYPE_NA.equals(paramTypes[j]))
						continue;

					String value = rowData.get(j);
					Method method = obj.getClass().getMethod(methods[j], new Class[] { getMapingClass(paramTypes[j]) });
					method.invoke(obj, getParamValue(paramTypes[j], value));
				}
			} catch (IllegalAccessException e) {
				throw new CheckedException("Method=transfer  IllegalAccessException ["+e.getMessage()+"]" );
			} catch (NoSuchMethodException e) {
				throw new CheckedException("Method=transfer  NoSuchMethodException ["+e.getMessage()+"]" );
			} catch (InvocationTargetException e) {
				throw new CheckedException("Method=transfer  InvocationTargetException ["+e.getMessage()+"]" );
			} catch (SecurityException e) {
				throw new CheckedException("Method=transfer SecurityException ["+e.getMessage()+"]" );
			} catch (IllegalArgumentException e) {
				throw new CheckedException("Method=transfer  IllegalArgumentException ["+e.getMessage()+"]" );
			}

			returnList.add(obj);
		}
		return returnList;
	}

	/**
	 * @param tables
	 * @return
	 */
	public Config[] initCommonLoadingAndSchedule(String[] tables) {
		Config[] config = new Config[tables.length];
		for (int i = 0; i < tables.length; i++) {
			config[i] = getConfig(tables[i]);
		}
		return config;
	}

	// load date by txt loading unit
	// this is main for convert txt file data to java data structure
	public List loadData(String fullPath) throws CheckedException {
		TxtLoadingUnit unit = TxtLoadingUnit.getInstance();
		List rtnList = null;
		try {
			rtnList = unit.convertTxt2Java(fullPath);
		} catch (IOInteractException e) {
			throw new CheckedException("load data error", e);
		} catch (IOException e) {
			throw new CheckedException("load data error", e);
		}

		return rtnList;
	}

	// get all txt files in the specified folder
	public boolean isDataFileReady(String realPath, String fileName, String postFix) {
		if (FileUtils.isFile(realPath + fileName + postFix))
			return true;
		return false;
	}

	/*
	 * validation : check the dependency related files.
	 * 
	 * @Param: fileCon : file context object
	 * 
	 * @Return : if matched return true. otherwise return false;
	 */
	public boolean validationForDependency(FileContext fileCon) throws IOException, CheckedException {
		logger.info("Checking the dependency related files.");
		String[] tables = fileCon.getTables();
		List<String>[][] dataFileList = fileCon.getDataFileList();
		for (int i = 0; i < tables.length; i++) {
			if (dataFileList[i] == null || dataFileList[i].length == 0) {
				String msg = "Error, No found the dependency files ";
				throw new CheckedException(msg);
			}

		}
		return true;

	}

	/*
	 * validation : check whether the records count is matched or not.
	 * 
	 * @Param: fileCon : file context object
	 * 
	 * @Return : if matched return true. otherwise return false;
	 */
	public boolean validationForIsCountMatched(FileContext fileCon) throws CheckedException {

		logger.info("Checking file whether have match record count");
		String[] tables = fileCon.getTables();
		List<String>[][] dataFileList = fileCon.getDataFileList();
		ControlFile[][] allControlFiles = fileCon.getControlFiles();
		String tempFileName = null;
		String tempFuncName = null;
		try {
			for (int i = 0; i < tables.length; i++) {
				for (int j = 0; j < dataFileList[i].length; j++) {
					List<String> list = dataFileList[i][j];
					int recordCount = allControlFiles[i][j].readRecordCount();
					if ((list.size() - 1) != recordCount) {
						String msg = "Error, record count does not match between data file "
								+ "and control file when loading " + allControlFiles[i][j].getFile().getName();
						tempFileName = fileCon.getDataFiles()[i][j].getFile().getName();
						tempFuncName = fileCon.getConfig()[i].getFileName();
						throw new CheckedException(msg);
					}

				}
			}
		} catch (CheckedException e) {
			DataSyncError error = new DataSyncError();
			error.setFileName(tempFileName);
			error.setErrorRecord("-1");
			error.setFunctionCode(tempFuncName);
			error.setCreatingDate(DateUtils.getCurrentAsiaDateObj());
			error.setErrorMessage(e.getMessage());
			errorSB.createErrorMsg(error);
			throw e;
		} catch (IOException e) {
			DataSyncError error = new DataSyncError();
			error.setFileName(tempFileName);
			error.setErrorRecord("-1");
			error.setFunctionCode(tempFuncName);
			error.setCreatingDate(DateUtils.getCurrentAsiaDateObj());
			error.setErrorMessage(e.getMessage());
			errorSB.createErrorMsg(error);

			throw new CheckedException("IOException :" + e.getMessage());
		}
		return true;
	}

	/*
	 * validation : check whether the file exist or not.
	 * 
	 * @Param: fileCon : file context object
	 * 
	 * @Return : if exist return true. otherwise return false;
	 */

	public boolean validationForFileIsExist(FileContext fileCon) throws CheckedException {

		logger.info("Checking file whether is exist");
		String[] tables = fileCon.getTables();
		DataFile[][] allDataFiles = fileCon.getDataFiles();
		ControlFile[][] allControlFiles = fileCon.getControlFiles();

		String tempFileName = null;
		String tempFuncName = null;
		try {
			for (int i = 0; i < tables.length; i++) {
				DataFile[] dataFiles = allDataFiles[i];
				for (int j = 0; j < dataFiles.length; j++) {
					if (!dataFiles[j].isFile()) {
						String msg = "Missing data file " + dataFiles[j].getFile();
						tempFileName = dataFiles[j].getFile().getName();
						tempFuncName = fileCon.getConfig()[j].getFileName();
						throw new CheckedException(msg);
					}

					if (!allControlFiles[i][j].isFile()) {
						String msg = "Missing control file " + allControlFiles[i][j].getFile();
						tempFileName = dataFiles[j].getFile().getName();
						tempFuncName = fileCon.getConfig()[j].getFileName();
						throw new CheckedException(msg);
					}
				}

			}
		} catch (CheckedException e) {
			DataSyncError error = new DataSyncError();
			error.setFileName(tempFileName);
			error.setErrorRecord("-1");
			error.setFunctionCode(tempFuncName);
			error.setCreatingDate(DateUtils.getCurrentAsiaDateObj());
			error.setErrorMessage(e.getMessage());
			errorSB.createErrorMsg(error);
			throw e;
		}

		return true;

	}

	/*
	 * Find out the files which will be handled.
	 * 
	 * @Param: fileCon : file context object
	 * 
	 * @Return : file context object
	 */
	public FileContext findFiles(FileContext fileCon) throws CheckedException {
		logger.info("Call findFiles");
		String[] tables = fileCon.getTables();
		String currentDate = DateUtils.getCurrentAsiaDate();
		DataFile[][] allDataFiles = new DataFile[tables.length][];
		ControlFile[][] allControlFiles = new ControlFile[tables.length][];

		for (int i = 0; i < tables.length; i++) {

			File[] files = null;
			DataFile[] dataFiles = null;
			ControlFile[] controlFiles = null;

			Config config = fileCon.getConfig()[i];
			logger.info("file path : " + config.getFilePath());
			if (config.isAllowMultipleLoad()) {
				config.setFilePath("C:\\sapfiles\\");
				File path = new File(config.getFilePath());
//				File path = new File("C:\\sapfiles\\");
				files = path.listFiles(new FilenameFilterImp(config.getFileName()));
			} else {
				files = new File[1];
				files[0] = new File(
						config.getFilePath() + config.getFileName() + currentDate + config.getDataFilePostfix());
			}

			Arrays.sort(files);
			dataFiles = new DataFile[files.length];
			controlFiles = new ControlFile[files.length];
			for (int j = 0; j < files.length; j++) {
				dataFiles[j] = new DataFile(files[j]);
				String name = new String(files[j].getName());
				name = name.substring(0, name.indexOf(config.getDataFilePostfix())) + config.getControlFilePostfix();
				controlFiles[j] = new ControlFile(new File(config.getFilePath() + name));
			}

			allDataFiles[i] = dataFiles;
			allControlFiles[i] = controlFiles;

			if (allDataFiles[i].length == 0) {
				DataSyncError error = new DataSyncError();
				error.setFileName("NA");
				error.setErrorRecord("-1");
				error.setFunctionCode(tables[0]);
				error.setCreatingDate(DateUtils.getCurrentAsiaDateObj());
//				error.setErrorMessage(MessageFormatorUtil
//						.getParameterizedString(CommonConstants.WQ_EJB_MASTER_DATA_NO_FILE_FOUND_EXCEPTION, tables[0]));
				String errMsg = MessageFormatorUtil
						.getParameterizedString(new Locale("en"), CommonConstants.WQ_EJB_MASTER_DATA_NO_FILE_FOUND_EXCEPTION, tables[0]);
				error.setErrorMessage(errMsg);
				errorSB.createErrorMsg(error);
				throw new CheckedException(errMsg);
//				throw new CheckedException(CommonConstants.WQ_EJB_MASTER_DATA_NO_FILE_FOUND_EXCEPTION,
//						new Object[] { tables[0] });
			}
		}

		fileCon.setControlFiles(allControlFiles);
		fileCon.setDataFiles(allDataFiles);

		return fileCon;
	}

	/*
	 * Loaded the raw file data to file context.
	 * 
	 * @Param: fileCon : file context object
	 */
	public FileContext loadFiles(FileContext fileCon) throws CheckedException {
		logger.info("call loadFiles method");
		String[] tables = fileCon.getTables();
		DataFile[][] allDataFiles = fileCon.getDataFiles();
		List<String>[][] dataFileList = new List[allDataFiles.length][allDataFiles[0].length];
		try {
			for (int i = 0; i < tables.length; i++) {
				DataFile[] dataFiles = allDataFiles[i];

				for (int j = 0; j < dataFiles.length; j++) {
					List<String> list = dataFiles[j].loadData();
					dataFileList[i][j] = list;
				}
			}
			fileCon.setDataFileList(dataFileList);
		} catch (Exception e)

		{
			throw new CheckedException(CommonConstants.WQ_EJB_MASTER_DATA_LOADFILES_EXCPTION + e.getMessage());
		}
		return fileCon;
	}

	/*
	 * backup the file and delete the files.
	 * 
	 * @Param: fileCon : file context object
	 */
	public void backupFiles(FileContext fileCon) throws CheckedException {
		logger.info("call backupFiles method");
		String[] tables = fileCon.getTables();
		DataFile[][] allDataFiles = fileCon.getDataFiles();
		ControlFile[][] allControlFiles = fileCon.getControlFiles();

		String tempFileName = null;
		String tempFuncName = null;
		try {
			for (int i = 0; i < tables.length; i++) {

				Config config = fileCon.getConfig()[i];
				DataFile[] dataFiles = allDataFiles[i];
				for (int j = 0; j < dataFiles.length; j++) {
					if (!FileUtils.isDir(config.getBackupPath())) {
						File backDir = new File(config.getBackupPath());
						backDir.mkdir();
					}
					SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
					String uploadTime = formatter.format(new Date());

					String dataFileDestination = config.getBackupPath() + allDataFiles[i][j].getFile().getName() + "."
							+ uploadTime;
					File dataFileDestFile = new File(dataFileDestination);
					FileUtils.copyFile(allDataFiles[i][j].getFile(), dataFileDestFile);
					FileUtils.delFile(allDataFiles[i][j].getFile().getAbsolutePath());

					String controlFileDestination = config.getBackupPath() + allControlFiles[i][j].getFile().getName()
							+ "." + uploadTime;
					File controlFileDestFile = new File(controlFileDestination);
					FileUtils.copyFile(allControlFiles[i][j].getFile(), controlFileDestFile);
					FileUtils.delFile(allControlFiles[i][j].getFile().getAbsolutePath());
				}
			}
		} catch (Exception e) {
			throw new CheckedException(CommonConstants.WQ_WEB_BACKUP_FILE_EXCEPTION, e);
		}

	}

	/*
	 * Get the class of the input type
	 * 
	 * @Param: type : object type.
	 * 
	 * @return: Object : return a class object
	 */
	public Class<?> getMapingClass(String type) {
		Class<?> theClass = String.class;
		if (Constants.TYPE_STR.equalsIgnoreCase(type)) {
			theClass = String.class;
		} else if (Constants.TYPE_INT.equalsIgnoreCase(type)) {
			theClass = Integer.class;
		} else if (Constants.TYPE_DOUBLE.equalsIgnoreCase(type)) {
			theClass = Double.class;
		} else if (Constants.TYPE_LONG.equalsIgnoreCase(type)) {
			theClass = long.class;
		} else if (Constants.TYPE_DATE.equalsIgnoreCase(type)) {
			theClass = Date.class;
			// DateUtils.convertDate(DateUtils.switchStringToDate(value,
			// "yyyyMMdd"));
		} else if (Constants.TYPE_INTEGER.equalsIgnoreCase(type)) {
			theClass = Integer.class;
			// DateUtils.convertDate(DateUtils.switchStringToDate(value,
			// "yyyyMMdd"));
		} else if (Constants.TYPE_BOOLEAN.equalsIgnoreCase(type)) {
			theClass = Boolean.class;
			// DateUtils.convertDate(DateUtils.switchStringToDate(value,
			// "yyyyMMdd"));
		}

		return theClass;
	}

	/*
	 * Transfer String value to object based on config
	 * 
	 * @Param: type : object type.
	 * 
	 * @Param: value : original value.
	 * 
	 * @return: Object : return a object
	 */

	public Object getParamValue(String type, String value) {
		Object obj = value;
		if (Constants.TYPE_STR.equalsIgnoreCase(type)) {
			obj = value;
		} else if (Constants.TYPE_INT.equalsIgnoreCase(type)) {
			obj = Integer.valueOf(value);
		} else if (Constants.TYPE_DOUBLE.equalsIgnoreCase(type)) {
			obj = Double.valueOf(value);
		} else if (Constants.TYPE_LONG.equalsIgnoreCase(type)) {
			obj = Long.valueOf(value).longValue();
		} else if (Constants.TYPE_DATE.equalsIgnoreCase(type)) {
			obj = DateUtils.convertDate(DateUtils.switchStringToDate(value, "yyyyMMdd"));
		} else if (Constants.TYPE_INTEGER.equalsIgnoreCase(type)) {
			Double tempCustNum = Double.parseDouble(value);
			obj = new Integer(String.valueOf(tempCustNum.intValue()));
		} else if (Constants.TYPE_BOOLEAN.equalsIgnoreCase(type)) {
			if ("X".equalsIgnoreCase(value))
				obj = new Boolean(true);
			else
				obj = new Boolean(false);
		}

		return obj;
	}

	/*
	 * Transfer raw row data to object
	 * 
	 * @Param: dataList : raw row data list
	 * 
	 * @Param: config : schedule config.
	 * 
	 * @return: List<Object> : return a list of object
	 * 
	 * Updated by Tonmy . added two parameters: 1, fileName , 2 , functionName
	 */
	public List<Object> transfer(List<String> dataList, Config config, String functionName, String fileName)
			throws CheckedException {
		List<Object> returnList = new ArrayList<Object>();
		List<String> fields = FunctionUtils.splitLineData((String) dataList.remove(0), "|", true);
		int size = dataList.size();
		List<String> rowData = null;
		try {
			for (int i = 0; i < size; i++) {

				rowData = FunctionUtils.splitLineData((String) dataList.get(i), "|", true);
				Object obj = ResourceLoader.getInstance().getClassInstance(config.getClassName());
				String[] methods = config.getMethods();
				String[] paramTypes = config.getParamTypes();

				for (int j = 0; j < rowData.size(); j++) {

					if (Constants.TYPE_NA.equals(paramTypes[j]))
						continue;

					String value = rowData.get(j);
					Method method = obj.getClass().getMethod(methods[j], new Class[] { getMapingClass(paramTypes[j]) });
					method.invoke(obj, getParamValue(paramTypes[j], value));
				}
				returnList.add(obj);
			}
		} catch (InstantiationException e) { 
			throw new CheckedException("Method=setValue  InstantiationException ["+e.getMessage()+"]");
		} catch (IllegalAccessException e) {
			throw new CheckedException("Method=setValue  IllegalAccessException ["+e.getMessage() +"]");
		} catch (ClassNotFoundException e) {
			throw new CheckedException("Method=setValue  ClassNotFoundException [{"+e.getMessage()+"}]");
		} catch (NoSuchMethodException e) {
			throw new CheckedException("Method=setValue  NoSuchMethodException [{"+e.getMessage()+"}]");
		} catch (InvocationTargetException e) {
			throw new CheckedException("Method=setValue  InvocationTargetException [{"+e.getMessage()+"}]");
		} catch (SecurityException e) {
			throw new CheckedException("Method=setValue  SecurityException [{"+ e.getMessage()+"}]");
		}

		return returnList;
	}

	/*
	 * Set attribute value to the object
	 */
	public Object setValue(Object obj, String methodName, Class<?> classObj, Object value) throws CheckedException {
		try {
			Method method = obj.getClass().getMethod(methodName, new Class[] { classObj });
			method.invoke(obj, value);
			return obj; 
		} catch (NoSuchMethodException e) {
			throw new CheckedException("Method=setValue para=[methodName="+methodName+" value="+ value+"] NoSuchMethodException ["+e.getMessage()+"]");
		} catch (SecurityException e1) {
			throw new CheckedException("Method=setValue para=[methodName="+methodName+" value="+ value+"] SecurityException ["+e1.getMessage()+"]");
		} catch (IllegalAccessException e2) {
			throw new CheckedException("Method=setValue para=[methodName="+methodName+" value="+ value+"] IllegalAccessException ["+e2.getMessage()+"]");
		} catch (IllegalArgumentException e3) {
			throw new CheckedException("Method=setValue para=[methodName="+methodName+" value="+ value+"] IllegalArgumentException ["+e3.getMessage()+"]" );
		} catch (InvocationTargetException e4) {
			throw new CheckedException("Method=setValue para=[methodName="+methodName+" value="+ value+"] InvocationTargetException ["+e4.getMessage()+"]");
		}
	}

	public Object appendCommonField(Object obj) throws CheckedException {
		if (obj == null)
			return obj;

		User user = new User();
		user.setId(999999);
		Date cal = DateUtils.getCurrentAsiaDateObj();

		obj = setValue(obj, "setCreatedOn", Date.class, cal);
		obj = setValue(obj, "setLastUpdatedOn", Date.class, cal);
		obj = setValue(obj, "setCreatedBy", User.class, user);
		obj = setValue(obj, "setUdpatedBy", User.class, user);
		obj = setValue(obj, "setVersion", int.class, 1);

		if (obj.getClass().getName().equals("com.avnet.emasia.webquote.entity.Customer")) {
			obj = setValue(obj, "setNewCustomerFlag", Boolean.class, false);
		}
		return obj;

	}

	public Object appendCommonFields(Object obj) throws WebQuoteException {
		if (obj == null)
			return obj;

		Date cal = DateUtils.getCurrentAsiaDateObj();

		try {
			obj = setValue(obj, "setCreatedOn", Date.class, cal);
			obj = setValue(obj, "setLastUpdatedOn", Date.class, cal);
			obj = setValue(obj, "setVersion", int.class, 1);

		} catch (CheckedException e) {
			throw new WebQuoteException(e);
		}
		return obj;

	}

	public Object appendCommonDateField(Object obj) throws CheckedException {
		if (obj == null)
			return obj;

		Date cal = DateUtils.getCurrentAsiaDateObj();

		obj = setValue(obj, "setCreatedOn", Date.class, cal);
		obj = setValue(obj, "setLastUpdatedOn", Date.class, cal);

		return obj;

	}

	public Object appendCommonDateFieldForSapMaterial(Object obj) throws CheckedException {
		if (obj == null)
			return obj;

		Date cal = DateUtils.getCurrentAsiaDateObj();

		obj = setValue(obj, "setCreateDate", Date.class, cal);
		obj = setValue(obj, "setUpdateDate", Date.class, cal);

		return obj;

	}

	public Object appendCreatedBy(Object obj) throws CheckedException {
		if (obj == null)
			return obj;

		// To-Do , it should be a "System" or "dataloading" account.
		User sysUser = new User();
		sysUser.setId(999999);

		obj = setValue(obj, "setCreatedBy", User.class, sysUser);

		return obj;
	}

	public Object appendUpdatedBy(Object obj) throws CheckedException {
		if (obj == null)
			return obj;

		User sysUser = new User();
		sysUser.setId(999999);

		obj = setValue(obj, "setLastUpdatedBy", User.class, sysUser);

		return obj;

	}

	public Object appendOriginalData(Object obj, String str) throws CheckedException {
		if (obj == null)
			return obj;

		obj = setValue(obj, "setOriginalSapData", String.class, str);

		return obj;
	}

	/*
	 * Send error mail
	 * 
	 * @param errorMsg : email content
	 */
	public void sendErrorEmail(String errorMsg,String subjectStr) {
		// TO-DO
		String jbossNodeName = System.getProperty(HATimerService.JBOSS_NODE_NAME);
		MailInfoBean mib = new MailInfoBean();
		mib.setMailFrom("GIS-EM-ASIA-ECO-WEB@Avnet.com");
		// add by Lenon.Yang(043044) 2016/05/23
		String subject = subjectStr;
		if (org.apache.commons.lang.StringUtils.isNotBlank(jbossNodeName)) {
			subject += "(Jboss Node:" + jbossNodeName + ")";
		}
		mib.setMailSubject(subject);
		mib.setMailContent(errorMsg);
		List<String> toList = new ArrayList<String>();
		String[] emailArray = schedConfSB.getErrorEmailAddress();
		for (String address : emailArray) {
			toList.add(address);
		}
		mib.setMailTo(toList);

		try {
			mailUtilsSB.sendTextMail(mib);
		} catch (Exception e) {
			logger.log(Level.SEVERE,
					"Exception occured in sending email for error message: " + errorMsg + ", Sender: "
							+ mib.getMailFrom() + ", " + "Recepient(s): " + mib.getMailTo().toString() + ", Subject: "
							+ mib.getMailSubject() + ", Reason for failure: "
							+ MessageFormatorUtil.getParameterizedStringFromException(e),
					e);
		}
	}

	/**
	 * @param fileCon
	 * @throws CheckedException
	 */
	public void loadingActionDrmsAgpAndMFRFileContext(FileContext fileCon) throws CheckedException {
		fileCon = findFiles(fileCon);

		if (!validationForFileIsExist(fileCon)) {
			return;
		}

		fileCon = loadFiles(fileCon);

		if (!validationForIsCountMatched(fileCon)) {
			return;
		}

		saveToDB(fileCon);

		backupFiles(fileCon);
	}

	/**
	 * @param fileCon
	 * @throws CheckedException
	 */
	public void loadingActionLoadingAndScheduleFileContext(FileContext fileCon) throws CheckedException {
		fileCon = findFiles(fileCon);
		if (!validationForFileIsExist(fileCon)) {
			return;
		}
		fileCon = loadFiles(fileCon);

		if (!validationForIsCountMatched(fileCon)) {
			return;
		}
		saveToDB(fileCon);
		backupFiles(fileCon);
	}

	/*
	 * save the data
	 * 
	 * @param fileCon : fileContext object
	 */
	public void saveToDB(FileContext fileCon) throws CheckedException {

		logger.info("call saveToDB ");
		List<String>[][] dataFileList = fileCon.getDataFileList();

		for (int i = 0; i < dataFileList[0].length; i++) {

			List<Object>[] objectList = new List[dataFileList.length];
			String[] fileNameArray = new String[dataFileList.length];
			String[] functionNameArray = new String[dataFileList.length];

			for (int j = 0; j < dataFileList.length; j++) {
				try {
					Config config = fileCon.getConfig()[j];
					objectList[j] = transfer(dataFileList[j][i], config, fileCon.getConfig()[j].getFileName(),
							fileCon.getDataFiles()[j][i].getFile().getName());
					fileNameArray[j] = fileCon.getDataFiles()[j][i].getFile().getName();
					functionNameArray[j] = fileCon.getConfig()[j].getFileName();
				} catch (CheckedException e) {
					DataSyncError error = new DataSyncError();
					error.setFileName(fileCon.getDataFiles()[j][i].getFile().getName());
					error.setErrorRecord("-1");
					error.setFunctionCode(fileCon.getConfig()[j].getFileName());
					error.setCreatingDate(DateUtils.getCurrentAsiaDateObj());
					error.setErrorMessage("[function] : " + fileCon.getConfig()[j].getFileName() + " [fileName] : "
							+ fileCon.getDataFiles()[j][i].getFile().getName()
							+ " The error occurred durring data transferred to object : [detail] : " + e.getMessage());
					errorSB.createErrorMsg(error);
					throw new CheckedException(CommonConstants.WQ_WEB_TRANSFER_EXCPTION + error.getErrorMessage());
				}
			}

			merge(objectList, fileNameArray, functionNameArray);
		}

	}

	/*
	 * call session ejb bean
	 */
	public void merge(List<Object>[] objList, String[] fileNameArray, String[] functionNameArray)
			throws CheckedException {
		masterSB.mergePos(objList, fileNameArray, functionNameArray, errorList);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @param dataList
	 * @param config
	 * @param functionName
	 * @param fileName
	 * @return
	 * @throws CheckedException
	 */
	public List<Object> transferSalesOrder(List<String> dataList, Config config, String functionName, String fileName)
			throws CheckedException {
		List<Object> returnList = new ArrayList<Object>();
		List<String> fields = FunctionUtils.splitLineData((String) dataList.remove(0), Constants.SEPARATOR, true);
		int size = dataList.size();
		List<String> rowData = null;
		for (int i = 0; i < size; i++) {
			rowData = FunctionUtils.splitLineData((String) dataList.get(i), Constants.SEPARATOR, true);
			Object obj = null;
			Object pkObj = null;

			try {
				logger.log(Level.FINE,
						"Method transferSalesOrder : functionName " + functionName + "fileName : " + fileName);
				obj = ResourceLoader.getInstance().getClassInstance(config.getClassName());
				if (config.getPkClass() != null) {
					pkObj = ResourceLoader.getInstance().getClassInstance(config.getPkClass());
				}

			} catch (ClassNotFoundException e) {
				throw new CheckedException("Method=transfer param=[className={"+config.getClassName()+"}}] ClassNotFoundException[{"+e.getMessage()+"}]");
			} catch (IllegalAccessException e) {
				throw new CheckedException("Method=transfer param=[className={"+config.getClassName()+"}] IllegalAccessException[{"+e.getMessage()+"}]");
			} catch (InstantiationException e) {
				throw new CheckedException("Method=transfer param=[className={"+config.getClassName()+"}]  InstantiationException[{"+e.getMessage()+"}]");
			}
			String[] methods = config.getMethods();
			String[] paramTypes = config.getParamTypes();

			String[] pkClassMethods = config.getPkClassMethods();
			String[] pkClassParamTypes = config.getPkClassParamTypes();

			for (int j = 0; j < rowData.size(); j++) {
				if (Constants.TYPE_NA.equals(paramTypes[j]))
					continue;

				String rowDataStr = rowData.get(j);
				if (("setSalesOrderItemNumber".endsWith(methods[j])) && rowDataStr != null
						&& StringUtils.isNumeric(rowDataStr)) {
					Integer tempCustNum = Integer.parseInt(rowDataStr);
					rowDataStr = String.valueOf(tempCustNum);
				}

				if (Constants.TYPE_PK.equals(paramTypes[j])) {
					pkObj = setValue(pkObj, methods[j], getMapingClass(pkClassParamTypes[j]),
							getParamValue(pkClassParamTypes[j], rowDataStr));
				} else {
					obj = setValue(obj, methods[j], getMapingClass(paramTypes[j]),
							getParamValue(paramTypes[j], rowDataStr));
				}
			}

			// update common fields
			obj = appendCommonDateField(obj);
			if (pkObj != null) {
				obj = setValue(obj, pkClassMethods[0], pkObj.getClass(), pkObj);
			}
			returnList.add(obj);
		}
		return returnList;
	}

	/**
	 * @param dataList
	 * @param config
	 * @param functionName
	 * @param fileName
	 * @return
	 * @throws CheckedException
	 */
	public List<Object> transferSalesOrg(List<String> dataList, Config config, String functionName, String fileName)
			throws CheckedException {
		List<Object> returnList = new ArrayList<Object>();
		List<String> fields = FunctionUtils.splitLineData((String) dataList.remove(0), Constants.SEPARATOR, true);
		int size = dataList.size();
		List<String> rowData = null;
		for (int i = 0; i < size; i++) {
			rowData = FunctionUtils.splitLineData((String) dataList.get(i), Constants.SEPARATOR, true);
			Object obj = null;
			Object fkObj = null;

			try {
				obj = ResourceLoader.getInstance().getClassInstance(config.getClassName());
				if (config.getFkClass() != null) {
					fkObj = ResourceLoader.getInstance().getClassInstance(config.getFkClass());
				}
			} catch (ClassNotFoundException e) {
				throw new CheckedException("Method=transfer param=[className={"+config.getClassName()+"}}] ClassNotFoundException[{"+e.getMessage()+"}]");
			} catch (IllegalAccessException e) {
				throw new CheckedException("Method=transfer param=[className={"+config.getClassName()+"}] IllegalAccessException[{"+e.getMessage()+"}]");
			} catch (InstantiationException e) {
				throw new CheckedException("Method=transfer param=[className={"+config.getClassName()+"}]  InstantiationException[{"+e.getMessage()+"}]");
			}
			String[] methods = config.getMethods();
			String[] paramTypes = config.getParamTypes();

			String[] fkClassMethods = config.getFkClassMethods();
			String[] fkClassParamTypes = config.getFkClassParamTypes();

			for (int j = 0; j < rowData.size(); j++) {

				if (Constants.TYPE_NA.equals(paramTypes[j]))
					continue;

				if (Constants.TYPE_FK.equals(paramTypes[j])) {
					fkObj = setValue(fkObj, methods[j], getMapingClass(fkClassParamTypes[0]),
							getParamValue(fkClassParamTypes[0], rowData.get(j)));
				} else {
					obj = setValue(obj, methods[j], getMapingClass(paramTypes[j]),
							getParamValue(paramTypes[j], rowData.get(j)));
				}

			}

			// update common fields
			obj = appendCommonDateField(obj);

			if (fkObj != null) {
				List bizList = new ArrayList<BizUnit>();
				bizList.add((BizUnit) fkObj);
				((SalesOrg) obj).setBizUnits(bizList);
				// obj = setValue(obj, fkClassMethods[0], fkObj.getClass() ,
				// fkObj ) ;
			}

			returnList.add(obj);
		}
		return returnList;
	}

	public List<Object> transferSpecialCustomer(List<String> dataList, Config config, String functionName,
			String fileName) throws CheckedException {
		List<Object> returnList = new ArrayList<Object>();
		List<String> fields = FunctionUtils.splitLineData((String) dataList.remove(0), Constants.SEPARATOR, true);
		int size = dataList.size();
		List<String> rowData = null;
		for (int i = 0; i < size; i++) {

			rowData = FunctionUtils.splitLineData((String) dataList.get(i), Constants.SEPARATOR, true);
			Object obj = null;

			try {
				logger.log(Level.FINE,
						"Method transferSpecialCustomer : functionName " + functionName + "fileName : " + fileName);
				obj = ResourceLoader.getInstance().getClassInstance(config.getClassName());
			} catch (ClassNotFoundException e) {
				throw new CheckedException("Method=transfer param=[className={"+config.getClassName()+"}}] ClassNotFoundException[{"+e.getMessage()+"}]");
			} catch (IllegalAccessException e) {
				throw new CheckedException("Method=transfer param=[className={"+config.getClassName()+"}] IllegalAccessException[{"+e.getMessage()+"}]");
			} catch (InstantiationException e) {
				throw new CheckedException("Method=transfer param=[className={"+config.getClassName()+"}]  InstantiationException[{"+e.getMessage()+"}]");
			}
			String[] methods = config.getMethods();
			String[] paramTypes = config.getParamTypes();
			try {
//				logger.info("In Mehtod transferSpecialCustomer ::: rowData Size :: " + rowData.size());
				for (int j = 0; j < rowData.size(); j++) {

					if (Constants.TYPE_NA.equals(paramTypes[j]))
						continue;

					String rowDataStr = rowData.get(j);
					if (("setSoldToCustomerNumber".endsWith(methods[j]) || "setEndCustomerNumber".endsWith(methods[j]))
							&& rowDataStr != null && StringUtils.isNumeric(rowDataStr)) {
						Integer tempCustNum = Integer.parseInt(rowDataStr);
						rowDataStr = String.valueOf(tempCustNum);
					}

					Method method = obj.getClass().getMethod(methods[j], new Class[] { getMapingClass(paramTypes[j]) });
					method.invoke(obj, getParamValue(paramTypes[j], rowDataStr));
				}

			} catch (NoSuchMethodException e) {
				throw new CheckedException("Method=transfer  NoSuchMethodException ["+e.getMessage()+"]" );
			} catch (SecurityException e1) {
				throw new CheckedException("Method=transfer SecurityException ["+e1.getMessage()+"]" );
			} catch (IllegalAccessException e2) {
				throw new CheckedException("Method=transfer  IllegalAccessException ["+e2.getMessage()+"]" );
			} catch (IllegalArgumentException e3) {
				throw new CheckedException("Method=transfer  IllegalArgumentException ["+e3.getMessage()+"]" );
			} catch (InvocationTargetException e4) {
				throw new CheckedException("Method=transfer  InvocationTargetException ["+e4.getMessage()+"]" );
			}

			returnList.add(obj);
		}
		return returnList;
	}

	public List<Object> transferCustomer(List<String> dataList, Config config, String functionName, String fileName,
			String param) throws CheckedException {
		List<Object> returnList = new ArrayList<Object>();
		List<String> fields = FunctionUtils.splitLineData((String) dataList.remove(0), Constants.SEPARATOR, true);
		int size = dataList.size();
		List<String> rowData = null;
		for (int i = 0; i < size; i++) {
			Object obj = null;
			Object pkObj = null;
			Object fkObj = null;
			try {
				rowData = FunctionUtils.splitLineData((String) dataList.get(i), Constants.SEPARATOR, true);
				obj = ResourceLoader.getInstance().getClassInstance(config.getClassName());

				if (config.getPkClass() != null) {
					pkObj = ResourceLoader.getInstance().getClassInstance(config.getPkClass());
				}

				if (config.getFkClass() != null) {
					fkObj = ResourceLoader.getInstance().getClassInstance(config.getFkClass());
				}
			} catch (ClassNotFoundException e) { 
				throw new CheckedException("Method=transfer param=[className={"+config.getClassName()+"}}] ClassNotFoundException[{"+e.getMessage()+"}]");
			} catch (IllegalAccessException e) {
				throw new CheckedException("Method=transfer param=[className={"+config.getClassName()+"}] IllegalAccessException[{"+e.getMessage()+"}]");
			} catch (InstantiationException e) {
				throw new CheckedException("Method=transfer param=[className={"+config.getClassName()+"}]  InstantiationException[{"+e.getMessage()+"}]");
			}

			String[] methods = config.getMethods();
			String[] paramTypes = config.getParamTypes();
			String[] pkClassParamTypes = config.getPkClassParamTypes();
			String[] fkClassParamTypes = config.getFkClassParamTypes();
			for (int j = 0; j < rowData.size(); j++) {

				if (Constants.TYPE_NA.equals(paramTypes[j]))
					continue;

				String rowDataStr = rowData.get(j);
				/*if (param != null && "".equals("")) {
					if (("setCustomerNumber".endsWith(methods[j]) || "setPartnerCustomerCode".endsWith(methods[j])
							|| "setAddressNumber".endsWith(methods[j]) || "setCustomerAddress".endsWith(methods[j]))
							&& rowDataStr != null && StringUtils.isNumeric(rowDataStr)) {
						Integer tempCustNum = Integer.parseInt(rowDataStr);
						rowDataStr = String.valueOf(tempCustNum);
					}
				} else {
					if (("setCustomerNumber".endsWith(methods[j]) || "setAddressNumber".endsWith(methods[j])
							|| "setCustomerAddress".endsWith(methods[j])) && rowDataStr != null
							&& StringUtils.isNumeric(rowDataStr)) {
						Integer tempCustNum = Integer.parseInt(rowDataStr);
						rowDataStr = String.valueOf(tempCustNum);
					}
				}*/
				//Fixed by Damonchen@20181210
				if (("setCustomerNumber".endsWith(methods[j]) || "setPartnerCustomerCode".endsWith(methods[j])
						|| "setAddressNumber".endsWith(methods[j]) || "setCustomerAddress".endsWith(methods[j]))
						&& rowDataStr != null && StringUtils.isNumeric(rowDataStr)) {
					Integer tempCustNum = Integer.parseInt(rowDataStr);
					rowDataStr = String.valueOf(tempCustNum);
				}

				if (Constants.TYPE_PK.equals(paramTypes[j])) {
					pkObj = setValue(pkObj, methods[j], getMapingClass(pkClassParamTypes[j]),
							getParamValue(pkClassParamTypes[j], rowDataStr));
				} else if (Constants.TYPE_FK.equals(paramTypes[j])) {
					fkObj = setValue(fkObj, methods[j], getMapingClass(fkClassParamTypes[0]),
							getParamValue(fkClassParamTypes[0], rowDataStr));
				} else {

					obj = setValue(obj, methods[j], getMapingClass(paramTypes[j]),
							getParamValue(paramTypes[j], rowDataStr));
				}

			}
			// update common fields

			obj = appendCommonDateField(obj);

			if (pkObj != null) {
				obj = setValue(obj, "setId", pkObj.getClass(), pkObj);
			}
			if (fkObj != null) {
				obj = setValue(obj, "setSalesPlant", fkObj.getClass(), fkObj);
			}

			returnList.add(obj);
		}
		return returnList;
	}

	public List<Object> transferDrms(List<String> dataList, Config config, String functionName, String fileName,
			List<DataSyncError> errorList) throws CheckedException {
		List<Object> returnList = new ArrayList<Object>();
		List<String> fields = FunctionUtils.splitLineData((String) dataList.remove(0), Constants.SEPARATOR, true);
		int size = dataList.size();
		List<String> rowData = null;

		for (int i = 0; i < size; i++) {
			rowData = FunctionUtils.splitLineData((String) dataList.get(i), Constants.SEPARATOR, true);
			Object obj = null;
			Object pkObj = null;
			Object fkObj = null;
			try {
				obj = ResourceLoader.getInstance().getClassInstance(config.getClassName());
				if (config.getPkClass() != null) {
					pkObj = ResourceLoader.getInstance().getClassInstance(config.getPkClass());
				}
				if (config.getFkClass() != null) {
					fkObj = ResourceLoader.getInstance().getClassInstance(config.getFkClass());
				}
			}catch(ClassNotFoundException e)
				{
					throw new CheckedException("Method=transfer param=[className="+config.getClassName()+"] ClassNotFoundException ["+e.getMessage()+"]" );
				}catch(IllegalAccessException e1)
				{
					throw new CheckedException("Method=transfer param=[className="+config.getClassName()+"]  IllegalAccessException ["+e1.getMessage()+"]" );
				}catch(InstantiationException e2)
				{
					throw new CheckedException("Method=transfer param=[className="+config.getClassName()+"]  InstantiationException ["+e2.getMessage()+"]" );
				}
			String[] methods = config.getMethods();
			String[] paramTypes = config.getParamTypes();

			String[] pkClassMethods = config.getPkClassMethods();
			String[] fkClassMethods = config.getFkClassMethods();

			String[] pkClassParamTypes = config.getPkClassParamTypes();
			String[] fkClassParamTypes = config.getFkClassParamTypes();

			try {
				for (int j = 0; j < rowData.size(); j++) {
					if (rowData.get(j) != null && !"".equals(rowData.get(j))) {
						if (Constants.TYPE_NA.equals(paramTypes[j]))
							continue;

						String rowDataStr = rowData.get(j);
						if ("setCustomerNumber".endsWith(methods[j]) && StringUtils.isNumeric(rowDataStr)) {
							Integer tempCustNum = Integer.parseInt(rowDataStr);
							rowDataStr = String.valueOf(tempCustNum);
						}

						if (Constants.TYPE_PK.equals(paramTypes[j])) {
							pkObj = setValue(pkObj, methods[j], getMapingClass(pkClassParamTypes[j]),
									getParamValue(pkClassParamTypes[j], rowDataStr));
						} else if (Constants.TYPE_FK.equals(paramTypes[j])) {
							fkObj = setValue(fkObj, methods[j], getMapingClass(fkClassParamTypes[0]),
									getParamValue(fkClassParamTypes[0], rowDataStr));
						} else {
							obj = setValue(obj, methods[j], getMapingClass(paramTypes[j]),
									getParamValue(paramTypes[j], rowDataStr));
						}
					} else {
						// sales org can be null
						if (Constants.TYPE_FK.equals(paramTypes[j])) {
							fkObj = null;
						}
					}

				}
				obj = appendCommonDateField(obj);
				obj = appendOriginalData(obj, (String) dataList.get(i));

				if (pkObj != null) {
					obj = setValue(obj, pkClassMethods[0], pkObj.getClass(), pkObj);
				}
				if (fkObj != null) {
					obj = setValue(obj, fkClassMethods[0], fkObj.getClass(), fkObj);
				}
				returnList.add(obj);

			} catch (Exception es) {
				logger.log(Level.SEVERE, "Exception occured in transferDrms() while transfer for file name: " + fileName
						+ ", Reason for failure: " + MessageFormatorUtil.getParameterizedStringFromException(es), es);
				errorList = masterSB.addError(errorList,
						"|initial data error|" + es.getMessage() + "|" + (String) dataList.get(i), fileName,
						functionName, 0);
				// throw new CheckedException("Method=setValue Exception
				// ["+es.getMessage()+"]" );
			}
		}
		return returnList;
	}

	public List<Object> transferFreeStockLoadingAndScedule(List<String> dataList, Config config, String functionName,
			String fileName) throws CheckedException {
		List<Object> returnList = new ArrayList<Object>();
		List<String> fields = FunctionUtils.splitLineData((String) dataList.remove(0), Constants.SEPARATOR, true);
		int size = dataList.size();
		List<String> rowData = null;
		for (int i = 0; i < size; i++) {
			rowData = FunctionUtils.splitLineData((String) dataList.get(i), Constants.SEPARATOR, true);
			Object obj = null;
			Object pkObj = null;

			try {
				obj = ResourceLoader.getInstance().getClassInstance(config.getClassName());
				if (config.getPkClass() != null) {
					pkObj = ResourceLoader.getInstance().getClassInstance(config.getPkClass());
				}
			} catch (ClassNotFoundException e) { 
				throw new CheckedException("Method=transfer param=[className={"+config.getClassName()+"}}] ClassNotFoundException[{"+e.getMessage()+"}]");
			} catch (IllegalAccessException e) {
				throw new CheckedException("Method=transfer param=[className={"+config.getClassName()+"}] IllegalAccessException[{"+e.getMessage()+"}]");
			} catch (InstantiationException e) {
				throw new CheckedException("Method=transfer param=[className={"+config.getClassName()+"}]  InstantiationException[{"+e.getMessage()+"}]");
			}

			String[] methods = config.getMethods();
			String[] paramTypes = config.getParamTypes();

			String[] pkClassMethods = config.getPkClassMethods();
			String[] pkClassParamTypes = config.getPkClassParamTypes();
			try {
				for (int j = 0; j < rowData.size(); j++) {

					if (Constants.TYPE_NA.equals(paramTypes[j]))
						continue;

					if (Constants.TYPE_PK.equals(paramTypes[j])) {
						pkObj = setValue(pkObj, methods[j], getMapingClass(pkClassParamTypes[j]),
								getParamValue(pkClassParamTypes[j], rowData.get(j)));
					} else {
						obj = setValue(obj, methods[j], getMapingClass(paramTypes[j]),
								getParamValue(paramTypes[j], rowData.get(j)));
					}

				}

				obj = appendCommonDateField(obj);
				obj = appendOriginalData(obj, (String) dataList.get(i));

				if (pkObj != null) {
					obj = setValue(obj, pkClassMethods[0], pkObj.getClass(), pkObj);
				}
				returnList.add(obj);

			} catch (Exception es) {
				logger.log(Level.SEVERE, "Exception occured while transfer for file name: " + fileName
						+ ", Reason for failure: " + MessageFormatorUtil.getParameterizedStringFromException(es), es);
				errorList = masterSB.addError(errorList,
						"|initial data error|" + es.getMessage() + "|" + (String) dataList.get(i), fileName,
						functionName, 0);
				// throw new CheckedException("Method=setValue Exception
				// ["+es.getMessage()+"]" );
			}
		}
		return returnList;
	}
	
	//Bryan Start
	/**
	 * @param dataList
	 * @param config
	 * @param functionName
	 * @param fileName
	 * @return
	 * @throws CheckedException
	 */
	public List<Object> transferSapDpaCustomer(List<String> dataList, Config config, String functionName, String fileName)
			throws CheckedException {
		List<Object> returnList = new ArrayList<Object>();
		List<String> fields = FunctionUtils.splitLineData((String) dataList.remove(0), Constants.SEPARATOR, true);
		int size = dataList.size();
		List<String> rowData = null;
		for (int i = 0; i < size; i++) {
			rowData = FunctionUtils.splitLineData((String) dataList.get(i), Constants.SEPARATOR, true);
			Object obj = null;
			Object fkObj = null;

			try {
				obj = ResourceLoader.getInstance().getClassInstance(config.getClassName());
				if (config.getFkClass() != null) {
					fkObj = ResourceLoader.getInstance().getClassInstance(config.getFkClass());
				}
			} catch (ClassNotFoundException e) {
				throw new CheckedException("Method=transfer param=[className={"+config.getClassName()+"}}] ClassNotFoundException[{"+e.getMessage()+"}]");
			} catch (IllegalAccessException e) {
				throw new CheckedException("Method=transfer param=[className={"+config.getClassName()+"}] IllegalAccessException[{"+e.getMessage()+"}]");
			} catch (InstantiationException e) {
				throw new CheckedException("Method=transfer param=[className={"+config.getClassName()+"}]  InstantiationException[{"+e.getMessage()+"}]");
			}
			String[] methods = config.getMethods();
			String[] paramTypes = config.getParamTypes();

			String[] fkClassMethods = config.getFkClassMethods();
			String[] fkClassParamTypes = config.getFkClassParamTypes();

			for (int j = 0; j < rowData.size(); j++) {

				String rowDataStr = rowData.get(j);
				
				if (Constants.TYPE_NA.equals(paramTypes[j]))
					continue;
				
				//Check column contain numeric String  setSoldToCustNumber|setShipToCustNumber|setEndCustNumber
				if (("setSoldToCustNumber".endsWith(methods[j]) || "setShipToCustNumber".endsWith(methods[j])
						|| "setEndCustNumber".endsWith(methods[j]) )
						&& rowDataStr != null && StringUtils.isNumeric(rowDataStr)) {
					Integer tempCustNum = Integer.parseInt(rowDataStr);
					rowDataStr = String.valueOf(tempCustNum);
				}
				

				if (Constants.TYPE_FK.equals(paramTypes[j])) {
					fkObj = setValue(fkObj, methods[j], getMapingClass(fkClassParamTypes[0]),
							getParamValue(fkClassParamTypes[0], rowDataStr));

				} else {
					obj = setValue(obj, methods[j], getMapingClass(paramTypes[j]),
							getParamValue(paramTypes[j], rowDataStr));
				}

			}
			
			if (fkObj != null && !((BizUnit) fkObj).getName().equals("")) {
				
				((SapDpaCust) obj).setBizUnit((BizUnit)fkObj);
			}

			// update common fields
			obj = appendCommonDateField(obj);

			returnList.add(obj);
		}
		return returnList;
	}
	//Bryan End

}