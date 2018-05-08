package com.xinyi.xinfo.search.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 功能说明：返回系统表的字段信息
 * 
 * ArchivesField.java
 * 
 * Original Author: zefeng.wang,2015年8月25日
 *
 * Copyright (C)1997-2014 深圳小树盛凯科技 All rights reserved.
 */
@Table(name = "tab_search_app_meta")
public class SearchAppMeta implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// 主键 
	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "res_id")
	private String resId;
	
	@Column(name = "field_name")
	private String fieldName;

	@Column(name = "field_desc")
	private String fieldDesc;

	@Column(name = "field_type")
	private String fieldType;
	
	@Column(name = "field_order")
	private String fieldOrder;
	
	@Column(name = "row_num")
	private String rowNum;
	
	@Column(name = "dic_id")
	private String dicId;
	
	@Column(name = "dic_flag")
	private String dicFlag;
	
	@Column(name = "arc_list_flag")
	private String arcListFlag;
	
	@Column(name = "arc_dtl_flag")
	private String arcDtlFlag;
	
	@Column(name = "arc_isvalid_flag")
	private String arcIsvalidFlag;
	
	@Column(name = "arc_href_flag")
	private String arcHrefFlag;
	
	@Column(name = "arc_pk_flag")
	private String arcPkFlag;
	
	@Column(name = "arc_view_flag")
	private String arcViewFlag;
	
	@Column(name = "es_high_flag")
	private String esHighFlag;
	
	@Column(name = "es_view_flag")
	private String esViewFlag;
	
	@Column(name = "es_min")
	private String esMin;
	
	@Column(name = "es_max")
	private String esMax;
	
	@Column(name = "es_arckey_flag")
	private String esArckeyFlag;
	
	@Column(name = "es_dtl_flag")
	private String esDtlFlag;
	
	@Column(name = "es_index_flag")
	private String esIndexFlag;
	
	@Column(name = "es_index_score")
	private String esIndexScore;
	
	@Column(name = "es_standard_field")
	private String esStandardField;
	
	@Column(name = "es_pk_flag")
	private String esPkFlag;
	
	@Column(name = "field_regex")
	private String fieldRegex;
	
	public String getFieldRegex() {
		return fieldRegex;
	}

	public void setFieldRegex(String fieldRegex) {
		this.fieldRegex = fieldRegex;
	}

	private String esFieldIa	;
	private String esFieldSa	;
	private String esFieldInAll	;
	private String esFieldAnalyze;
	private String esFieldType   ;
	@Column(name = "es_field_store")
	private String esFieldStore ;

	public String getEsFieldStore() {
		return esFieldStore;
	}

	public void setEsFieldStore(String esFieldStore) {
		this.esFieldStore = esFieldStore;
	}

	@Column(name = "sfcx")
	private String sfcx ;
	
	@Column(name = "arc_list_order")
	private int arcListOrder ;
	

	public int getArcListOrder() {
		return arcListOrder;
	}

	public void setArcListOrder(int arcListOrder) {
		this.arcListOrder = arcListOrder;
	}

	public String getSfcx() {
		return sfcx;
	}

	public void setSfcx(String sfcx) {
		this.sfcx = sfcx;
	}

	public String getEsFieldIa() {
		return esFieldIa;
	}

	public void setEsFieldIa(String esFieldIa) {
		this.esFieldIa = esFieldIa;
	}

	public String getEsFieldSa() {
		return esFieldSa;
	}

	public void setEsFieldSa(String esFieldSa) {
		this.esFieldSa = esFieldSa;
	}

	public String getEsFieldInAll() {
		return esFieldInAll;
	}

	public void setEsFieldInAll(String esFieldInAll) {
		this.esFieldInAll = esFieldInAll;
	}

	public String getEsFieldAnalyze() {
		return esFieldAnalyze;
	}

	public void setEsFieldAnalyze(String esFieldAnalyze) {
		this.esFieldAnalyze = esFieldAnalyze;
	}

	public String getEsFieldType() {
		return esFieldType;
	}

	public void setEsFieldType(String esFieldType) {
		this.esFieldType = esFieldType;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public String getFieldDesc() {
		return fieldDesc;
	}

	public void setFieldDesc(String fieldDesc) {
		this.fieldDesc = fieldDesc;
	}

	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getResId() {
		return resId;
	}

	public void setResId(String resId) {
		this.resId = resId;
	}

	public String getFieldOrder() {
		return fieldOrder;
	}

	public void setFieldOrder(String fieldOrder) {
		this.fieldOrder = fieldOrder;
	}

	public String getRowNum() {
		return rowNum;
	}

	public void setRowNum(String rowNum) {
		this.rowNum = rowNum;
	}

	public String getDicId() {
		return dicId;
	}

	public void setDicId(String dicId) {
		this.dicId = dicId;
	}

	public String getDicFlag() {
		return dicFlag;
	}

	public void setDicFlag(String dicFlag) {
		this.dicFlag = dicFlag;
	}

	public String getArcListFlag() {
		return arcListFlag;
	}

	public void setArcListFlag(String arcListFlag) {
		this.arcListFlag = arcListFlag;
	}

	public String getArcDtlFlag() {
		return arcDtlFlag;
	}

	public void setArcDtlFlag(String arcDtlFlag) {
		this.arcDtlFlag = arcDtlFlag;
	}

	public String getArcIsvalidFlag() {
		return arcIsvalidFlag;
	}

	public void setArcIsvalidFlag(String arcIsvalidFlag) {
		this.arcIsvalidFlag = arcIsvalidFlag;
	}

	public String getArcHrefFlag() {
		return arcHrefFlag;
	}

	public void setArcHrefFlag(String arcHrefFlag) {
		this.arcHrefFlag = arcHrefFlag;
	}

	public String getArcPkFlag() {
		return arcPkFlag;
	}

	public void setArcPkFlag(String arcPkFlag) {
		this.arcPkFlag = arcPkFlag;
	}



	public String getArcViewFlag() {
		return arcViewFlag;
	}

	public void setArcViewFlag(String arcViewFlag) {
		this.arcViewFlag = arcViewFlag;
	}

	public String getEsHighFlag() {
		return esHighFlag;
	}

	public void setEsHighFlag(String esHighFlag) {
		this.esHighFlag = esHighFlag;
	}

	public String getEsViewFlag() {
		return esViewFlag;
	}

	public void setEsViewFlag(String esViewFlag) {
		this.esViewFlag = esViewFlag;
	}

	public String getEsMin() {
		return esMin;
	}

	public void setEsMin(String esMin) {
		this.esMin = esMin;
	}

	public String getEsMax() {
		return esMax;
	}

	public void setEsMax(String esMax) {
		this.esMax = esMax;
	}

	public String getEsArckeyFlag() {
		return esArckeyFlag;
	}

	public void setEsArckeyFlag(String esArckeyFlag) {
		this.esArckeyFlag = esArckeyFlag;
	}

	public String getEsDtlFlag() {
		return esDtlFlag;
	}

	public void setEsDtlFlag(String esDtlFlag) {
		this.esDtlFlag = esDtlFlag;
	}

	public String getEsIndexFlag() {
		return esIndexFlag;
	}

	public void setEsIndexFlag(String esIndexFlag) {
		this.esIndexFlag = esIndexFlag;
	}

	public String getEsIndexScore() {
		return esIndexScore;
	}

	public void setEsIndexScore(String esIndexScore) {
		this.esIndexScore = esIndexScore;
	}

	public String getEsStandardField() {
		return esStandardField;
	}

	public void setEsStandardField(String esStandardField) {
		this.esStandardField = esStandardField;
	}

	public String getEsPkFlag() {
		return esPkFlag;
	}

	public void setEsPkFlag(String esPkFlag) {
		this.esPkFlag = esPkFlag;
	}
		
}

