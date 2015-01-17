/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.aozhi.pack.service.channel;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springside.modules.persistence.DynamicSpecifications;
import org.springside.modules.persistence.SearchFilter;

import com.aozhi.pack.entity.Channel;
import com.aozhi.pack.repository.ChannelDao;

// Spring Bean的标识.
@Component
// 类中所有public函数都纳入事务管理的标识.
@Transactional
public class ChannelService {

	private ChannelDao channelDao;

	public Channel getChannel(Long id) {
		return channelDao.findOne(id);
	}

	public void saveChannel(Channel entity) {
		channelDao.save(entity);
	}

	public void deleteChannel(Long id) {
		channelDao.delete(id);
	}
	
	public Channel findByChannelName(String channelName) {
		return channelDao.findByChannelName(channelName);
	}

	public List<Channel> getAllChannel() {
		return (List<Channel>) channelDao.findAll();
	}

	public Page<Channel> getUserChannel(Long userId, Map<String, Object> searchParams, int pageNumber, int pageSize,
			String sortType) {
		PageRequest pageRequest = buildPageRequest(pageNumber, pageSize, sortType);
		Specification<Channel> spec = buildSpecification(userId, searchParams);

		return channelDao.findAll(spec, pageRequest);
	}

	/**
	 * 创建分页请求.
	 */
	private PageRequest buildPageRequest(int pageNumber, int pagzSize, String sortType) {
		Sort sort = null;
		if ("auto".equals(sortType)) {
			sort = new Sort(Direction.DESC, "id");
		} else if ("channelName".equals(sortType)) {
			sort = new Sort(Direction.ASC, "channelName");
		}

		return new PageRequest(pageNumber - 1, pagzSize, sort);
	}

	/**
	 * 创建动态查询条件组合.
	 */
	private Specification<Channel> buildSpecification(Long userId, Map<String, Object> searchParams) {
		Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
		// filters.put("user.id", new SearchFilter("user.id", Operator.EQ, userId));
		Specification<Channel> spec = DynamicSpecifications.bySearchFilter(filters.values(), Channel.class);
		return spec;
	}

	@Autowired
	public void setChannelDao(ChannelDao taskDao) {
		this.channelDao = taskDao;
	}
}
