/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.aozhi.pack.service.packet;

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

import com.aozhi.pack.entity.Packet;
import com.aozhi.pack.repository.PacketDao;

// Spring Bean的标识.
@Component
// 类中所有public函数都纳入事务管理的标识.
@Transactional
public class PacketService {

	private PacketDao packetDao;

	public Packet getPacket(Long id) {
		return packetDao.findOne(id);
	}

	public void savePacket(Packet entity) {
		packetDao.save(entity);
	}

	public void deletePacket(Long id) {
		packetDao.delete(id);
	}

	public List<Packet> getAllPacket() {
		return (List<Packet>) packetDao.findAll();
	}
	
	public Packet findByPacketName(String packetName) {
		return packetDao.findByPacketName(packetName);
	}

	public Page<Packet> getUserPacket(Long userId, Map<String, Object> searchParams, int pageNumber, int pageSize,
			String sortType) {
		PageRequest pageRequest = buildPageRequest(pageNumber, pageSize, sortType);
		Specification<Packet> spec = buildSpecification(userId, searchParams);

		return packetDao.findAll(spec, pageRequest);
	}

	/**
	 * 创建分页请求.
	 */
	private PageRequest buildPageRequest(int pageNumber, int pagzSize, String sortType) {
		Sort sort = null;
		if ("auto".equals(sortType)) {
			sort = new Sort(Direction.DESC, "id");
		} else if ("packetName".equals(sortType)) {
			sort = new Sort(Direction.ASC, "packetName");
		}

		return new PageRequest(pageNumber - 1, pagzSize, sort);
	}

	/**
	 * 创建动态查询条件组合.
	 */
	private Specification<Packet> buildSpecification(Long userId, Map<String, Object> searchParams) {
		Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
		// filters.put("user.id", new SearchFilter("user.id", Operator.EQ, userId));
		Specification<Packet> spec = DynamicSpecifications.bySearchFilter(filters.values(), Packet.class);
		return spec;
	}

	@Autowired
	public void setPacketDao(PacketDao taskDao) {
		this.packetDao = taskDao;
	}
}
