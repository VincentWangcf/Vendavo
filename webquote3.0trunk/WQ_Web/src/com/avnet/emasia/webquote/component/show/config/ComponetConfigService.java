package com.avnet.emasia.webquote.component.show.config;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.*;

import java.util.Arrays;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.avnet.emasia.webquote.entity.LeafLogicCalConfig.ConditionType;
import com.avnet.emasia.webquote.entity.PageComponentItem;
import com.avnet.emasia.webquote.entity.Role;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.utilites.web.common.CacheUtil;

@ManagedBean
@ApplicationScoped
public class ComponetConfigService {
	static final Logger logger = Logger.getLogger(ComponetConfigService.class.getName());
	//@EJB
	private static CacheUtil cacheUtil;
	static {
		try {
			Context context = new InitialContext();
			cacheUtil = ((CacheUtil) context.lookup("java:app/WQ_Web/CacheUtil!com.avnet.emasia.webquote.utilites.web.common.CacheUtil"));
		} catch (NamingException e) {
			logger.log(Level.SEVERE, "This shoud not happen" , e);
		}			
		
	}
	private static volatile Map<String, List<PageComponentItem>> CONFIGMAP = getMap();
	private static final String SPECIALSTR = "&$";
	
	private static Map<String, Boolean> RESULTMAP = new ConcurrentHashMap<String, Boolean>();
	
	public static synchronized void refresh() {
		CONFIGMAP = getMap();
		RESULTMAP.clear();
	}

	private static Map<String, List<PageComponentItem>> getMap() {
		List<PageComponentItem> list = cacheUtil.getPageComponents();
		Map<String, List<PageComponentItem>> map = list.stream().collect(groupingBy(c -> c.getPageName().toUpperCase() + SPECIALSTR + c.getComponetId().toUpperCase()));
		/*//sort list of every value in map;
		map.values().forEach(v -> v.sort(Comparator.comparing(c -> c.getPriority())));*/
		return map;
	}

	public static boolean show(String pageName, String componentID, User user) {
		if(pageName == null || componentID==null || user==null) return true;
		refresh();
		Boolean isShow = null;
		Set<String> bs = user.getAllBizUnits();
		List<Role> rs = user.getRoles();
		isShow = show(pageName, componentID, bs.stream().collect(toList()),
				rs.stream().map(p -> p.getName()).collect(toList()));
		return isShow;
	}

	public static boolean show(String pageName, String componentID, List<String> regions, List<String> roles) {
		
		EnumMap<ConditionType,Collection<String>> mapParam = new EnumMap<ConditionType,Collection<String>>(ConditionType.class);
		mapParam.put(ConditionType.REGION, regions);
		mapParam.put(ConditionType.ROLE, roles);
		return show(pageName, componentID, mapParam);
	}
	
	private static boolean show(String pageName, String componentID, EnumMap<ConditionType,Collection<String>> mapParam) {
		if(pageName == null || componentID==null) return true;
		List<PageComponentItem> list = getFromMap(pageName, componentID);
		boolean isShow = true;
		if(list.isEmpty()) {
			isShow = true;
		} else {
			if(list.size() > 1) {
				list = list.stream()
						.sorted(Comparator.comparing(c -> c.getPriority())).collect(toList());
			}
			Optional<PageComponentItem> result = list.stream().filter(c -> c.isConditionMatch(mapParam))
					.findFirst();
			isShow = result.isPresent() ? result.get().isShow() : true;
			if(result.isPresent()) {
				logger.config(()-> "CONFIG MATCHED ID IS ::" + result.get().getId());
			}
		}
		return isShow;
	}
	
	public static boolean showByRegion(String pageName, String componentID,List<String> regions) {
		return show(pageName, componentID, regions, Collections.emptyList());
	}
	
	public static boolean showByRegion(String pageName, String componentID, String region) {
		return show(pageName, componentID, Arrays.asList(region) , Collections.emptyList());
	}
	
	public static boolean showByRole(String pageName, String componentID, List<String> roles) {
		return show(pageName, componentID, Collections.emptyList(), roles);
	}
	
	/*public static boolean showByRegion(String pageName, String componentID, String... regions) {
		if(regions == null) {
			logger.warning("regions is null, this should not occure." );
			return true;
		}
		return show(pageName,componentID,Arrays.asList(regions),Collections.EMPTY_LIST);
	}
	
	public static boolean showByRegion(String pageName, String componentID, String... regions) {
		if(regions == null) {
			logger.warning("regions is null, this should not occure." );
			return true;
		}
		return show(pageName,componentID,Arrays.asList(regions),Collections.EMPTY_LIST);
	}*/
	
	private static List<PageComponentItem> getFromMap(String pageName, String componentID) {
		//if(pageName == null || componentID==null) return null;
		 List<PageComponentItem> list = CONFIGMAP.get(pageName.toUpperCase() + SPECIALSTR + componentID.toUpperCase());
		 if(list == null) {
			 list = Collections.emptyList();
		 }  
		 return list;
	}
}