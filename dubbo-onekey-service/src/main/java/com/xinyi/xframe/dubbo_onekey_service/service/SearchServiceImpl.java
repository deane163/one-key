/**
 * 
 */
package com.xinyi.xframe.dubbo_onekey_service.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import javax.validation.constraints.NotNull;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
//import org.elasticsearch.client.action.suggest.SuggestRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ExistsFilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.NotFilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryFilterBuilder;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.facet.terms.TermsFacet;
import org.elasticsearch.search.facet.terms.TermsFacetBuilder;
import org.elasticsearch.search.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.xinyi.xframe.dubbo_onekey_api.declare.MySearchOption;
import com.xinyi.xframe.dubbo_onekey_api.declare.MySearchOption.DataFilter;
import com.xinyi.xframe.dubbo_onekey_api.declare.MySearchOption.SearchLogic;
import com.xinyi.xframe.dubbo_onekey_api.service.SearchService;
import com.xinyi.xframe.dubbo_onekey_service.client.RedUserInfoService;
import com.xinyi.xframe.dubbo_onekey_service.utils.Constant;
import com.xinyi.xframe.dubbo_onekey_service.utils.DateUtils;
import com.xinyi.xframe.dubbo_onekey_service.utils.EsProperties;
import com.xinyi.xinfo.search.bean.AttentionContent;
import com.xinyi.xinfo.search.bean.SearchAppMeta;
import com.xinyi.xinfo.search.bean.SearchIndexType;


/**
 * 功能说明：搜索实现定义
 * 
 * SearchServiceImp.java
 * 
 * Original Author: liangliang.jia,2015年7月28日上午10:46:32
 * 
 * Copyright (C)1997-2015 深圳小树盛凯科技 All rights reserved.
 */
@Service(version="3.0.0", protocol="dubbo",timeout=60000)
public class SearchServiceImpl implements SearchService, InitializingBean,
		DisposableBean {

	//private static final String IK = "ik";

	private List<String> clusterList = null;
    private Logger logger = Logger.getLogger(SearchServiceImpl.class);
    private Client searchClient = null;
    private HashMap<String, String> searchClientConfigureMap = null;
    private String highlightCSS = "em,em";
    
    @Autowired
	private EsProperties esProperties;
    
    @Autowired
    private RedUserInfoService redUserInfoService;
    
    @Value("${application.message.format.date.column}")
    private String formatDateColumn;
    
    private List<String> formatDateColumns;
    
    public void setHighlightCSS(String highlightCSS) {
        this.highlightCSS = highlightCSS;
    }

    public void setSearchClientConfigureMap(HashMap<String, String> searchClientConfigureMap) {
        this.searchClientConfigureMap = searchClientConfigureMap;
    }

    private boolean _bulkInsertData(String indexName, XContentBuilder xContentBuilder) {
        try {
            BulkRequestBuilder bulkRequest = this.searchClient.prepareBulk();
            bulkRequest.add(this.searchClient.prepareIndex(indexName, indexName).setSource(xContentBuilder));
            BulkResponse bulkResponse = bulkRequest.execute().actionGet();
            if (!bulkResponse.hasFailures()) {
                return true;
            }
            else {
                this.logger.error(bulkResponse.buildFailureMessage());
            }
        }
        catch (Exception e) {
            this.logger.error(e.getMessage());
        }
        return false;
    }

    public void afterPropertiesSet() throws Exception {
        this.logger.info("Connect the ElasticSearch Server ...");
        this.open();
    }

    public boolean bulkDeleteData(String indexName, HashMap<String, Object[]> contentMap) {
        try {
            QueryBuilder queryBuilder = null;
            queryBuilder = this.createQueryBuilder(contentMap, SearchLogic.must);
            this.logger.warn("[" + indexName + "]" + queryBuilder.toString());
            this.searchClient.prepareDeleteByQuery(indexName).setQuery(queryBuilder).execute().actionGet();
            return true;
        }
        catch (Exception e) {
            this.logger.error(e.getMessage());
        }
        return false;
    }

    public boolean bulkInsertData(String indexName, HashMap<String, Object[]> insertContentMap) {
        XContentBuilder xContentBuilder = null;
        try {
            xContentBuilder = XContentFactory.jsonBuilder().startObject();
        }
        catch (IOException e) {
            this.logger.error(e.getMessage());
            return false;
        }
        Iterator<Entry<String, Object[]>> iterator = insertContentMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, Object[]> entry = iterator.next();
            String field = entry.getKey();
            Object[] values = entry.getValue();
            String formatValue = this.formatInsertData(values);
            try {
                xContentBuilder = xContentBuilder.field(field, formatValue);
            }
            catch (IOException e) {
                this.logger.error(e.getMessage());
                return false;
            }
        }
        try {
            xContentBuilder = xContentBuilder.endObject();
        }
        catch (IOException e) {
            this.logger.error(e.getMessage());
            return false;
        }
        try {
            this.logger.debug("[" + indexName + "]" + xContentBuilder.string());
        }
        catch (IOException e) {
            this.logger.error(e.getMessage());
        }
        return this._bulkInsertData(indexName, xContentBuilder);
    }

    public boolean bulkUpdateData(String indexName, HashMap<String, Object[]> oldContentMap, HashMap<String, Object[]> newContentMap) {
        if (this.bulkDeleteData(indexName, oldContentMap)) {
            return this.bulkInsertData(indexName, newContentMap);
        }
        this.logger.warn("删除数据失败");
        return false;
    }

    public boolean autoBulkUpdateData(String indexName, HashMap<String, Object[]> oldContentMap, HashMap<String, Object[]> newContentMap) {
        try {
            List<Map<String, Object>> searchResult = this.simpleSearch(true,new String[] { indexName }, oldContentMap, null, 0, 1, null, null);
            if (searchResult == null || searchResult.size() == 0) {
                this.logger.warn("未找到需要更新的数据");
                return false;
            }
            if (!this.bulkDeleteData(indexName, oldContentMap)) {
                this.logger.warn("删除数据失败");
                return false;
            }
            HashMap<String, Object[]> insertContentMap = new HashMap<String, Object[]>();
            for (Map<String, Object> contentMap : searchResult) {
                Iterator<Entry<String, Object>> oldContentIterator = contentMap.entrySet().iterator();
                while (oldContentIterator.hasNext()) {
                    Entry<String, Object> entry = oldContentIterator.next();
                    insertContentMap.put(entry.getKey(), new Object[] { entry.getValue() });
                }
            }
            Iterator<Entry<String, Object[]>> newContentIterator = newContentMap.entrySet().iterator();
            while (newContentIterator.hasNext()) {
                Entry<String, Object[]> entry = newContentIterator.next();
                insertContentMap.put(entry.getKey(), entry.getValue());
            }
            return this.bulkInsertData(indexName, insertContentMap);
        }
        catch (Exception e) {
            this.logger.error(e.getMessage());
        }
        return false;
    }

    /*简单的值校验*/
    private boolean checkValue(Object[] values) {
        if (values == null) {
            return false;
        }
        else if (values.length == 0) {
            return false;
        }
        else if (values[0] == null) {
            return false;
        }
        else if (values[0].toString().trim().isEmpty()) {
            return false;
        }
        return true;
    }

    private void close() {
        if (this.searchClient == null) {
            return;
        }
        this.searchClient.close();
        this.searchClient = null;
    }

    private RangeQueryBuilder createRangeQueryBuilder(String field, Object[] values) {
        if (values.length == 1 || values[1] == null || values[1].toString().trim().isEmpty()) {
            this.logger.warn("[区间搜索]必须传递两个值，但是只传递了一个值，所以返回null");
            return null;
        }
        boolean timeType = false;
        if (MySearchOption.isDate(values[0])) {
            if (MySearchOption.isDate(values[1])) {
                timeType = true;
            }
        }
        String begin = "", end = "";
        if (timeType) {
            /*
             * 如果时间类型的区间搜索出现问题，有可能是数据类型导致的：
             *     （1）在监控页面（elasticsearch-head）中进行range搜索，看看什么结果，如果也搜索不出来，则：
             *     （2）请确定mapping中是date类型，格式化格式是yyyy-MM-dd HH:mm:ss
             *    （3）请确定索引里的值是类似2012-01-01 00:00:00的格式
             *    （4）如果是从数据库导出的数据，请确定数据库字段是char或者varchar类型，而不是date类型（此类型可能会有问题）
             * */
            begin = MySearchOption.formatDate(values[0]);
            end = MySearchOption.formatDate(values[1]);
        }
        else {
            begin = values[0].toString();
            end = values[1].toString();
        }
        return QueryBuilders.rangeQuery(field).from(begin).to(end);
    }

    /*
     * 创建过滤条件
     * */
    private QueryBuilder createFilterBuilder(SearchLogic searchLogic, QueryBuilder queryBuilder, HashMap<String, Object[]> searchContentMap, HashMap<String, Object[]> filterContentMap) throws Exception
    {
        try {
            Iterator<Entry<String, Object[]>> iterator = searchContentMap.entrySet().iterator();
            AndFilterBuilder andFilterBuilder = null;
            while (iterator.hasNext()) {
                Entry<String, Object[]> entry = iterator.next();
                Object[] values = entry.getValue();
                /*排除非法的搜索值*/
                if (!this.checkValue(values)) {
                    continue;
                }
                MySearchOption mySearchOption = this.getSearchOption(values);
                if (mySearchOption.getDataFilter() == DataFilter.exists) {
                    /*被搜索的条件必须有值*/
                    ExistsFilterBuilder existsFilterBuilder = FilterBuilders.existsFilter(entry.getKey());
                    if (andFilterBuilder == null) {
                        andFilterBuilder = FilterBuilders.andFilter(existsFilterBuilder);
                    }
                    else {
                        andFilterBuilder = andFilterBuilder.add(existsFilterBuilder);
                    }
                }
            }
            if (filterContentMap == null || filterContentMap.isEmpty()) {
                /*如果没有其它过滤条件，返回*/
                return QueryBuilders.filteredQuery(queryBuilder, andFilterBuilder);
            }
            /*构造过滤条件*/
            QueryFilterBuilder queryFilterBuilder = FilterBuilders.queryFilter(this.createQueryBuilder(filterContentMap, searchLogic));
            /*构造not过滤条件，表示搜索结果不包含这些内容，而不是不过滤*/
            NotFilterBuilder notFilterBuilder = FilterBuilders.notFilter(queryFilterBuilder);
            return QueryBuilders.filteredQuery(queryBuilder, FilterBuilders.andFilter(andFilterBuilder, notFilterBuilder));
        }
        catch (Exception e) {
            this.logger.error(e.getMessage());
        }
        return null;
    }

    private QueryBuilder createSingleFieldQueryBuilder(String field, Object[] values, MySearchOption mySearchOption) {
        try {
            if (mySearchOption.getSearchType() == MySearchOption.SearchType.range) {
                /*区间搜索*/
                return this.createRangeQueryBuilder(field, values);
            }
            //        String[] fieldArray = field.split(",");/*暂时不处理多字段[field1,field2,......]搜索情况*/
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            for (Object valueItem : values) {
                if (valueItem instanceof MySearchOption) {
                    continue;
                }
                QueryBuilder queryBuilder = null;
                String formatValue = valueItem.toString().trim().replace("*", "");//格式化搜索数据
                if (mySearchOption.getSearchType() == MySearchOption.SearchType.term) {
                    queryBuilder = QueryBuilders.termQuery(field, formatValue).boost(mySearchOption.getBoost());
                }
                else if (mySearchOption.getSearchType() == MySearchOption.SearchType.querystring) {
                    if (formatValue.length() == 1) {
                        /*如果搜索长度为1的非数字的字符串，格式化为通配符搜索，暂时这样，以后有时间改成multifield搜索，就不需要通配符了*/
                        if (!Pattern.matches("[0-9]", formatValue)) {
                            formatValue = "*"+formatValue+"*";
                        }
                    }
                    QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders.queryString(formatValue).minimumShouldMatch(mySearchOption.getQueryStringPrecision());
                    queryBuilder = queryStringQueryBuilder.field(field).boost(mySearchOption.getBoost());
                }
                if (mySearchOption.getSearchLogic() == SearchLogic.should) {
                    boolQueryBuilder = boolQueryBuilder.should(queryBuilder);
                }
                else {
                    boolQueryBuilder = boolQueryBuilder.must(queryBuilder);
                }
            }
            return boolQueryBuilder;
        }
        catch (Exception e) {
            this.logger.error(e.getMessage());
        }
        return null;
    }

    /*
     * 创建搜索条件，根据查询条件和查询类型
     * */
    private QueryBuilder createQueryBuilder(HashMap<String, Object[]> searchContentMap, SearchLogic searchLogic) {
        try {
            if (searchContentMap == null || searchContentMap.size() ==0) {
                return null;
            }
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            Iterator<Entry<String, Object[]>> iterator = searchContentMap.entrySet().iterator();
            /*循环每一个需要搜索的字段和值*/
            while (iterator.hasNext()) {
                Entry<String, Object[]> entry = iterator.next();
                String field = entry.getKey();
                Object[] values = entry.getValue();
                /*排除非法的搜索值*/
                if (!this.checkValue(values)) {
                    continue;
                }
                /*获得搜索类型*/
                MySearchOption mySearchOption = this.getSearchOption(values);
                QueryBuilder queryBuilder = this.createSingleFieldQueryBuilder(field, values, mySearchOption);
                if (queryBuilder != null) {
                    if (searchLogic == SearchLogic.should) {
                        /*should关系，也就是说，在A索引里有或者在B索引里有都可以*/
                        boolQueryBuilder = boolQueryBuilder.should(queryBuilder);
                    }
                    else {
                        /*must关系，也就是说，在A索引里有，在B索引里也必须有*/
                        boolQueryBuilder = boolQueryBuilder.must(queryBuilder);
                    }
                }
            }
            return boolQueryBuilder;
        }
        catch (Exception e) {
            this.logger.error(e.getMessage());
        }
        return null;
    }

    public void destroy() throws Exception {
        this.logger.info("Close the Search Client ...");
        this.close();
    }

    private String formatInsertData(Object[] values) {
        if (!this.checkValue(values)) {
            return "";
        }
        if (MySearchOption.isDate(values[0])) {
            this.logger.warn("[" + values[0].toString() + "] formatDate");
            return MySearchOption.formatDate(values[0]);
        }
        String formatValue = values[0].toString();
        for (int index = 1; index < values.length; ++index) {
            formatValue += "," + values[index].toString();
        }
        return formatValue.trim();
    }

    public long getCount(String[] indexNames, HashMap<String, Object[]> searchContentMap, HashMap<String, Object[]> filterContentMap) {
        SearchLogic searchLogic = indexNames.length > 1 ? SearchLogic.should : SearchLogic.must;
        return this.getCount(indexNames, searchContentMap, searchLogic, filterContentMap, searchLogic);
    }

    private SearchResponse searchCountRequest(String[] indexNames, Object queryBuilder) {
        try {
            SearchRequestBuilder searchRequestBuilder = this.searchClient.prepareSearch(indexNames).setSearchType(SearchType.COUNT);
            if (queryBuilder instanceof QueryBuilder) {
                searchRequestBuilder = searchRequestBuilder.setQuery((QueryBuilder)queryBuilder);
                this.logger.debug(searchRequestBuilder.toString());
            }
            if (queryBuilder instanceof byte[]) {
                String query = new String((byte[])queryBuilder);
                searchRequestBuilder = searchRequestBuilder.setQuery(QueryBuilders.wrapperQuery(query));
                this.logger.debug(query);
            }
            return searchRequestBuilder.execute().actionGet();
        }
        catch (Exception e) {
            this.logger.error(e.getMessage());
        }
        return null;
    }

    public long getCount(String[] indexNames, byte[] queryString) {
        try {
            SearchResponse searchResponse = this.searchCountRequest(indexNames, queryString);
            return searchResponse.getHits().totalHits();
        }
        catch (Exception e) {
            this.logger.error(e.getMessage());
        }
        return 0;
    }

    /*获得搜索结果，对搜索出的结果信息，进行二次封装--*/
    private List<Map<String, Object>> getSearchResult(SearchResponse searchResponse) {
        try {
            List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
            
            for (SearchHit searchHit : searchResponse.getHits()) {
                Iterator<Entry<String, Object>> iterator = searchHit.getSource().entrySet().iterator();
                HashMap<String, Object> resultMap = new HashMap<String, Object>();
                while (iterator.hasNext()) {
                    Entry<String, Object> entry = iterator.next();
                    resultMap.put(entry.getKey(), entry.getValue());
                }
                //对日期进行格式化操作
                this.addFormatDateColumn(resultMap);
                Map<String, HighlightField> highlightMap = searchHit.highlightFields();
                Iterator<Entry<String, HighlightField>> highlightIterator = highlightMap.entrySet().iterator();
                while (highlightIterator.hasNext()) {
                    Entry<String, HighlightField> entry = highlightIterator.next();
                    Object[] contents = entry.getValue().fragments();
                    if (contents.length == 1) {
                        resultMap.put(entry.getKey(), contents[0].toString());
                        //System.out.println(contents[0].toString());
                    }
                    else {
                        this.logger.warn("搜索结果中的高亮结果出现多数据contents.length = " + contents.length);
                    }
                }
                //加入查询结果总数，分页使用
                resultMap.put("totalHits", searchResponse.getHits().getTotalHits());
                resultList.add(resultMap);
            }
            return resultList;
        }
        catch (Exception e) {
            this.logger.error(e.getMessage());
        }
        return null;
    }

    /*获得搜索选项*/
    private MySearchOption getSearchOption(Object[] values) {
        try {
            for (Object item : values) {
                if (item instanceof MySearchOption) {
                    return (MySearchOption) item;
                }
            }
        }
        catch (Exception e) {
            this.logger.error(e.getMessage());
        }
        return new MySearchOption();
    }

    /*获得搜索建议
     * 服务器端安装elasticsearch-plugin-suggest
     * 客户端加入elasticsearch-plugin-suggest的jar包
     * https://github.com/spinscale/elasticsearch-suggest-plugin
     * 暂时不实现此接口，后续可以添加此接口
     * */
    public List<String> getSuggest(String[] indexNames, String fieldName, String value, int count) {
//        try {
//            SuggestRequestBuilder suggestRequestBuilder = new SuggestRequestBuilder(this.searchClient);
//            suggestRequestBuilder = suggestRequestBuilder.setIndices(indexNames).field(fieldName).term(value).size(count);//.similarity(0.5f);
//            SuggestResponse suggestResponse = suggestRequestBuilder.execute().actionGet();
//            return suggestResponse.suggestions();
//        }
//        catch (Exception e) {
//            this.logger.error(e.getMessage());
//        }
//        return null;
    	return null;
    }

    /*
     * 创建搜索客户端
     * tcp连接搜索服务器
     * 创建索引
     * 创建mapping
     * */
    private synchronized void open() {
    	if( this.clusterList != null &&  !this.clusterList.isEmpty()){
    		//确认已经获得搜索的客户端连接
    	}else{
    		try {
                /*如果10秒没有连接上搜索服务器，则超时*/
    			Settings settings = null;
    			if(this.searchClientConfigureMap == null){
    				Map<String, String> paramMap = new HashMap<String, String>();
    				paramMap.put("cluster.name", esProperties.getClusterName());
    				settings = ImmutableSettings.settingsBuilder().put(paramMap).build();
    			}else{
    				settings = ImmutableSettings.settingsBuilder().put(this.searchClientConfigureMap).build();
    			}
               
                /*创建搜索客户端*/
                this.searchClient = new TransportClient(settings);
                if (CollectionUtils.isEmpty(this.clusterList)) {
    				String cluster = esProperties.getSearchClusterList();
                    if (cluster != null) {
                        this.clusterList = Arrays.asList(cluster.split(","));
                    }
                }
                for (String item : this.clusterList) {
                    String address = item.split(":")[0];
                    int port = Integer.parseInt(item.split(":")[1]);
                    /*通过tcp连接搜索服务器，如果连接不上，有一种可能是服务器端与客户端的jar包版本不匹配*/
                    this.searchClient = ((TransportClient) this.searchClient)
                    		.addTransportAddress(new InetSocketTransportAddress(address, port));
                }
            }
            catch (Exception e) {
                this.logger.error(e.getMessage());
            }
    	}
    }

    public void setClusterList(List<String> clusterList) {
        this.clusterList = clusterList;
    }

    public List<Map<String, Object>> simpleSearch(boolean isCheckRed, String[] indexNames, HashMap<String, Object[]> searchContentMap, HashMap<String, Object[]> filterContentMap, int from, int offset) {
        return this.simpleSearch(isCheckRed, indexNames, searchContentMap, filterContentMap, from, offset, null, null);
    }

    public List<Map<String, Object>> simpleSearch(boolean isCheckRed, String[] indexNames, HashMap<String, Object[]> searchContentMap, HashMap<String, Object[]> filterContentMap, int from, int offset, String sortField, String sortType)
    {
    	SearchLogic searchLogic = indexNames.length > 1 ? SearchLogic.should : SearchLogic.must;
        return this.simpleSearch(isCheckRed, indexNames, searchContentMap, searchLogic, filterContentMap, searchLogic, from, offset, sortField, sortType);
    }

    public long getComplexCount(String[] indexNames
            , HashMap<String, Object[]> mustSearchContentMap
            , HashMap<String, Object[]> shouldSearchContentMap) {
        /*创建must搜索条件*/
    	this.open();
        QueryBuilder mustQueryBuilder = this.createQueryBuilder(mustSearchContentMap, SearchLogic.must);
        /*创建should搜索条件*/
        QueryBuilder shouldQueryBuilder = this.createQueryBuilder(shouldSearchContentMap, SearchLogic.should);
        if (mustQueryBuilder == null && shouldQueryBuilder == null) {
            return 0;
        }
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (mustQueryBuilder != null) {
            boolQueryBuilder = boolQueryBuilder.must(mustQueryBuilder);
        }
        if (shouldQueryBuilder != null) {
            boolQueryBuilder = boolQueryBuilder.must(shouldQueryBuilder);
        }
        try {
            SearchResponse searchResponse = this.searchCountRequest(indexNames, boolQueryBuilder);
            return searchResponse.getHits().totalHits();
        }
        catch (Exception e) {
            this.logger.error(e.getMessage());
        }
        return 0;
    }

    public List<Map<String, Object>> complexSearch(String[] indexNames
            , HashMap<String, Object[]> mustSearchContentMap
            , HashMap<String, Object[]> shouldSearchContentMap
            , int from, int offset, @Nullable String sortField, @Nullable String sortType) {
        if (offset <= 0) {
            return null;
        }
        this.open();
        /*创建must搜索条件*/
        QueryBuilder mustQueryBuilder = this.createQueryBuilder(mustSearchContentMap, SearchLogic.must);
        /*创建should搜索条件*/
        QueryBuilder shouldQueryBuilder = this.createQueryBuilder(shouldSearchContentMap, SearchLogic.should);
        if (mustQueryBuilder == null && shouldQueryBuilder == null) {
            return null;
        }
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (mustQueryBuilder != null) {
            boolQueryBuilder = boolQueryBuilder.must(mustQueryBuilder);
        }
        if (shouldQueryBuilder != null) {
            boolQueryBuilder = boolQueryBuilder.must(shouldQueryBuilder);
        }
        try {
            SearchRequestBuilder searchRequestBuilder = null;
            searchRequestBuilder = this.searchClient.prepareSearch(indexNames).setSearchType(SearchType.DEFAULT)
                    .setQuery(boolQueryBuilder).setFrom(from).setSize(offset).setExplain(true);
            if (sortField == null || sortField.isEmpty() || sortType == null || sortType.isEmpty()) {
                /*如果不需要排序*/
            }
            else {
                /*如果需要排序*/
                org.elasticsearch.search.sort.SortOrder sortOrder = sortType.equals("desc") ? org.elasticsearch.search.sort.SortOrder.DESC : org.elasticsearch.search.sort.SortOrder.ASC;
                searchRequestBuilder = searchRequestBuilder.addSort(sortField, sortOrder);
            }
            searchRequestBuilder = this.createHighlight(searchRequestBuilder, mustSearchContentMap);
            searchRequestBuilder = this.createHighlight(searchRequestBuilder, shouldSearchContentMap);
            this.logger.debug(searchRequestBuilder.toString());
            SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
            return this.getSearchResult(searchResponse);
        }
        catch (Exception e) {
        	e.printStackTrace() ;
            this.logger.error(e);
        }
        return null;
    }
    
    @Deprecated
    public List<Map<String, Object>> simpleSearch(String[] indexNames, String queryString, int from, int offset, String sortField, String sortType) {
       
    	if (offset <= 0) {
            return null;
        }
    	this.open();
        try {
            SearchRequestBuilder searchRequestBuilder = this.searchClient.prepareSearch(indexNames).setSearchType(SearchType.DEFAULT)
                    .setFrom(from).setSize(offset).setExplain(true);
            if (sortField == null || sortField.isEmpty() || sortType == null || sortType.isEmpty()) {
                /*如果不需要排序*/
            }
            else {
                /*如果需要排序*/
                org.elasticsearch.search.sort.SortOrder sortOrder = sortType.equals("desc") ? org.elasticsearch.search.sort.SortOrder.DESC : org.elasticsearch.search.sort.SortOrder.ASC;
                searchRequestBuilder = searchRequestBuilder.addSort(sortField, sortOrder);
            }

            //searchRequestBuilder = searchRequestBuilder.setQuery(QueryBuilders.wrapperQuery(query));
            searchRequestBuilder = searchRequestBuilder.setQuery( QueryBuilders.matchQuery(queryString.split(":")[0], queryString.split(":")[1]));
            this.logger.debug(queryString);
            SearchResponse response = searchRequestBuilder.execute().actionGet();
            
            return this.getSearchResult(response);
        }
        catch (Exception e) {
            this.logger.error(e.getMessage());
            //System.out.println(e.getMessage().toString());
        }
        return null;
    }
    
    public List<Map<String, Object>> simpleSearch(boolean isCheckRed,String[] indexNames, HashMap<String, Object[]> searchContentMap, int from, int offset, String sortField, String sortType) {
        
    	if (offset <= 0) {
            return null;
        }
    	this.open();
    	SearchLogic searchLogic = indexNames.length > 1 ? SearchLogic.should : SearchLogic.must;
        try {
            SearchRequestBuilder searchRequestBuilder = this.searchClient.prepareSearch(indexNames).setSearchType(SearchType.DEFAULT)
                    .setFrom(from).setSize(offset);
            if (sortField == null || sortField.isEmpty() || sortType == null || sortType.isEmpty()) {
                /*如果不需要排序*/
            }
            else {
                /*如果需要排序*/
                org.elasticsearch.search.sort.SortOrder sortOrder = sortType.equals("desc") ? org.elasticsearch.search.sort.SortOrder.DESC : org.elasticsearch.search.sort.SortOrder.ASC;
                searchRequestBuilder = searchRequestBuilder.addSort(sortField, sortOrder);
            }

            searchRequestBuilder = searchRequestBuilder.setQuery(this.createQueryBuilder(searchContentMap, searchLogic));
            this.logger.debug(searchContentMap.toString());
            searchRequestBuilder = this.createHighlight(searchRequestBuilder, searchContentMap);
            SearchResponse response = searchRequestBuilder.execute().actionGet();
            if(isCheckRed){//需要进行红名单验证
            	List<Map<String,Object>> resultMap = this.getSearchResult(response);
            	return this.getSearchResultExcludeRedUserInfo(resultMap);
            }else{
            	return this.getSearchResult(response);
            }
        }
        catch (Exception e) {
            this.logger.error(e.getMessage());
        }
        return null;
    }

    private Map<String, String> _group(String indexName, QueryBuilder queryBuilder, String[] groupFields) {
        try {
            TermsFacetBuilder termsFacetBuilder = FacetBuilders.termsFacet("group").fields(groupFields).size(9999);
            SearchRequestBuilder searchRequestBuilder = this.searchClient.prepareSearch(indexName).setSearchType(SearchType.DEFAULT)
                    .addFacet(termsFacetBuilder).setQuery(queryBuilder).setFrom(0).setSize(1).setExplain(true);
            this.logger.debug(searchRequestBuilder.toString());
            SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
            TermsFacet termsFacet = searchResponse.getFacets().facet("group");
            HashMap<String, String> result = new HashMap<String, String>();
            for (org.elasticsearch.search.facet.terms.TermsFacet.Entry entry : termsFacet.getEntries()) {
                result.put(entry.getTerm().toString(), entry.getCount() + "");
            }
            return result;
        }
        catch (Exception e) {
            this.logger.error(e.getMessage());
        }
        return null;
    }

    public Map<String, String> group(String indexName
            , HashMap<String, Object[]> mustSearchContentMap
            , HashMap<String, Object[]> shouldSearchContentMap
            , String[] groupFields) {
    	 this.open();
        /*创建must搜索条件*/
        QueryBuilder mustQueryBuilder = this.createQueryBuilder(mustSearchContentMap, SearchLogic.must);
        /*创建should搜索条件*/
        QueryBuilder shouldQueryBuilder = this.createQueryBuilder(shouldSearchContentMap, SearchLogic.should);
        if (mustQueryBuilder == null && shouldQueryBuilder == null) {
            return null;
        }
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (mustQueryBuilder != null) {
            boolQueryBuilder = boolQueryBuilder.must(mustQueryBuilder);
        }
        if (shouldQueryBuilder != null) {
            boolQueryBuilder = boolQueryBuilder.must(shouldQueryBuilder);
        }
        try {
            return this._group(indexName, boolQueryBuilder, groupFields);
        }
        catch (Exception e) {
            this.logger.error(e.getMessage());
        }
        return null;
    }

    public List<Map<String, Object>> simpleSearch(boolean isCheckRed, String[] indexNames
            , HashMap<String, Object[]> searchContentMap, SearchLogic searchLogic
            , HashMap<String, Object[]> filterContentMap, SearchLogic filterLogic
            , int from, int offset, String sortField, String sortType){
        if (offset <= 0) {
            return null;
        }
        this.open();
        try {
            QueryBuilder queryBuilder = null;
            queryBuilder = this.createQueryBuilder(searchContentMap, searchLogic);
            queryBuilder = this.createFilterBuilder(filterLogic, queryBuilder, searchContentMap, filterContentMap);
            
           // this.searchClient.prepareSearch(indexNames).setTypes(types)
            //1_ryxx,2_ryxx  //1_type,2_type
            SearchRequestBuilder searchRequestBuilder = this.searchClient.prepareSearch(indexNames).setSearchType(SearchType.DEFAULT)
                    .setQuery(queryBuilder).setFrom(from).setSize(offset).setExplain(true);
            if (sortField == null || sortField.isEmpty() || sortType == null || sortType.isEmpty()) {
                /*如果不需要排序*/
            }
            else {
                /*如果需要排序*/
                org.elasticsearch.search.sort.SortOrder sortOrder = sortType.equals("desc") ? org.elasticsearch.search.sort.SortOrder.DESC : org.elasticsearch.search.sort.SortOrder.ASC;
                searchRequestBuilder = searchRequestBuilder.addSort(sortField, sortOrder);
            }
            searchRequestBuilder = this.createHighlight(searchRequestBuilder, searchContentMap);
            this.logger.debug(searchRequestBuilder.toString());
            SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
            if(isCheckRed){ //过滤红名单信息
            	List<Map<String,Object>> resultMap = this.getSearchResult(searchResponse);
            	return this.getSearchResultExcludeRedUserInfo(resultMap);
            }else{
            	return this.getSearchResult(searchResponse);
            }
        }
        catch (Exception e) {
            this.logger.error(e.getMessage());
        }
        return null;
    }
    
 

    private SearchRequestBuilder createHighlight(SearchRequestBuilder searchRequestBuilder, HashMap<String, Object[]> searchContentMap) {
        Iterator<Entry<String, Object[]>> iterator = searchContentMap.entrySet().iterator();
        /*循环每一个需要搜索的字段和值*/
        while (iterator.hasNext()) {
            Entry<String, Object[]> entry = iterator.next();
            String field = entry.getKey();
            Object[] values = entry.getValue();
            /*排除非法的搜索值*/
            if (!this.checkValue(values)) {
                continue;
            }
            /*获得搜索类型*/
            MySearchOption mySearchOption = this.getSearchOption(values);
            if (mySearchOption.isHighlight()) {
                /*
                 * http://www.elasticsearch.org/guide/reference/api/search/highlighting.html
                 *
                 * fragment_size设置成1000，默认值会造成返回的数据被截断
                 * */
                searchRequestBuilder = searchRequestBuilder.addHighlightedField(field, 1000)
                        .setHighlighterPreTags("<"+this.highlightCSS.split(",")[0]+">")
                        .setHighlighterPostTags("</"+this.highlightCSS.split(",")[1]+">");
            }
        }
        return searchRequestBuilder;
    }

    public long getCount(String[] indexNames
            , HashMap<String, Object[]> searchContentMap, SearchLogic searchLogic
            , @Nullable HashMap<String, Object[]> filterContentMap, @Nullable SearchLogic filterLogic)
    {
    	this.open();
        QueryBuilder queryBuilder = null;
        try {
            queryBuilder = this.createQueryBuilder(searchContentMap, searchLogic);
            queryBuilder = this.createFilterBuilder(searchLogic, queryBuilder, searchContentMap, filterContentMap);
            SearchResponse searchResponse = this.searchCountRequest(indexNames, queryBuilder);
            return searchResponse.getHits().totalHits();
        }
        catch (Exception e) {
            this.logger.error(e.getMessage());
        }
        return 0;
    }

	
	

	public long getCount(String keyword,SearchIndexType indexType,Map<String,Object> fieldNamesAndValues,Map<String,String> fieldNamesAndTypes) {
		this.open();
		SearchRequestBuilder searchRequestBuilder = this.createSearchRequestBuilder(indexType);
		QueryBuilder boolQueryBuilder = createBoolQueryBuilder(fieldNamesAndValues,fieldNamesAndTypes);
		QueryStringQueryBuilder queryStringBuilder = createQueryStringQueryBuilder(keyword);
		SearchResponse searchResponse = searchRequestBuilder
				.setQuery(
						QueryBuilders.filteredQuery(queryStringBuilder,
								FilterBuilders.queryFilter(boolQueryBuilder)))
				.setSearchType(SearchType.COUNT).execute().actionGet();
		 if(logger.isDebugEnabled()){
			 logger.debug(searchRequestBuilder);	
		 }
		SearchHits hits = searchResponse.getHits();
		return hits.getTotalHits();
	}
	
	
	
	
	/**
     * 分页的搜索
     * @return
     * @author wenlei.luo 
     */
	private SearchResponse result(String queryString,SearchIndexType indexType,Map<String,Object> fieldNamesAndValues,List<SearchAppMeta> viewFieldList,
			Map<String,String> fieldNamesAndTypes,long pageIndex,long pageSize) {
		return search(queryString, indexType,fieldNamesAndValues, viewFieldList,fieldNamesAndTypes,pageIndex,pageSize);

	}

	public List<Map<String,Object>> simpleSearch(boolean isCheckRed, String queryString,SearchIndexType indexType,Map<String,Object> condition,
			List<SearchAppMeta> viewFieldList,Map<String,String> conditionTypes,long pageIndex,long pageSize) {
		this.open();
		SearchResponse searchResponse = result(queryString,indexType,condition,viewFieldList,conditionTypes,pageIndex,pageSize);
		
		if(isCheckRed){ //红名单信息过滤
			List<Map<String,Object>> restList = toList(searchResponse);
			return this.getSearchResultExcludeRedUserInfo(restList);
		}else{
			return toList(searchResponse);
		}
	}

	/**
	 * 添加基础信息 _index,_type,_id
	 * @param hit
	 * @param map
	 */
	public static void addMetadata(SearchHit hit, Map<String, Object> map) {
		map.put(Metadata._id.toString(),hit.getId());
		map.put(Metadata._type.toString(),hit.getType());
		map.put(Metadata._index.toString(), hit.getIndex());
	}

	/**
	 * 对日期字段进行过滤，时区修改为东八区
	 * @param map
	 */
	public void addFormatDateColumn(Map<String,Object> map){
		if(null == formatDateColumns){
			formatDateColumns = Arrays.asList(this.formatDateColumn.split(";"));
		}
		Set<String> keys = map.keySet();
		for(String key : keys){
			if(formatDateColumns.contains(key)){
				//时区修改，东0区修改为东8区
				map.put(key, DateUtils.format(DateUtils.hourOffset(
						DateUtils.parse(((String)map.get(key)).replaceAll(".000Z", "").replaceAll("T", " "),
						DateUtils.TO_SECOND),8),DateUtils.TO_SECOND));
			}
			if(key.equals(Constant.PARAM_FORMAT_XDATA_RKSJ)){ //日期修改为标准时间格式'yyyy-MM-dd hh:mm:ss'
				map.put(key,DateUtils.format(DateUtils.getDateMillionSeconds((long)map.get(key)), DateUtils.TO_SECOND));
			}
		}
	}
	
	/**
     *	设置高亮
     * @return
     * @author wenlei.luo
     */
	private SearchRequestBuilder  addHighlightedField(SearchRequestBuilder searchRequestBuilder,List<SearchAppMeta> highlightedFields) {
		if(highlightedFields ==null || highlightedFields.size() == 0){
			searchRequestBuilder.addHighlightedField("*");
			return searchRequestBuilder;
		}
		for(SearchAppMeta sf : highlightedFields) {
			if("1".equals(sf.getEsHighFlag())){
				searchRequestBuilder.addHighlightedField(sf.getFieldName().toUpperCase());
			}
		}
		return searchRequestBuilder;
	}
	private SearchRequestBuilder  createSearchRequestBuilder(SearchIndexType indexType) {
		String[] indexName = new String[]{};
		String[] typeName = new String[]{};
		if(StringUtils.isNotEmpty(indexType.getIndexName())){
			 indexName = indexType.getIndexName().split(";");
		}
		if(StringUtils.isNotEmpty(indexType.getTypeName())){
			typeName = indexType.getTypeName().split(";");
		}
		SearchRequestBuilder sb = this.searchClient.prepareSearch(indexName);
		if(typeName.length > 0){	
			sb.setTypes(typeName);
		}
		return sb;
	}
	
	/**
	 * 排除车牌等未识别的车辆信息
	 * @param boolQueryBuilder
	 * @return
	 */
	private void addExcludeUnidentified(QueryBuilder boolQueryBuilder) {
		boolQueryBuilder = ((BoolQueryBuilder) boolQueryBuilder).mustNot(QueryBuilders.termsQuery(
						Constant.PARAM_USER_INFO_CAR_ID,Constant.UNIDENTIFIED_CAR_ID_NO1,
						Constant.UNIDENTIFIED_CAR_ID_NO2,Constant.UNIDENTIFIED_CAR_ID_NO3,
						Constant.UNIDENTIFIED_CAR_ID_NO4));
	}
	/**
     *	分页的搜索的实现过程
     * @return
     * @author wenlei.luo
     */
	private SearchResponse search(String queryString,SearchIndexType indexType,Map<String,Object> fieldNamesAndValues,
			List<SearchAppMeta> highlightedFields,Map<String,String> fieldNamesAndTypes,long pageIndex,long pageSize) {
		pageIndex = pageIndex - 1;
		SearchRequestBuilder searchRequestBuilder =this.createSearchRequestBuilder(indexType).setSearchType(SearchType.DEFAULT);
		//创建查询 builder
		QueryBuilder boolQueryBuilder = createBoolQueryBuilder(fieldNamesAndValues, fieldNamesAndTypes);
	    //添加高亮字段
		 this.addHighlightedField(searchRequestBuilder, highlightedFields);
		 //创建全文搜索builder
		 QueryStringQueryBuilder queryStringQueryBuilder = createQueryStringQueryBuilder(queryString,highlightedFields); 
		// queryStringQueryBuilder.
		 SearchResponse searchResponse = searchRequestBuilder
					.setFrom((int)pageIndex * (int)pageSize)
					.setSize((int)pageSize)
					.setHighlighterPreTags("<"+this.highlightCSS.split(",")[0]+">")
                    .setHighlighterPostTags("</"+this.highlightCSS.split(",")[1]+">")
					.setQuery(QueryBuilders.filteredQuery(queryStringQueryBuilder, FilterBuilders.queryFilter(boolQueryBuilder)))
					.execute().actionGet();
		 if(logger.isDebugEnabled()){
			 logger.debug(searchRequestBuilder);	
		 }
		return searchResponse;

	}

	private QueryStringQueryBuilder createQueryStringQueryBuilder(
			String queryString) {
		return createQueryStringQueryBuilder(queryString,null);
	}
	
	private QueryStringQueryBuilder createQueryStringQueryBuilder(String queryString,List<SearchAppMeta> viewFieldList) {
		QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders.queryString(queryString);
		return queryStringQueryBuilder;
	}
	
	private QueryBuilder createBoolQueryBuilder(Map<String, Object> fieldNamesAndValues,Map<String, String> fieldNamesAndTypes) {
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery(); 
		if(fieldNamesAndValues ==null || fieldNamesAndTypes ==null){
			return boolQueryBuilder;
		}
		Set<String> fieldNames = fieldNamesAndValues.keySet();
		for(String filedName : fieldNames) {
			String searchOption = getSearchType(fieldNamesAndTypes, filedName);
			//0 普通文本 ;1 字典展示; 2 时间范围 ;3 数字范围; 4 多选
 			if ("0".equals(searchOption)) {
 				boolQueryBuilder.must(QueryBuilders.wildcardQuery(filedName,fieldNamesAndValues.get(filedName).toString()));
			}else if ("1".equals(searchOption)) {
				boolQueryBuilder.must(QueryBuilders.termQuery(filedName,fieldNamesAndValues.get(filedName)));
			}else if ("2".equals(searchOption)) {
				Date[] filedValues = (Date[])fieldNamesAndValues.get(filedName);
				boolQueryBuilder.must(QueryBuilders.rangeQuery(filedName).from(filedValues[0]).to(filedValues[1]));
			}else if ("3".equals(searchOption)) {
				String[] filedValues = fieldNamesAndValues.get(filedName).toString().split(",");
				boolQueryBuilder.must(QueryBuilders.rangeQuery(filedName).from(filedValues[0]).to(filedValues[1]));
			}else if ("4".equals(searchOption)) {
				Object[] paramsObject = (Object[])fieldNamesAndValues.get(filedName);
				boolQueryBuilder.must(QueryBuilders.termsQuery(filedName,paramsObject));
			}
 			
		}
		return boolQueryBuilder;
		
	}
	
	private QueryBuilder createBoolQuerySuggest(Map<String, Object> fieldNamesAndValues,Map<String, String> fieldNamesAndTypes) {
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery(); 
		if(fieldNamesAndValues ==null || fieldNamesAndTypes ==null){
			return boolQueryBuilder;
		}
		Set<String> fieldNames = fieldNamesAndValues.keySet();
		for(String filedName : fieldNames) {
			String searchOption = getSearchType(fieldNamesAndTypes, filedName);
			//0 普通文本 ;1 字典展示; 2 时间范围 ;3 数字范围; 4 多选
 			if ("0".equals(searchOption)) {
 				boolQueryBuilder.should(QueryBuilders.wildcardQuery(filedName,fieldNamesAndValues.get(filedName).toString()));
			}else if ("1".equals(searchOption)) {
				boolQueryBuilder.should(QueryBuilders.termQuery(filedName,fieldNamesAndValues.get(filedName)));
			}else if ("2".equals(searchOption)) {
				Date[] filedValues = (Date[])fieldNamesAndValues.get(filedName);
				boolQueryBuilder.must(QueryBuilders.rangeQuery(filedName).from(filedValues[0]).to(filedValues[1]));
			}else if ("3".equals(searchOption)) {
				String[] filedValues = fieldNamesAndValues.get(filedName).toString().split(",");
				boolQueryBuilder.must(QueryBuilders.rangeQuery(filedName).from(filedValues[0]).to(filedValues[1]));
			}else if ("4".equals(searchOption)) {
				Object[] paramsObject = (Object[])fieldNamesAndValues.get(filedName);
				boolQueryBuilder.should(QueryBuilders.termsQuery(filedName,paramsObject));
			}
 			
		}
		return boolQueryBuilder;
		
	}

	private String getSearchType(Map<String, String> conditionTypes,
			String filedName) {
		String fieldType = conditionTypes.get(filedName);
		return fieldType;
	}

	public List<Map<String, Object>> multiMatchSearch(String[] indexNames,
			String[] fileds, Object queryObject,
			HashMap<String, Object[]> highlightSearchContentMap, int from,
			int offset, String sortField, String sortType) {
		
		return null;
	}

	public List<Map<String, Object>> multiGetByIndexAndIds(String index,
			List<String> ids) {
		return multiGetByIndexAndTypeAndIds(index, null, ids);
	}

	public List<Map<String, Object>> multiGetByIndexAndTypeAndIds(String index,
			String type, List<String> ids) {
		this.open();
		MultiGetRequestBuilder mgr = this.searchClient.prepareMultiGet();
		for (String id : ids) {
			mgr.add(index, type, id);
		}
		List<Map<String, Object>> resultList = executeAndGet(mgr);// 此地方可以添加 分页功能 executeAndGet(mgr).subList(fromIndex, toIndex)
		return resultList;
	}

	private List<Map<String, Object>> executeAndGet(MultiGetRequestBuilder mgr) {
		MultiGetItemResponse[] multiResp = mgr.get().getResponses();
		List<Map<String, Object>>resultList= new ArrayList<Map<String, Object>>();
		for (int i = 0; i < multiResp.length; i++) {
			GetResponse getResp = multiResp[i].getResponse();
			String _type = getResp.getType();
			String _id = getResp.getId();
			String _index = getResp.getIndex();
			Map<String, Object> sourceAsMap = getResp.getSourceAsMap();
			if(sourceAsMap == null){
				continue;
			}
			sourceAsMap.put(Metadata._id.toString(),_id);
			sourceAsMap.put(Metadata._type.toString(),_type);
			sourceAsMap.put(Metadata._index.toString(), _index);
			resultList.add(sourceAsMap);
		}
		return resultList;
	}
	
	

	public enum Metadata {
		//文档所在的索引
		_index
		//文档的类型名
		, _type
		//文档的字符串 ID
		, _id ;
	}



	@Override
	public List<Map<String, Object>> multiGetByDocumentItem(List<AttentionContent> attContents) {
		this.open();
		if(attContents.size() == 0){
			return new ArrayList<Map<String,Object>>(0);
		}
		MultiGetRequestBuilder mgr = this.searchClient.prepareMultiGet();
		for (AttentionContent item : attContents) {
			mgr.add(item.getEsIndices(), item.getEsTypes(), item.getEsDocId());
		}
		List<Map<String, Object>> resultList = executeAndGet(mgr);
		return resultList;
	}

	@Override
	public List<Map<String, Object>> simpleSearch(String keyword,String[] indices, long pageIndex, long pageSize) {
		this.open();
		
		pageIndex = pageIndex - 1;
		SearchRequestBuilder searchRequestBuilder =this.searchClient.prepareSearch(indices).setSearchType(SearchType.DEFAULT);
	    //添加高亮字段
		 this.addHighlightedField(searchRequestBuilder, null);
		 //创建全文搜索builder
		 QueryStringQueryBuilder queryStringQueryBuilder = createQueryStringQueryBuilder(keyword,null); 
		 SearchResponse searchResponse = searchRequestBuilder
					.setFrom((int)pageIndex * (int)pageSize)
					.setSize((int)pageSize)
					.setHighlighterPreTags("<"+this.highlightCSS.split(",")[0]+">")
                    .setHighlighterPostTags("</"+this.highlightCSS.split(",")[1]+">")
					.setQuery(queryStringQueryBuilder)
					.execute().actionGet();
		 if(logger.isDebugEnabled()){
			 logger.debug(searchRequestBuilder);	
		 }
		List<Map<String,Object>> resultList =  toList(searchResponse);
		return this.getSearchResultExcludeRedUserInfo(resultList);
	}

	public List<Map<String,Object>> toList(SearchResponse searchResponse) {
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		SearchHits hits = searchResponse.getHits();
		for(SearchHit hit :hits.getHits()) {
			Map<String,Object> map  = hit.getSource();
			//添加  _index,_type,_id
			addMetadata(hit, map);
			//格式化日期类型
			addFormatDateColumn(map);
         //  添加高亮  
            Map<String, HighlightField> result = hit.highlightFields(); 
            //从设定的高亮域中取得指定域
            Set<String> set = result.keySet();
            for(String key: set) {
            	HighlightField titleField = result.get(key); 
            	//	         取得定义的高亮标签
            	if(titleField != null) {
            		Text[] titleTexts =  titleField.fragments();    
            		//为title串值增加自定义的高亮标签
            		String title = "";
            		for(Text text : titleTexts){    
            			title += text;  
            		}
            		map.put(key, title);
            	}
            }
            list.add(map);
		}
		return list;
	}
	//--//
	@Override
	public List<Map<String, Object>> fullTextQuery(boolean isRedCheck, String queryString,
			SearchIndexType type, String[] fieldNames,String analyzer,
			Map<String, Object> fieldNamesAndValues, String[] highlightedFields,
			Map<String, String> fieldNamesAndTypes, long pageIndex, long pageSize) {
		pageIndex = pageIndex - 1;
		 //创建全文搜索builder
		if(StringUtils.isNotEmpty(queryString)){//queryString 非空查询
			QueryStringQueryBuilder qsqb = QueryBuilders.queryString(queryString);

			if(StringUtils.isNotEmpty(analyzer)){			
				qsqb.analyzer(analyzer);
			}
			qsqb.minimumShouldMatch("75");
			addSearchField(fieldNames, qsqb);
			SearchRequestBuilder searchRequestBuilder =this.createSearchRequestBuilder(type).setSearchType(SearchType.DEFAULT);
			//创建查询 builder
			QueryBuilder boolQueryBuilder = createBoolQueryBuilder(fieldNamesAndValues, fieldNamesAndTypes);
			addExcludeUnidentified(boolQueryBuilder);
		    //添加高亮字段
			for(String highlightedField : highlightedFields) {
				searchRequestBuilder.addHighlightedField(highlightedField);
			}
			 SearchResponse searchResponse = searchRequestBuilder
						.setFrom((int)pageIndex * (int)pageSize)
						.setSize((int)pageSize)
						.setHighlighterPreTags("<"+this.highlightCSS.split(",")[0]+">")
	                    .setHighlighterPostTags("</"+this.highlightCSS.split(",")[1]+">")
						.setQuery(QueryBuilders.filteredQuery(qsqb, FilterBuilders.queryFilter(boolQueryBuilder)))
						.execute().actionGet();
			 if(logger.isDebugEnabled()){
				 logger.debug(searchRequestBuilder);	
			 }
			logger.info(searchRequestBuilder);
			if(isRedCheck){ //红名单信息过滤
				List<Map<String,Object>> resultMap =  toList(searchResponse);
				return this.getSearchResultExcludeRedUserInfo(resultMap);
			}else{
				return toList(searchResponse);
			}
		}else{ //queryString 空查询
			SearchRequestBuilder searchRequestBuilder =this.createSearchRequestBuilder(type).setSearchType(SearchType.DEFAULT);
			//创建查询 builder
			QueryBuilder boolQueryBuilder = createBoolQueryBuilder(fieldNamesAndValues, fieldNamesAndTypes);
			addExcludeUnidentified(boolQueryBuilder);
		    //添加高亮字段
			for(String highlightedField : highlightedFields) {
				searchRequestBuilder.addHighlightedField(highlightedField);
			}
			 SearchResponse searchResponse = searchRequestBuilder
						.setFrom((int)pageIndex * (int)pageSize)
						.setSize((int)pageSize)
						.setHighlighterPreTags("<"+this.highlightCSS.split(",")[0]+">")
	                    .setHighlighterPostTags("</"+this.highlightCSS.split(",")[1]+">")
						.setQuery(boolQueryBuilder)
						.execute().actionGet();
			 if(logger.isDebugEnabled()){
				 logger.debug(searchRequestBuilder);	
			 }
			logger.info(searchRequestBuilder);
			if(isRedCheck){ //红名单信息过滤
				List<Map<String,Object>> resultMap =  toList(searchResponse);
				return this.getSearchResultExcludeRedUserInfo(resultMap);
			}else{
				return toList(searchResponse);
			}
		}
		
	}
	//--//
	@Override
	public List<Map<String, Object>> fullTextQuery(boolean isRedCheck, String queryString,SearchIndexType type, 
			String[] fieldNames,String analyzer, @Nullable Map<String, Object> fieldNamesAndValues, 
			String[] highlightedFields,@Nullable Map<String, String> fieldNamesAndTypes, long pageIndex, long pageSize, 
			@Nullable String sortField,@Nullable String sortType){
		pageIndex = pageIndex - 1;
		//queryString 非空查询
		if(StringUtils.isNotEmpty(queryString) ){
			 //创建全文搜索builder
			QueryStringQueryBuilder qsqb = QueryBuilders.queryString(queryString);

			if(StringUtils.isNotEmpty(analyzer)){			
				qsqb.analyzer(analyzer);
			}
			qsqb.minimumShouldMatch("75");
			addSearchField(fieldNames, qsqb);
			SearchRequestBuilder searchRequestBuilder =this.createSearchRequestBuilder(type).setSearchType(SearchType.DEFAULT);
			//创建查询 builder
			QueryBuilder boolQueryBuilder = createBoolQueryBuilder(fieldNamesAndValues, fieldNamesAndTypes);
			addExcludeUnidentified(boolQueryBuilder);
	        if (sortField == null || sortField.isEmpty() || sortType == null || sortType.isEmpty()) {
	            /*如果不需要排序*/
	        }
	        else {
	            /*如果需要排序*/
	            SortOrder sortOrder = sortType.equals("desc") ? SortOrder.DESC : SortOrder.ASC;
	            searchRequestBuilder = searchRequestBuilder.addSort(sortField, sortOrder);
	        }
		    //添加高亮字段
			for(String highlightedField : highlightedFields) {
				searchRequestBuilder.addHighlightedField(highlightedField);
			}
			 SearchResponse searchResponse = searchRequestBuilder
						.setFrom((int)pageIndex * (int)pageSize)
						.setSize((int)pageSize)
						.setHighlighterPreTags("<"+this.highlightCSS.split(",")[0]+">")
	                    .setHighlighterPostTags("</"+this.highlightCSS.split(",")[1]+">")
						.setQuery(QueryBuilders.filteredQuery(qsqb, FilterBuilders.queryFilter(boolQueryBuilder)))
						.execute().actionGet();
			 if(logger.isDebugEnabled()){
				 logger.debug(searchRequestBuilder);	
			 }
			logger.info(searchRequestBuilder);
			if(isRedCheck){ //红名单信息过滤
				List<Map<String,Object>> resultMap =  toList(searchResponse);
				return this.getSearchResultExcludeRedUserInfo(resultMap);
			}else{
				return toList(searchResponse);
			}
		} else{ //queryString 空查询
			SearchRequestBuilder searchRequestBuilder =this.createSearchRequestBuilder(type).setSearchType(SearchType.DEFAULT);
			//创建查询 builder
			QueryBuilder boolQueryBuilder = createBoolQueryBuilder(fieldNamesAndValues, fieldNamesAndTypes);
			addExcludeUnidentified(boolQueryBuilder);
	        if (sortField == null || sortField.isEmpty() || sortType == null || sortType.isEmpty()) {
	            /*如果不需要排序*/
	        }
	        else {
	            /*如果需要排序*/
	            SortOrder sortOrder = sortType.equals("desc") ? SortOrder.DESC : SortOrder.ASC;
	            searchRequestBuilder = searchRequestBuilder.addSort(sortField, sortOrder);
	        }
		    //添加高亮字段
			for(String highlightedField : highlightedFields) {
				searchRequestBuilder.addHighlightedField(highlightedField);
			}
			SearchResponse searchResponse = searchRequestBuilder
					.setFrom((int)pageIndex * (int)pageSize)
					.setSize((int)pageSize)
					.setHighlighterPreTags("<"+this.highlightCSS.split(",")[0]+">")
                    .setHighlighterPostTags("</"+this.highlightCSS.split(",")[1]+">")
					.setQuery(boolQueryBuilder)
					.execute().actionGet();
			 if(logger.isDebugEnabled()){
				 logger.debug(searchRequestBuilder);	
			 }
			logger.info(searchRequestBuilder);
			if(isRedCheck){ //红名单信息过滤
				List<Map<String,Object>> resultMap =  toList(searchResponse);
				return this.getSearchResultExcludeRedUserInfo(resultMap);
			}else{
				return toList(searchResponse);
			}
		}
		
	}
	@Override
	public List<Map<String, Object>> fullTextQueryFilter(boolean isRedCheck, SearchIndexType type, Map<String, Object> fieldNamesAndValues, Map<String, String> fieldNamesAndTypes, long pageIndex, long pageSize) {
		pageIndex = pageIndex - 1;
		SearchRequestBuilder searchRequestBuilder =this.createSearchRequestBuilder(type).setSearchType(SearchType.DEFAULT);
		if(StringUtils.isNotEmpty(type.getTypeName())){
			searchRequestBuilder.setTypes(type.getTypeName());
		}
		//创建查询 builder
		QueryBuilder boolQueryBuilder = createBoolQueryBuilder(fieldNamesAndValues, fieldNamesAndTypes);
		addExcludeUnidentified(boolQueryBuilder);
	    //添加高亮字段
		 SearchResponse searchResponse = searchRequestBuilder
					.setFrom((int)pageIndex * (int)pageSize)
					.setSize((int)pageSize)
					.setQuery(boolQueryBuilder)
					.execute().actionGet();
		 if(logger.isDebugEnabled()){
			 logger.debug(searchRequestBuilder);	
		 }
		 logger.info(searchRequestBuilder);
		 if(isRedCheck){//红名单信息过滤
			 List<Map<String,Object>> resultMap = toList(searchResponse);
			 return this.getSearchResultExcludeRedUserInfo(resultMap);
		 }else{
			 return toList(searchResponse);
		 }
		
	}

	private void addSearchField(String[] fieldNames,QueryStringQueryBuilder qsqb) {
		//指定查询那些字段
		for (int i = 0; i < fieldNames.length; i++) {
			qsqb.field(fieldNames[i]);			
		}
	}

	@Override
	public long wildCardCount(String keyword, String fieldName,SearchIndexType index, Map<String, Object> fieldNamesAndValues,Map<String, String> fieldNamesAndTypes) {
		this.open();
		SearchRequestBuilder searchRequestBuilder = this.createSearchRequestBuilder(index);
		QueryBuilder boolQueryBuilder = createBoolQueryBuilder(fieldNamesAndValues,fieldNamesAndTypes);
		WildcardQueryBuilder wildCardQuery = QueryBuilders.wildcardQuery(fieldName,keyword);
		
		 SearchResponse searchResponse = searchRequestBuilder.setQuery(QueryBuilders.filteredQuery(wildCardQuery, FilterBuilders.queryFilter(boolQueryBuilder)))
				 .setSearchType(SearchType.COUNT).execute().actionGet();
		 if(logger.isDebugEnabled()){
			 logger.debug(searchRequestBuilder);	
		 }
		SearchHits hits = searchResponse.getHits();
		return hits.getTotalHits();
	}
	
	@Override
	public long wildCardFilteredCount(String keyword, String fieldName,SearchIndexType indice, Map<String, Object> fieldNamesAndValues,Map<String, String> fieldNamesAndTypes) {
		this.open();
		SearchRequestBuilder searchRequestBuilder = this.createSearchRequestBuilder(indice);
		QueryBuilder boolQueryBuilder = createBoolQueryBuilder(fieldNamesAndValues,fieldNamesAndTypes);
		
		 SearchResponse searchResponse = searchRequestBuilder.setQuery(boolQueryBuilder)
				 .setSearchType(SearchType.DEFAULT).execute().actionGet();
		 if(logger.isDebugEnabled()){
			 logger.debug(searchRequestBuilder);	
		 }
		SearchHits hits = searchResponse.getHits();
		return hits.getTotalHits();
	}

	@Override
	public List<Map<String, Object>> wildCardQuery(boolean isRedCheck,String search,
			SearchIndexType type, String filedName,
			Map<String, Object> fieldNamesAndValues, List<SearchAppMeta> highlightedFields,
			Map<String, String> fieldNamesAndTypes, long pageIndex, long pageSize) {
		pageIndex = pageIndex - 1;
		QueryBuilder qb = QueryBuilders.wildcardQuery(filedName, search);
	
		SearchRequestBuilder searchRequestBuilder =this.createSearchRequestBuilder(type).setSearchType(SearchType.DEFAULT);
		//创建查询 builder
		QueryBuilder boolQueryBuilder = createBoolQueryBuilder(fieldNamesAndValues, fieldNamesAndTypes);
	    //添加高亮字段
		 this.addHighlightedField(searchRequestBuilder, highlightedFields);
		 SearchResponse searchResponse = searchRequestBuilder
					.setFrom((int)pageIndex * (int)pageSize)
					.setSize((int)pageSize)
					.setHighlighterPreTags("<"+this.highlightCSS.split(",")[0]+">")
                   .setHighlighterPostTags("</"+this.highlightCSS.split(",")[1]+">")
					.setQuery(QueryBuilders.filteredQuery(qb, FilterBuilders.queryFilter(boolQueryBuilder)))
					.execute().actionGet();
		 if(logger.isDebugEnabled()){
			 logger.debug(searchRequestBuilder);	
		 }
		if(isRedCheck){ //红名单信息过滤
			List<Map<String,Object>> resultMap =  toList(searchResponse);
			return this.getSearchResultExcludeRedUserInfo(resultMap);
		} else{
			return toList(searchResponse);
		}
	}
	/**
	 * 根据查询条件统计索引结果，如果没有指定索引下的type,则统计当前索引下面所有的type
	 * keyword 输入的关键字
	 * index 索引对象
	 * fieldNames 字段名
	 * fieldNamesAndValues 字段名和字段值
	 * fieldNamesAndTypes 字段名和字段类型
	 */
	@Override
	public Map<String,Long> getCount(String keyword, String indexName,
			String[] fieldNames, String analyzer,Map<String, Object> fieldNamesAndValues,
			Map<String, String> fieldNamesAndTypes) {
		this.open();
		SearchRequestBuilder srb = this.searchClient.prepareSearch(indexName);
		//布尔搜索 
		QueryBuilder bqb = createBoolQueryBuilder(fieldNamesAndValues,fieldNamesAndTypes);
		//
		addExcludeUnidentified(bqb);
		if(StringUtils.isNotEmpty(keyword)){
			//全文搜索
			QueryStringQueryBuilder qsb = createQueryStringQueryBuilder(keyword);
			if(StringUtils.isNotEmpty(analyzer)){			
				qsb.analyzer(analyzer);
			}
			//指定查询那些字段
			qsb.minimumShouldMatch("75");
			addSearchField(fieldNames, qsb);
			SearchResponse sr = srb.setQuery(
							QueryBuilders.filteredQuery(qsb,FilterBuilders.queryFilter(bqb))).
							addAggregation(AggregationBuilders.terms("count_by_type").field("_type").order(Terms.Order.count(false)).size(0))
					       .execute().actionGet();
			 if(logger.isDebugEnabled()){
				 logger.debug(srb);	
			 }
			System.out.println(srb);
			Terms status = sr.getAggregations().get("count_by_type");
			System.err.println(sr) ;
			Collection<Bucket> buckets = status.getBuckets();
			Map<String,Long> typeNumMap = new HashMap<String,Long>();
			for (Iterator<Bucket> iterator = buckets.iterator(); iterator.hasNext();) {
				Bucket object =  iterator.next();
				typeNumMap.put(object.getKey(), object.getDocCount());
				
			}
			return typeNumMap;
		}else{
			//全文搜索
			SearchResponse sr = srb.setQuery(bqb).
							addAggregation(AggregationBuilders.terms("count_by_type")
							.field("_type").order(Terms.Order.count(false)).size(0))
					       .execute().actionGet();
			 if(logger.isDebugEnabled()){
				 logger.debug(srb);	
			 }
			System.out.println(srb);
			Terms status = sr.getAggregations().get("count_by_type");
			System.err.println(sr) ;
			Collection<Bucket> buckets = status.getBuckets();
			Map<String,Long> typeNumMap = new HashMap<String,Long>();
			for (Iterator<Bucket> iterator = buckets.iterator(); iterator.hasNext();) {
				Bucket object =  iterator.next();
				typeNumMap.put(object.getKey(), object.getDocCount());
				
			}
			return typeNumMap;
		}
		
	}
	
	
	/**
	 * 根据查询条件统计索引结果，如果没有指定索引下的type,则统计当前索引下面所有的type
	 * keyword 输入的关键字
	 * index 索引对象
	 * fieldNames 字段名
	 * fieldNamesAndValues 字段名和字段值
	 * fieldNamesAndTypes 字段名和字段类型
	 */
	@Override
	public Map<String,Long> getFilteredCount(SearchIndexType indexType,Map<String,Object> fieldNamesAndValues,Map<String,String> fieldNamesAndTypes) {
		this.open();
		SearchRequestBuilder srb = this.searchClient.prepareSearch(indexType.getIndexName());
		srb.setTypes(indexType.getTypeName());//指定单个Type查询
		//布尔搜索 
		QueryBuilder bqb = createBoolQueryBuilder(fieldNamesAndValues,fieldNamesAndTypes);
		//全文搜索
//		QueryStringQueryBuilder qsb = createQueryStringQueryBuilder(keyword);
//		if(StringUtils.isNotEmpty(analyzer)){			
//			qsb.analyzer(analyzer);
//		}
		//指定查询那些字段
//		addSearchField(fieldNames, qsb);
		SearchResponse sr = srb.setQuery(bqb).
						addAggregation(AggregationBuilders.terms("count_by_type").field("_type").order(Terms.Order.count(false)).size(0))
				       .execute().actionGet();
		 if(logger.isDebugEnabled()){
			 logger.debug(srb);	
		 }
		logger.info(srb);
		Terms status = sr.getAggregations().get("count_by_type");
		Collection<Bucket> buckets = status.getBuckets();
		Map<String,Long> typeNumMap = new HashMap<String,Long>();
		for (Iterator<Bucket> iterator = buckets.iterator(); iterator.hasNext();) {
			Bucket object =  iterator.next();
			typeNumMap.put(object.getKey(), object.getDocCount());
			
		}
		return typeNumMap;
	}

	public List<Map<String, Object>> wildCardFilteredQuery(boolean isRedCheck, String search,
			SearchIndexType type, String filedName,
			Map<String, Object> fieldNamesAndValues, List<SearchAppMeta> highlightedFields,
			Map<String, String> fieldNamesAndTypes, long pageIndex, long pageSize) {
		pageIndex = pageIndex - 1;
		//QueryBuilder qb = QueryBuilders.wildcardQuery(filedName, search);
		SearchRequestBuilder searchRequestBuilder =this.createSearchRequestBuilder(type).setSearchType(SearchType.DEFAULT);
		//设置查询的类型：子索引范围
		searchRequestBuilder.setTypes(type.getTypeName());
		//创建查询 builder
		QueryBuilder boolQueryBuilder = createBoolQueryBuilder(fieldNamesAndValues, fieldNamesAndTypes);
	    //添加高亮字段
		 this.addHighlightedField(searchRequestBuilder, highlightedFields);
		 SearchResponse searchResponse = searchRequestBuilder
					.setFrom((int)pageIndex * (int)pageSize)
					.setSize((int)pageSize)
					.setHighlighterPreTags("<"+this.highlightCSS.split(",")[0]+">")
                   .setHighlighterPostTags("</"+this.highlightCSS.split(",")[1]+">")
                   .setQuery(boolQueryBuilder)
					.execute().actionGet();
		 if(logger.isDebugEnabled()){
			 logger.debug(searchRequestBuilder);	
		 }
		logger.info(searchRequestBuilder);
		if(isRedCheck){
			List<Map<String,Object>> resultMap = toList(searchResponse);
			return this.getSearchResultExcludeRedUserInfo(resultMap);
		} else{
			return toList(searchResponse);
		}
	}

	public List<Map<String, Object>> wildCardFilteredSuggest(boolean isRedCheck,SearchIndexType type, 
			Map<String, Object> fieldNamesAndValues,Map<String, String> fieldNamesAndTypes, long pageIndex, long pageSize) {
		pageIndex = pageIndex - 1;
		//QueryBuilder qb = QueryBuilders.wildcardQuery(filedName, search);
		SearchRequestBuilder searchRequestBuilder =this.createSearchRequestBuilder(type).setSearchType(SearchType.DEFAULT);
		//创建查询 builder
		QueryBuilder boolQueryBuilder = createBoolQuerySuggest(fieldNamesAndValues, fieldNamesAndTypes);
	   
		 SearchResponse searchResponse = searchRequestBuilder
					.setFrom((int)pageIndex * (int)pageSize)
				    .setSize((int)pageSize)
                   .setQuery(boolQueryBuilder)
					.execute().actionGet();
		 if(logger.isDebugEnabled()){
			 logger.debug(searchRequestBuilder);	
		 }
		logger.info(searchRequestBuilder);
		if(isRedCheck){
			List<Map<String,Object>> resultMap = toList(searchResponse);
			return this.getSearchResultExcludeRedUserInfo(resultMap);
		} else{
			return toList(searchResponse);
		}
	}
	
	public List<Map<String,Object>> aggreationFilterQuery(boolean isRedCheck,SearchIndexType type,Map<String, Object> fieldNamesAndValues,
			Map<String, String> fieldNamesAndTypes,@NotNull Map<String,Object> aggreFieldAndValue, long pageIndex, long pageSize){
		pageIndex = pageIndex - 1;
		//QueryBuilder qb = QueryBuilders.wildcardQuery(filedName, search);
		SearchRequestBuilder searchRequestBuilder =this.createSearchRequestBuilder(type).setSearchType(SearchType.DEFAULT);
		//创建查询 builder
		QueryBuilder boolQueryBuilder = createBoolQueryBuilder(fieldNamesAndValues, fieldNamesAndTypes);
		// 聚合查询
		TermsBuilder termsBuilder = null;
		Set<String> fields = aggreFieldAndValue.keySet();
		String terms = null;
		for(String field :fields){
			terms = (String)aggreFieldAndValue.get(field);
			 termsBuilder  = AggregationBuilders
					.terms((String)aggreFieldAndValue.get(field))
					.field(field).size(Integer.MAX_VALUE);
		}
		
		SearchResponse searchResponse = searchRequestBuilder
					.setFrom((int)pageIndex * (int)pageSize).setSize((int)pageSize)
                .setQuery(boolQueryBuilder).addAggregation(termsBuilder)
					.execute().actionGet();
		 if(logger.isDebugEnabled()){
			 logger.debug(searchRequestBuilder);	
		 }
		logger.info(searchRequestBuilder.toString());
		
		return this.getAggregationsResult(searchResponse,terms);
	}

	
	/**
	 * 只获取聚合的值，聚合key和value
	 */
	private List<Map<String, Object>> getAggregationsResult(
			SearchResponse searchResponse, String terms) {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		try {
			if (!StringUtils.isEmpty(terms)) {
				Map<String, Aggregation> aggMap = searchResponse
						.getAggregations().asMap();
				StringTerms stringTerms = (StringTerms) aggMap.get(terms);
				Iterator<Bucket> stringTermsIt = stringTerms.getBuckets()
						.iterator();
				while (stringTermsIt.hasNext()) {
					HashMap<String, Object> resultMapAggregations = new HashMap<String, Object>();
					Bucket stringBucket = stringTermsIt.next();
					resultMapAggregations.put(stringBucket.getKey(),
							stringBucket.getDocCount());
					resultList.add(resultMapAggregations);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}

	/**
	 * 红名单，过滤查询数据信息
	 * @param searchResult
	 * @return
	 */
	private List<Map<String,Object>> getSearchResultExcludeRedUserInfo(List<Map<String,Object>> searchResult){
		if(CollectionUtils.isNotEmpty(searchResult)){
			for(Map<String,Object> map : searchResult){
				//车辆信息过滤
				Set<String> keys = map.keySet();
				if(keys.contains(Constant.PARAM_USER_INFO_CAR_ID)){
					if(redUserInfoService.getRedUserCarIds().contains(map.get(Constant.PARAM_USER_INFO_CAR_ID))){
						for(String key:keys){
							if(Metadata._id.toString().equals(key)){
								continue;
							}else if(Metadata._index.toString().equals(key)){
								continue;
							}else if(Metadata._type.toString().equals(key)){
								continue;
							}else{
								map.put(key, "***");
							}
						}
					}
				}else if (keys.contains(Constant.PARAM_USER_INFO_MAC_ADDRESS)){
					if(redUserInfoService.getRedUserCarIds().contains(map.get(Constant.PARAM_USER_INFO_MAC_ADDRESS))){
						for(String key:keys){
							if(Metadata._id.toString().equals(key)){
								continue;
							}else if(Metadata._index.toString().equals(key)){
								continue;
							}else if(Metadata._type.toString().equals(key)){
								continue;
							}else{
								map.put(key, "***");
							}
						}
					}
				}
			}
			return searchResult;
		}else{
			return searchResult;
		}
	}
}
