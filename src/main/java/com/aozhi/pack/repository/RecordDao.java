/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.aozhi.pack.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.aozhi.pack.entity.Packet;
import com.aozhi.pack.entity.Record;
import com.aozhi.pack.entity.Task;

public interface RecordDao extends PagingAndSortingRepository<Record, Long>, JpaSpecificationExecutor<Record> {

	Packet findByPacketName(String packetName);

	@Query("select * from Record record where record.channel.id=?1")
	Page<Record> findByChannelId(Long id, Pageable pageRequest);
}
