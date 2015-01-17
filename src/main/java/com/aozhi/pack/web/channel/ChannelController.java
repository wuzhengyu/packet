/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.aozhi.pack.web.channel;

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

import com.aozhi.pack.entity.Channel;
import com.aozhi.pack.service.account.ShiroDbRealm.ShiroUser;
import com.aozhi.pack.service.channel.ChannelService;
import com.google.common.collect.Maps;

/**
 * Channel管理的Controller, 使用Restful风格的Urls:
 * 
 * List page : GET /channel/
 * Create page : GET /channel/create
 * Create action : POST /channel/create
 * Update page : GET /channel/update/{id}
 * Update action : POST /channel/update
 * Delete action : GET /channel/delete/{id}
 * 
 */
@Controller
@RequestMapping(value = "/channel")
public class ChannelController {

	private static final String PAGE_SIZE = "3";

	private static Map<String, String> sortTypes = Maps.newLinkedHashMap();
	static {
		sortTypes.put("auto", "自动");
		sortTypes.put("channelName", "名称");
	}

	@Autowired
	private ChannelService channelService;

	@RequestMapping(method = RequestMethod.GET)
	public String list(@RequestParam(value = "page", defaultValue = "1") int pageNumber,
			@RequestParam(value = "page.size", defaultValue = PAGE_SIZE) int pageSize,
			@RequestParam(value = "sortType", defaultValue = "auto") String sortType, Model model,
			ServletRequest request) {
		Map<String, Object> searchParams = Servlets.getParametersStartingWith(request, "search_");
		Long userId = getCurrentUserId();

		Page<Channel> channels = channelService.getUserChannel(userId, searchParams, pageNumber, pageSize, sortType);

		model.addAttribute("channels", channels);
		model.addAttribute("sortType", sortType);
		model.addAttribute("sortTypes", sortTypes);
		// 将搜索条件编码成字符串，用于排序，分页的URL
		model.addAttribute("searchParams", Servlets.encodeParameterStringWithPrefix(searchParams, "search_"));

		return "channel/channelList";
	}

	@RequestMapping(value = "create", method = RequestMethod.GET)
	public String createForm(Model model) {
		model.addAttribute("channel", new Channel());
		model.addAttribute("action", "create");
		return "channel/channelForm";
	}

	@RequestMapping(value = "create", method = RequestMethod.POST)
	public String create(@Valid Channel newChannel, RedirectAttributes redirectAttributes) {
		channelService.saveChannel(newChannel);
		redirectAttributes.addFlashAttribute("message", "添加渠道成功");
		return "redirect:/channel/";
	}

	@RequestMapping(value = "update/{id}", method = RequestMethod.GET)
	public String updateForm(@PathVariable("id") Long id, Model model) {
		model.addAttribute("channel", channelService.getChannel(id));
		model.addAttribute("action", "update");
		return "channel/channelForm";
	}

	@RequestMapping(value = "update", method = RequestMethod.POST)
	public String update(@Valid @ModelAttribute("channel") Channel channel, RedirectAttributes redirectAttributes) {
		channelService.saveChannel(channel);
		redirectAttributes.addFlashAttribute("message", "更新渠道成功");
		return "redirect:/channel/";
	}

	@RequestMapping(value = "delete/{id}")
	public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
		channelService.deleteChannel(id);
		redirectAttributes.addFlashAttribute("message", "删除渠道成功");
		return "redirect:/channel/";
	}

	/**
	 * 所有RequestMapping方法调用前的Model准备方法, 实现Struts2 Preparable二次部分绑定的效果,先根据form的id从数据库查出Channel对象,再把Form提交的内容绑定到该对象上。
	 * 因为仅update()方法的form中有id属性，因此仅在update时实际执行.
	 */
	@ModelAttribute
	public void getChannel(@RequestParam(value = "id", defaultValue = "-1") Long id, Model model) {
		if (id != -1) {
			model.addAttribute("channel", channelService.getChannel(id));
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
	@RequestMapping(value = "checkChannelName")
	@ResponseBody
	public String checkChannelName(@RequestParam("channelName") String channelName) {
		System.out.println("channelName : "+channelName);
		if (channelService.findByChannelName(channelName) == null) {
			return "true";
		} else {
			return "false";
		}
	}
	public static Map<String, Object> getParametersStartingWith(ServletRequest request, String prefix) {
		Validate.notNull(request, "Request must not be null");
		String paramValue = request.getParameter("search_LIKE_channelName");  
		Map<String, Object> params = new TreeMap<String, Object>();
		try {
			if (paramValue!=null) {
				
				paramValue = new String(paramValue.trim().getBytes("ISO-8859-1"), "utf-8");
				params.put("LIKE_channelName", paramValue);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
		
		return params;
	}

}
