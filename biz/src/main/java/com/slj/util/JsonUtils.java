/**
 * Project: avatar-biz
 * 
 * File Created at 2011-8-29
 * $Id$
 * 
 * Copyright 2010 dianping.com.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with dianping.com.
 */
package com.slj.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
public final class JsonUtils {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	static {
		MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
	}

	/**
	 * 将对象转换为JSON格式
	 * 
	 * @param model
	 * @return
	 * @throws IOException
	 */
	public static String toStr(Object model) throws IOException {
		return MAPPER.writeValueAsString(model);
	}

	/**
	 * 将JSON字符串转换为指定类实例
	 *
	 * @param <T>
	 * @param content
	 * @param clazz
	 * @return
	 * @throws IOException
	 */
	public static <T> T fromStr(String content, Class<T> clazz) throws IOException {
		return MAPPER.readValue(content, clazz);
	}

	public static <T> T fromStr(String content, TypeReference<T> ref) throws IOException {
		return MAPPER.readValue(content, ref);
	}

	public static <T> T fromStr(String content, CollectionType ref) throws IOException {
		return MAPPER.readValue(content, ref);
	}
	/**
	 * 将JSON字符串转换为Map
	 *
	 * @param content
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> fromStrToMap(String content) throws IOException {
		return fromStr(content, Map.class);
	}

	/**
	 * 将JSON字符串转换为List
	 *
	 * @param content
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static List<Object> fromStrToList(String content) throws IOException {
		return fromStr(content, List.class);
	}

	public static List parseListSilently(String value){
		try {
			return fromStr(value, List.class);
		} catch (Exception e) {
			log.error("parse json error {}", value, e);
			return Collections.emptyList();
		}
	}

}
