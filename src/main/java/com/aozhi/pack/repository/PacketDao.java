/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.aozhi.pack.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.aozhi.pack.entity.Packet;

public interface PacketDao extends PagingAndSortingRepository<Packet, Long>, JpaSpecificationExecutor<Packet> {

	Packet findByPacketName(String packetName);
}
