/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.aozhi.pack.web.packet;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.Validate;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springside.modules.web.Servlets;

import com.aozhi.pack.entity.Packet;
import com.aozhi.pack.service.account.ShiroDbRealm.ShiroUser;
import com.aozhi.pack.service.packet.PacketService;
import com.google.common.collect.Maps;

/**
 * Packet管理的Controller, 使用Restful风格的Urls:
 * 
 * List page : GET /packet/
 * Create page : GET /packet/create
 * Create action : POST /packet/create
 * Update page : GET /packet/update/{id}
 * Update action : POST /packet/update
 * Delete action : GET /packet/delete/{id}
 * 
 */
@Controller
@RequestMapping(value = "/packet")
public class PacketController {

	private static final String PAGE_SIZE = "3";

	private static Map<String, String> sortTypes = Maps.newLinkedHashMap();
	static {
		sortTypes.put("auto", "自动");
		sortTypes.put("packetName", "名称");
	}

	@Autowired
	private PacketService packetService;

	@RequestMapping(method = RequestMethod.GET)
	public String list(@RequestParam(value = "page", defaultValue = "1") int pageNumber,
			@RequestParam(value = "page.size", defaultValue = PAGE_SIZE) int pageSize,
			@RequestParam(value = "sortType", defaultValue = "auto") String sortType, Model model,
			ServletRequest request) {
		Map<String, Object> searchParams = Servlets.getParametersStartingWith(request, "search_");
		Long userId = getCurrentUserId();

		Page<Packet> packets = packetService.getUserPacket(userId, searchParams, pageNumber, pageSize, sortType);

		model.addAttribute("packets", packets);
		model.addAttribute("sortType", sortType);
		model.addAttribute("sortTypes", sortTypes);
		// 将搜索条件编码成字符串，用于排序，分页的URL
		model.addAttribute("searchParams", Servlets.encodeParameterStringWithPrefix(searchParams, "search_"));

		return "packet/packetList";
	}

	@RequestMapping(value = "create", method = RequestMethod.GET)
	public String createForm(Model model) {
		model.addAttribute("packet", new Packet());
		model.addAttribute("action", "create");
		return "packet/packetForm";
	}

	@RequestMapping(value = "create", method = RequestMethod.POST)
	public String create(@Valid Packet newPacket, RedirectAttributes redirectAttributes) {
		packetService.savePacket(newPacket);
		redirectAttributes.addFlashAttribute("message", "添加渠道成功");
		return "redirect:/packet/";
	}

	@RequestMapping(value = "update/{id}", method = RequestMethod.GET)
	public String updateForm(@PathVariable("id") Long id, Model model) {
		model.addAttribute("packet", packetService.getPacket(id));
		model.addAttribute("action", "update");
		return "packet/packetForm";
	}

	@RequestMapping(value = "update", method = RequestMethod.POST)
	public String update(@Valid @ModelAttribute("packet") Packet packet, RedirectAttributes redirectAttributes) {
		packetService.savePacket(packet);
		redirectAttributes.addFlashAttribute("message", "更新渠道成功");
		return "redirect:/packet/";
	}

	@RequestMapping(value = "delete/{id}")
	public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
		packetService.deletePacket(id);
		redirectAttributes.addFlashAttribute("message", "删除渠道成功");
		return "redirect:/packet/";
	}

	/**
	 * 所有RequestMapping方法调用前的Model准备方法, 实现Struts2 Preparable二次部分绑定的效果,先根据form的id从数据库查出Packet对象,再把Form提交的内容绑定到该对象上。
	 * 因为仅update()方法的form中有id属性，因此仅在update时实际执行.
	 */
	@ModelAttribute
	public void getPacket(@RequestParam(value = "id", defaultValue = "-1") Long id, Model model) {
		if (id != -1) {
			model.addAttribute("packet", packetService.getPacket(id));
		}
	}

	/**
	 * 取出Shiro中的当前用户Id.
	 */
	private Long getCurrentUserId() {
		ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		return user.id;
	}
	
	/**
	 * Ajax请求校验channelName是否唯一。
	 */
	@RequestMapping(value = "checkPacketName")
	@ResponseBody 
	public String checkChannelName(@RequestParam("packetName") String packetlName) {
		if (packetService.findByPacketName(packetlName) == null) {
			return "true";
		} else {
			return "false";
		}
	}
	public static Map<String, Object> getParametersStartingWith(ServletRequest request, String prefix) {
		Validate.notNull(request, "Request must not be null");
		String paramValue = request.getParameter("search_LIKE_packetName");  
		Map<String, Object> params = new TreeMap<String, Object>();
		try {
			if (paramValue!=null) {
				
				paramValue = new String(paramValue.trim().getBytes("ISO-8859-1"), "utf-8");
				params.put("LIKE_packetName", paramValue);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
		
		return params;
	}

}
