package com.xinyi.xframe.dubbo_onekey_sample;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.xinyi.xframe.dubbo_onekey_api.declare.MySearchOption.SearchLogic;
import com.xinyi.xframe.dubbo_onekey_sample.service.ElasticSearchService;
import com.xinyi.xinfo.search.bean.SearchAppMeta;
import com.xinyi.xinfo.search.bean.SearchIndexType;


/**
 * Hello world!
 * 
 */
public class ApplicationStart {
	
	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[]{"application-dubbo-search-consumer.xml"});
		context.start();
		ElasticSearchService elasticSearchService = (ElasticSearchService) context.getBean("elasticSearchService");
		try {
			//commonSearch(elasticSearchService, "张三");
			//aliasIndex(elasticSearchService,"etl-mysql","mysql-mysql");
			//simpleSearch(elasticSearchService);
			//simpleSearch2(elasticSearchService);
			//simpleSearch2SimpleSearch(elasticSearchService);
			//complexSearch(elasticSearchService);
			//simpleSearch3SimpleSearch(elasticSearchService);
			//simpleFullTextSearch(elasticSearchService);
			//simpleFullTextSearchWildCardCarID(elasticSearchService);
			//simpleFullTextSearchWildCardAddress(elasticSearchService);
			//simpleFullTextSearch(elasticSearchService);
			//simpleFullTextSearchWildCardAJBH(elasticSearchService);
			//simpleFullTextSearchWildCardCarID(elasticSearchService);
			//simpleFullTextSearchWildCardAddress(elasticSearchService);
			//simpleFullTextSearchWildCardCarID(elasticSearchService);
			//testAggreationFilterQuery(elasticSearchService);
			//testFilterQuery(elasticSearchService);
			//testAggreationFilterQueryCategory(elasticSearchService);
			//simpleSearch3(elasticSearchService);
			//simpleSearch4(elasticSearchService);
			//simpleFullTextSearch(elasticSearchService);
			//simpleFullTextSearch2(elasticSearchService);
			//simpleSearch4(elasticSearchService);
			simpleFullTextMacSearch(elasticSearchService);
			simpleFullTextMacCoutSearch(elasticSearchService);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			context.close();
		}
		
	}
	//通过Mac地址查询
	public static void simpleFullTextMacSearch(ElasticSearchService elasticSearchService){
		
		//String queryString ="粤SS772J";
		String queryString ="宝安";
		SearchIndexType indexType = new SearchIndexType();
		indexType.setIndexName("lgfj-xcloud-wifi");
		//indexType.setTypeName("vehicle2015;vehicle201601;vehicle201602;vehicle201603;wifi201601;face201601");
		//indexType.setTypeName("vehicle201601");
		Map<String,Object> condition = new HashMap<String, Object>();
		condition.put("mac", "90:b6:86:70:eb:06");
		
		List<SearchAppMeta> viewFieldList = new ArrayList<SearchAppMeta>();
		SearchAppMeta searchAppMeta = new SearchAppMeta();
		//searchAppMeta.set
		viewFieldList.add(searchAppMeta);
		
		Map<String,String> conditionTypes = new HashMap<String, String>();
		conditionTypes.put("mac", "1");
		List<Map<String, Object>>  lists = elasticSearchService.getSearchService2()
				.fullTextQuery(true, null, indexType, new String[]{"mac","device_address"}, null, condition, new String[]{"mac"}, conditionTypes, 1, 10);
		System.out.println(lists.size());
		
		for(Map<String,Object> map :lists){
			System.out.println(map.toString());
		}
	}
	
	//通过Mac地址查询
	public static void simpleFullTextMacCoutSearch(ElasticSearchService elasticSearchService){
		
		//String queryString ="粤SS772J";
		String queryString ="宝安";
		SearchIndexType indexType = new SearchIndexType();
		indexType.setIndexName("lgfj-xcloud-wifi");
		//indexType.setTypeName("vehicle2015;vehicle201601;vehicle201602;vehicle201603;wifi201601;face201601");
		//indexType.setTypeName("vehicle201601");
		Map<String,Object> condition = new HashMap<String, Object>();
		condition.put("mac", "90:b6:86:70:eb:06");
		
		List<SearchAppMeta> viewFieldList = new ArrayList<SearchAppMeta>();
		SearchAppMeta searchAppMeta = new SearchAppMeta();
		//searchAppMeta.set
		viewFieldList.add(searchAppMeta);
		
		Map<String,String> conditionTypes = new HashMap<String, String>();
		conditionTypes.put("mac", "1");
		Map<String,Long>  lists = elasticSearchService.getSearchService2()
				.getCount(null, "lgfj-xcloud-wifi", new String[]{"mac","device_address"}, null, condition, conditionTypes);
		System.out.println(lists.size());
		Set<String> keys = lists.keySet();
		for(String key :keys){
			System.out.println("key is :" + key + "  value is :" + lists.get(key));
		}
	}
	
	public static void simpleFullTextSearch(ElasticSearchService elasticSearchService){
		
		//String queryString ="粤SS772J";
		String queryString ="宝安";
		SearchIndexType indexType = new SearchIndexType();
		indexType.setIndexName("vehicleinfo2016-v4");
		//indexType.setTypeName("vehicle2015;vehicle201601;vehicle201602;vehicle201603;wifi201601;face201601");
		//indexType.setTypeName("vehicle201601");
		Map<String,Object> condition = new HashMap<String, Object>();
		//condition.put("slogo", "宝马");
		
		List<SearchAppMeta> viewFieldList = new ArrayList<SearchAppMeta>();
		SearchAppMeta searchAppMeta = new SearchAppMeta();
		//searchAppMeta.set
		viewFieldList.add(searchAppMeta);
		
		Map<String,String> conditionTypes = new HashMap<String, String>();
		//conditionTypes.put("slogo", "1");
		List<Map<String, Object>>  lists = elasticSearchService.getSearchService2()
				.fullTextQuery(true, null, indexType, new String[]{"car_id","bayonet_type_name"}, null, condition, new String[]{"bayonet_type_name"}, conditionTypes, 1, 10);
		System.out.println(lists.size());
		
		for(Map<String,Object> map :lists){
			System.out.println(map.toString());
		}
	}
	
	
	
	
	
	public static void simpleFullTextSearch2(ElasticSearchService elasticSearchService) throws ParseException{
		
		String queryString ="*";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		SearchIndexType indexType = new SearchIndexType();
		indexType.setIndexName("vehicleinfo2016-v4");
		//indexType.setTypeName("vehicle2015;vehicle201601;vehicle201602;vehicle201603;wifi201601;face201601");
		//indexType.setTypeName("vehicle201601");
		Map<String,Object> condition = new HashMap<String, Object>();
		condition.put("carplate_color", "蓝*");
		condition.put("record_from", new String[] { "1" });
//		condition.put("bayonet_type_id", "0,181"); //油站
		//condition.put("bayonet_type_id", "181,1000");//卡口
//		 Date[] dateParams = new Date[]{sdf.parse("2016-01-01 00:00:00"),sdf.parse("2016-01-30 00:00:00")};
//		condition.put("passing_time", dateParams); //时间段查询
		
		List<SearchAppMeta> viewFieldList = new ArrayList<SearchAppMeta>();
		SearchAppMeta searchAppMeta = new SearchAppMeta();
		//searchAppMeta.set
		viewFieldList.add(searchAppMeta);
		
		Map<String,String> conditionTypes = new HashMap<String, String>();
		conditionTypes.put("carplate_color", "0");
	    conditionTypes.put("record_from", "4");
//	    conditionTypes.put("bayonet_type_id", "3");
//	    conditionTypes.put("passing_time", "2");
	    
		List<Map<String, Object>>  lists = elasticSearchService.getSearchService2()
				.fullTextQuery(true, "*", indexType, new String[]{"carplate_color","bayonet_type_name"}, null, condition, 
						new String[]{"carplate_color","bayonet_type_name"}, conditionTypes, 1, 10,"_type","desc");
		System.out.println(lists.size());
		
		for(Map<String,Object> map :lists){
			System.out.println(map.toString());
		}
	}
	
	/**
	 * common search test, test search thing
	 * @param elasticSearchService
	 * @param queryString
	 */
//	public static void commonSearch(ElasticSearchService elasticSearchService,String queryString){
//		
//		String resultStr = elasticSearchService.getSearchService().commonSearch(queryString);
//		System.out.println(resultStr);
//	}
//	
//	
//	public static void aliasIndex(ElasticSearchService elasticSearchService,String srcIndex,String targetIndex){
//		
//		String result = elasticSearchService.getIndexService().aliasIndexName(srcIndex, targetIndex);
//		System.out.println("alias the index name, the result is :" + result);
//	}
	
	public static void simpleSearch(ElasticSearchService elasticSearchService){
		
		String[] indexes = new String[]{"pjaxx"};
		
		HashMap<String, Object[]> searchContentMaps = new HashMap<String, Object[]>();
		Object[] object = new Object[]{"张三"};
		searchContentMaps.put("PA_PAJG", object);
		
		List<Map<String, Object>>  lists = elasticSearchService.getSearchService2()
				.simpleSearch(true,indexes,searchContentMaps,null,1,10,null,null);
		System.out.println(lists.size());
		
		for(Map<String,Object> map :lists){
			System.out.println(map.toString());
		}
	}
	
	public static void simpleSearch2(ElasticSearchService elasticSearchService){
		
		String[] indexes = new String[]{"lgfj-xcloud"};
		
		HashMap<String, Object[]> searchContentMaps = new HashMap<String, Object[]>();
		Object[] object = new Object[]{"宝安"};
		searchContentMaps.put("bayonet_type_name", object);
		
		List<Map<String, Object>>  lists = elasticSearchService.getSearchService2()
				.simpleSearch(true,indexes,searchContentMaps,null,1,10,null,null);
		System.out.println(lists.size());
		
		for(Map<String,Object> map :lists){
			System.out.println(map.toString());
		}
	}
	
	/**
	 * 根据 xdata_id 查看记录详情
	 * @param elasticSearchService
	 */
	public static void simpleSearch3(ElasticSearchService elasticSearchService){
		
		//String queryString ="粤SS772J";
		String queryString = null;
		SearchIndexType indexType = new SearchIndexType();
		indexType.setIndexName("vehicleinfo2016-v2");

		Map<String,Object> condition = new HashMap<String, Object>();
		condition.put("xdata_id", "b884966bf6474693b1a489af5b96a03a");
		
		List<SearchAppMeta> viewFieldList = new ArrayList<SearchAppMeta>();
		SearchAppMeta searchAppMeta = new SearchAppMeta();
		//searchAppMeta.set
		viewFieldList.add(searchAppMeta);
		
		Map<String,String> conditionTypes = new HashMap<String, String>();
		conditionTypes.put("xdata_id", "1");
	
		List<Map<String,Object>>  lists = elasticSearchService.getSearchService2()
				.fullTextQueryFilter(true, indexType,condition,
						conditionTypes, 1,10);
		for(Map<String,Object> map :lists){
			System.out.println(map.toString());
		}	
	}
	
	/**
	 * 根据 _id 查看记录详情
	 * @param elasticSearchService
	 */
	public static void simpleSearch4(ElasticSearchService elasticSearchService){
		
		//String queryString ="粤SS772J";
		String queryString = null;
		SearchIndexType indexType = new SearchIndexType();
		indexType.setIndexName("vehicleinfo2016-v2");

		Map<String,Object> condition = new HashMap<String, Object>();
		condition.put("_id", "YYJLsHT2TT6yWnvIWUO4CA");
		
		List<SearchAppMeta> viewFieldList = new ArrayList<SearchAppMeta>();
		SearchAppMeta searchAppMeta = new SearchAppMeta();
		//searchAppMeta.set
		viewFieldList.add(searchAppMeta);
		
		Map<String,String> conditionTypes = new HashMap<String, String>();
		conditionTypes.put("_id", "1");
	
		List<Map<String,Object>>  lists = elasticSearchService.getSearchService2()
				.fullTextQueryFilter(true, indexType,condition,
						conditionTypes, 1,10);
		for(Map<String,Object> map :lists){
			System.out.println(map.toString());
		}	
	}
	
	/**
	 * 搜索建议，功能测试接口,针对车牌
	 * @param elasticSearchService
	 */
	public static void simpleFullTextSearchWildCardCarID(ElasticSearchService elasticSearchService){
		
		//String queryString ="粤SS772J";
		String queryString = null;
		SearchIndexType indexType = new SearchIndexType();
		indexType.setIndexName("vehicleinfo2016-v2;lgfj-xcloud-face;lgfj-xcloud-police");
		//indexType.setTypeName("vehicle2015;vehicle201601;vehicle201602;vehicle201603;wifi201601;face201601");
		//indexType.setTypeName("vehicle201601");
		Map<String,Object> condition = new HashMap<String, Object>();
		//condition.put("car_id", "粤B1*");
		condition.put("car_id", "粤B21*");
		condition.put("car_type", "粤B21*");
		condition.put("carplate_color", "粤B21*");
		
		List<SearchAppMeta> viewFieldList = new ArrayList<SearchAppMeta>();
		SearchAppMeta searchAppMeta = new SearchAppMeta();
		//searchAppMeta.set
		viewFieldList.add(searchAppMeta);
		
		Map<String,String> conditionTypes = new HashMap<String, String>();
		conditionTypes.put("car_id", "0");
		conditionTypes.put("car_type", "0");
		conditionTypes.put("carplate_color", "0");
		List<Map<String, Object>>  lists = elasticSearchService.getSearchService2()
				.wildCardFilteredSuggest(true,indexType,condition,conditionTypes, 1, 10);
				
		
		System.out.println(lists.size());
		
		for(Map<String,Object> map :lists){
			System.out.println(map.toString());
		}
	}
	
	
	/**
	 * 搜索建议，功能测试接口,针对设备地址
	 * @param elasticSearchService
	 */
	public static void simpleFullTextSearchWildCardAddress(ElasticSearchService elasticSearchService){
		
		//String queryString ="粤SS772J";
		String queryString = null;
		SearchIndexType indexType = new SearchIndexType();
		indexType.setIndexName("vehicleinfo2016-v2;lgfj-xcloud-word;lgfj-xcloud-face;lgfj-xcloud-police");
		//indexType.setTypeName("vehicle2015;vehicle201601;vehicle201602;vehicle201603;wifi201601;face201601");
		//indexType.setTypeName("vehicle201601");
		Map<String,Object> condition = new HashMap<String, Object>();
		//condition.put("car_id", "粤B1*");
		condition.put("call_word", "中石化*");
		condition.put("car_type", "中石化*");
		condition.put("carplate_color", "中石化*");
		condition.put("car_id", "中石化*");
		
		List<SearchAppMeta> viewFieldList = new ArrayList<SearchAppMeta>();
		SearchAppMeta searchAppMeta = new SearchAppMeta();
		//searchAppMeta.set
		viewFieldList.add(searchAppMeta);
		
		Map<String,String> conditionTypes = new HashMap<String, String>();
		conditionTypes.put("car_id", "0");
		conditionTypes.put("car_type", "0");
		conditionTypes.put("carplate_color", "0");
		conditionTypes.put("call_word", "0");
		List<Map<String, Object>>  lists = elasticSearchService.getSearchService2()
				.wildCardFilteredSuggest(true,indexType,condition,conditionTypes, 1, 10);
				
		
		System.out.println(lists.size());
		
		for(Map<String,Object> map :lists){
			System.out.println(map.toString());
		}
	}
	
	/**
	 * 卡口油站  过车统计(车辆经过每个卡口的次数统计)
	 * @param elasticSearchService
	 */
	public static void testAggreationFilterQuery(ElasticSearchService elasticSearchService){
		
		//String queryString ="粤SS772J";
		String queryString = null;
		SearchIndexType indexType = new SearchIndexType();
		indexType.setIndexName("vehicleinfo2016-v2");

		Map<String,Object> condition = new HashMap<String, Object>();
		condition.put("car_id", "粤B1J339");
		condition.put("record_from", "1"); //海康
		//condition.put("record_from", "0"); //高清
		//condition.put("bayonet_type_id", "0,181");  //海康 ->油站
		condition.put("bayonet_type_id", "182,500");  //海康 ->卡口
		
		List<SearchAppMeta> viewFieldList = new ArrayList<SearchAppMeta>();
		SearchAppMeta searchAppMeta = new SearchAppMeta();
		//searchAppMeta.set
		viewFieldList.add(searchAppMeta);
		
		Map<String,String> conditionTypes = new HashMap<String, String>();
		conditionTypes.put("car_id", "1");
		conditionTypes.put("record_from", "1");
		conditionTypes.put("bayonet_type_id", "3");
		
		Map<String,Object> aggreationTypes = new HashMap<String, Object>();
		//field and fileds
		aggreationTypes.put("bayonet_type_id", "bayonet_type_ids");
		List<Map<String,Object>>  lists = elasticSearchService.getSearchService2()
				.aggreationFilterQuery(true,indexType,condition,
						conditionTypes,aggreationTypes, 1, 10);
		for(Map<String,Object> map :lists){
			System.out.println(map.toString());
		}	
	}
	
	/**
	 * 卡口油站  过车统计(车辆经过每个卡口的次数统计) --根据卡口名称进行分类
	 * @param elasticSearchService
	 */
	public static void testAggreationFilterQueryCategory(ElasticSearchService elasticSearchService){
		
		//String queryString ="粤SS772J";
		String queryString = null;
		SearchIndexType indexType = new SearchIndexType();
		indexType.setIndexName("vehicleinfo2016-v2");

		Map<String,Object> condition = new HashMap<String, Object>();
		condition.put("car_id", "粤B1J339");
		
		List<SearchAppMeta> viewFieldList = new ArrayList<SearchAppMeta>();
		SearchAppMeta searchAppMeta = new SearchAppMeta();
		//searchAppMeta.set
		viewFieldList.add(searchAppMeta);
		
		Map<String,String> conditionTypes = new HashMap<String, String>();
		conditionTypes.put("car_id", "1");
		
		Map<String,Object> aggreationTypes = new HashMap<String, Object>();
		//field and fileds
		aggreationTypes.put("bayonet_type_name", "bayonet_type_names");
		List<Map<String,Object>>  lists = elasticSearchService.getSearchService2()
				.aggreationFilterQuery(true,indexType,condition,
						conditionTypes,aggreationTypes, 1, 10);
		for(Map<String,Object> map :lists){
			System.out.println(map.toString());
		}	
	}
	
	/**
	 * 根据卡口编号，获得卡口的详细信息
	 * @param elasticSearchService
	 */
	public static void testFilterQuery(ElasticSearchService elasticSearchService){
		
		//String queryString ="粤SS772J";
		String queryString = null;
		SearchIndexType indexType = new SearchIndexType();
		indexType.setIndexName("vehicleinfo2016-v2");

		Map<String,Object> condition = new HashMap<String, Object>();
		condition.put("bayonet_type_id", "440307682056");
		condition.put("car_id", "粤B713HP");
		
		
		List<SearchAppMeta> viewFieldList = new ArrayList<SearchAppMeta>();
		SearchAppMeta searchAppMeta = new SearchAppMeta();
		//searchAppMeta.set
		viewFieldList.add(searchAppMeta);
		
		Map<String,String> conditionTypes = new HashMap<String, String>();
		conditionTypes.put("bayonet_type_id", "1");
		conditionTypes.put("car_id", "1");
	
		List<Map<String,Object>>  lists = elasticSearchService.getSearchService2()
				.fullTextQueryFilter(true, indexType,condition,
						conditionTypes, 1,10);
		for(Map<String,Object> map :lists){
			System.out.println(map.toString());
		}	
	}
	
	/**
	 * 搜索建议，功能测试接口,针对案件编号
	 * @param elasticSearchService
	 */
	public static void simpleFullTextSearchWildCardAJBH(ElasticSearchService elasticSearchService){
		
		//String queryString ="粤SS772J";
		String queryString = null;
		SearchIndexType indexType = new SearchIndexType();
		indexType.setIndexName("vehicleinfo2016-v2;lgfj-xcloud-word;lgfj-xcloud-face;lgfj-xcloud-police;lgfj-xcloud-alertsituation");
		//indexType.setTypeName("vehicle2015;vehicle201601;vehicle201602;vehicle201603;wifi201601;face201601");
		//indexType.setTypeName("vehicle201601");
		Map<String,Object> condition = new HashMap<String, Object>();
		//condition.put("car_id", "粤B1*");
		condition.put("ajbh", "J440*");
		condition.put("car_type", "J440*");
		condition.put("carplate_color", "J440*");
		condition.put("car_id", "J440*");
		
		List<SearchAppMeta> viewFieldList = new ArrayList<SearchAppMeta>();
		SearchAppMeta searchAppMeta = new SearchAppMeta();
		//searchAppMeta.set
		viewFieldList.add(searchAppMeta);
		
		Map<String,String> conditionTypes = new HashMap<String, String>();
		conditionTypes.put("car_id", "0");
		conditionTypes.put("car_type", "0");
		conditionTypes.put("carplate_color", "0");
		conditionTypes.put("ajbh", "0");
		List<Map<String, Object>>  lists = elasticSearchService.getSearchService2()
				.wildCardFilteredSuggest(true,indexType,condition,conditionTypes, 1, 10);
				
		
		System.out.println(lists.size());
		
		for(Map<String,Object> map :lists){
			System.out.println(map.toString());
		}
	}
	
	/**
	 * 
	 * @param elasticSearchService
	 */
	public static void simpleSearch2SimpleSearch(ElasticSearchService elasticSearchService){
		
		String[] indexes = new String[]{"lgfj-xcloud"};
		
		HashMap<String, Object[]> searchContentMaps = new HashMap<String, Object[]>();
		Object[] object1 = new Object[]{"宝安"};
		searchContentMaps.put("bayonet_type_name", object1);
		
		Object[] object2 = new Object[]{"粤BL01G5"};
		searchContentMaps.put("car_id",object2 );
		
		
		Object[] object3 = new Object[]{"龙新所"};
		searchContentMaps.put("bayonet_type_id",object3 );

		
		List<Map<String, Object>>  lists = elasticSearchService.getSearchService2()
				.simpleSearch(true,indexes,searchContentMaps, SearchLogic.must
			            , null,null
			            , 0, 10, null, null);
		System.out.println(lists.size());
		
		for(Map<String,Object> map :lists){
			System.out.println(map.toString());
		}
	}
	
	
	public static void simpleSearch3SimpleSearch(ElasticSearchService elasticSearchService){
		
		//String queryString ="粤SS772J";
		String queryString ="宝安大道";
		SearchIndexType indexType = new SearchIndexType();
		indexType.setIndexName("lgfj-xcloud-vehicle;lgfj-xcloud-face;lgfj-xcloud-wifi");
		//indexType.setTypeName("vehicle2015;vehicle201601;vehicle201602;vehicle201603;wifi201601;face201601");
		//indexType.setTypeName("vehicle201601");
		Map<String,Object> condition = null;// new HashMap<String, Object>();
		//condition.put("vehicle_type", 0);
		
		List<SearchAppMeta> viewFieldList =null;// new ArrayList<SearchAppMeta>();
		SearchAppMeta searchAppMeta = new SearchAppMeta();
		//searchAppMeta.set
		//viewFieldList.add(searchAppMeta);
		
		Map<String,String> conditionTypes = null;// new HashMap<String, String>();
		//conditionTypes.put("vehicle_type", "1");
		
		List<Map<String, Object>>  lists = elasticSearchService.getSearchService2()
				 .simpleSearch(true,queryString,indexType,condition,viewFieldList,conditionTypes,1,10);
		System.out.println(lists.size());
		
		for(Map<String,Object> map :lists){
			System.out.println(map.toString());
		}
	}
	
	 public static void complexSearch(ElasticSearchService elasticSearchService){
		
		 HashMap<String,String> mapParam = new HashMap<String, String>();
	    	mapParam.put("cluster.name", "elasticsearch");
	    	String[] indexes = new String[]{"zyxx_1"};
	    	HashMap<String, Object[]> mustSearchContentMaps = new HashMap<String, Object[]>();
			Object[] mustObject = new Object[]{"王伟伟"};
			mustSearchContentMaps.put("USER_NAME", mustObject);
	    	HashMap<String, Object[]> shouldSearchContentMaps = new HashMap<String, Object[]>();
			Object[] shouldObject = new Object[]{"104"};
			//两者之间是 or 的关系
			shouldSearchContentMaps.put("PA_PAFF", shouldObject);
			shouldSearchContentMaps.put("PA_ZADJ", new Object[]{"102"});
			List<Map<String, Object>>  lists = elasticSearchService.getSearchService2().complexSearch(indexes,mustSearchContentMaps,shouldSearchContentMaps,1,10,null,null);
			System.out.println("/////////////////////////" +lists.size());
			for(Map<String, Object> map : lists){
				System.out.println(map.toString());
			}
	 }
	
//	List<Map<String, Object>> complexSearch(String[] indexNames,
//			@Nullable HashMap<String, Object[]> mustSearchContentMap,
//			@Nullable HashMap<String, Object[]> shouldSearchContentMap,
//			int from, int offset, @Nullable String sortField,
//			@Nullable String sortType);
}
