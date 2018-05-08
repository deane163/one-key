package com.xinyi.xframe.dubbo_onekey_api.service;

import java.util.List;
import java.util.Map;

import com.xinyi.xframe.dubbo_onekey_api.model.ArchivesSearchCriteria;
import com.xinyi.xframe.dubbo_onekey_api.model.ArchivesSearchResult;



public interface ArchivesSearchService {


	List<ArchivesSearchResult> searchArchives(List<ArchivesSearchCriteria> serachCriteriaList);
	
	long getArchviesCount(ArchivesSearchCriteria searchCriteria);

	List<Map<String, Object>> searchArchives(ArchivesSearchCriteria searchCriteria);
}
