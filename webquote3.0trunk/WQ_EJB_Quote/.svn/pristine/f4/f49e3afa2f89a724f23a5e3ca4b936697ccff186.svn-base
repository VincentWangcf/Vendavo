package com.avnet.emasia.webquote.edi.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import com.avnet.emasia.webquote.constants.EdiMesgType;

public class TIExchangeContextFactory {
	private static class TIExchangeContextLoader {
		private static Map<String,TIExchangeContext> MapExContext = GetInitMap();
	}
	//private  static  Map<String,TIExchangeContext> MapExContext = null;
	private static String folderRule = "";
			//DeploymentConfiguration.configPath+File.separator;
			//"tiexchange/";
	
	public static TIExchangeContext GetExchengCtxByKey(String key){
		return GetMapExcontext().get(key);
	}
	
	public static Map<String, TIExchangeContext> GetMapExcontext() {
		return TIExchangeContextLoader.MapExContext;
		/*if(MapExContext == null) {
			synchronized(TIExchangeContextFactory.class) {
				if(MapExContext == null) {
					MapExContext = GetInitMap();
				}
			}			
		}
		return MapExContext;*/
	}
	
	private static Map<String,TIExchangeContext> GetInitMap() {
		Map<String,TIExchangeContext> mapExContext = new HashMap<String,TIExchangeContext>();
		initMapAR1(mapExContext);
		initMapAR2(mapExContext);
		initMapDR1(mapExContext);
		initMapDR2(mapExContext);
		initMap3A1(mapExContext);
		return mapExContext;
	}
	
	private static void initMapAR1(Map<String,TIExchangeContext> mapExContext) {
		TIExchangeContext ctx = new TIExchangeContext();
		ctx.setKey(EdiMesgType.TARONE.getMesgTypeName());
		ctx.setTemplateFileName(folderRule + "3A1(R1).xlsx");
		ctx.setHeaderRowCount(1);//
		InitEntityMapXmlForADR1(ctx.getEntityMapFile());
		ctx.setClassName("com.avnet.emasia.webquote.edi.AMesgRefuse");
		mapExContext.put(ctx.getKey(), ctx);
	}
	private static void initMapAR2(Map<String,TIExchangeContext> mapExContext) {
		TIExchangeContext ctx = new TIExchangeContext();
		ctx.setKey(EdiMesgType.TARTWO.getMesgTypeName());
		ctx.setTemplateFileName(folderRule + "3A1(R2)+5D1(R2).xlsx");
		ctx.setHeaderRowCount(2);
		InitEntityMapXmlForAR2(ctx.getEntityMapFile());
		ctx.setClassName("com.avnet.emasia.webquote.edi.AMesgApprove");
		mapExContext.put(ctx.getKey(), ctx);
		
	}

	private static void initMapDR1(Map<String,TIExchangeContext> mapExContext) {
		TIExchangeContext ctx = new TIExchangeContext();
		ctx.setKey(EdiMesgType.FDRONE.getMesgTypeName());
		ctx.setTemplateFileName(folderRule + "5D1(R1).xlsx");
		ctx.setHeaderRowCount(1);
		InitEntityMapXmlForADR1(ctx.getEntityMapFile());
		ctx.setClassName("com.avnet.emasia.webquote.edi.DMesgRefuse");
		mapExContext.put(ctx.getKey(), ctx);
	}
	private static void initMapDR2(Map<String,TIExchangeContext> mapExContext) {
		TIExchangeContext ctx = new TIExchangeContext();
		ctx.setKey(EdiMesgType.FDRTWO.getMesgTypeName());
		ctx.setTemplateFileName(folderRule + "3A1(R2)+5D1(R2).xlsx");
		ctx.setHeaderRowCount(2);
		InitEntityMapXmlForDR2(ctx.getEntityMapFile());
		ctx.setClassName("com.avnet.emasia.webquote.edi.DMesgApprove");
		mapExContext.put(ctx.getKey(), ctx);
		 
	}
	
	private static void initMap3A1(Map<String,TIExchangeContext> mapExContext) {
		TIExchangeContext ctx = new TIExchangeContext();
		ctx.setKey(EdiMesgType.TAONE.getMesgTypeName());
		ctx.setTemplateFileName(folderRule + "3A1.xlsx");
		ctx.setHeaderRowCount(1);//
		InitEntityMapCsvForADR1(ctx.getEntityMapFile());
		ctx.setClassName("com.avnet.emasia.webquote.edi.ThreeAMesgOne");
		mapExContext.put(ctx.getKey(), ctx);
	}
	
	private static void InitEntityMapXmlForADR1(Map<String, String> map) {
		map.put("poNum","PO_NUM");
		map.put("poItemNum","PO_ITEM_NUM");
		map.put("quoteNum","WQ_NUM");
		map.put("tiQuoteNum","QUOTE_NUM");
		map.put("tiQuoteItemNum","QUOTE_ITEM_NUM");
		map.put("tiPartNum","PART_NUM");
		map.put("mesgType","MESSAGE_TYPE");
		map.put("status","STATUS");
		map.put("reason","STATUS_DESC");
	}
	private static void InitEntityMapXmlForAR2(Map<String, String> map) {
		map.put("poNum","PO_NUM");
		map.put("poItemNum","PO_ITEM_NUM");
		map.put("quoteNum","WQ_NUM");
		map.put("tiQuoteNum","QUOTE_NUM");
		map.put("tiQuoteItemNum","QUOTE_ITEM_NUM");
		map.put("tiPartNum","PART_NUM");
		map.put("startDate","START_DATE");
		map.put("endDate","END_DATE");
		map.put("status","STATUS");
		map.put("statusDesc","STATUS_DESC");
		map.put("qty","QUANTITY");
		map.put("zcpc","ZCPC");
		map.put("zcpcCurr","ZCPC_CURR");
		map.put("offerPrice","OFFER_PRICE");
		map.put("offerPriceCurr","OFFER_PRICE_CURR");
		map.put("resalesPrice","RESALES_PRICE");
		map.put("resalesPriceCurr","RESALES_PRICE_CURR");
	}
	private static void InitEntityMapXmlForDR2(Map<String, String> map) {
		map.put("poNum","PO_NUM");
		map.put("poItemNum","PO_ITEM_NUM");
		map.put("quoteNum","WQ_NUM");
		map.put("tiQuoteNum","QUOTE_NUM");
		map.put("tiQuoteItemNum","QUOTE_ITEM_NUM");
		map.put("tiPartNum","PART_NUM");
		map.put("tiAuthNum","AUTH_NUM");
		//map.put("","MESSAGE_TYPE");
		map.put("tiComment","STATUS_DESC");
		map.put("startDate","START_DATE");
		map.put("endDate","END_DATE");
		map.put("authQty","QUANTITY");
		map.put("offerPricer","OFFER_PRICE");
		map.put("offerPricerCurr","OFFER_PRICE_CURR");
	}
	
	private static void InitEntityMapCsvForADR1(Map<String, String> map) {
		int i = 0;
		map.put("userName",String.valueOf(i++));
		map.put("userEmail",String.valueOf(i++));
		map.put("userTel",String.valueOf(i++));
		map.put("poNum",String.valueOf(i++));
		map.put("poLineItem",String.valueOf(i++));
		map.put("contractQuote",String.valueOf(i++));
		
		map.put("cmName",String.valueOf(i++));
		map.put("cmAvnetCustCode",String.valueOf(i++));
		map.put("tiEcNum",String.valueOf(i++));
		map.put("tiSoldToNum",String.valueOf(i++));
		map.put("cmCity",String.valueOf(i++));
		map.put("cmCountryCode",String.valueOf(i++));
		
		map.put("cmPostalCode",String.valueOf(i++));
		map.put("ucName",String.valueOf(i++));
		map.put("ucAvnetCustNo",String.valueOf(i++));
		map.put("emsiNum",String.valueOf(i++));
		map.put("ucCity",String.valueOf(i++));
		map.put("ucCountryCode",String.valueOf(i++));
		
		map.put("ucPostalCode",String.valueOf(i++));
		map.put("tiPartNum",String.valueOf(i++));
		map.put("pnControlledItem",String.valueOf(i++));
		map.put("competitorName",String.valueOf(i++));
		map.put("competitorMaterial",String.valueOf(i++));
		map.put("competitorCurrency",String.valueOf(i++));
		
		map.put("competitorAmount",String.valueOf(i++));
		map.put("requestedQuantity",String.valueOf(i++));
		map.put("unitMeasurement",String.valueOf(i++));
		map.put("zcprCurrency",String.valueOf(i++));
		map.put("zcprPrice",String.valueOf(i++));
		map.put("zcpcCurrency",String.valueOf(i++));
		
		map.put("zcpcPrice",String.valueOf(i++));
		map.put("comments",String.valueOf(i++));
		
		 
	}
	
	
}
