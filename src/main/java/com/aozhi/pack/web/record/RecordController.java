/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.aozhi.pack.web.record;

import java.util.Map;

import javax.servlet.ServletRequest;
import javax.validation.Valid;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springside.modules.web.Servlets;

import com.aozhi.pack.entity.Record;
import com.aozhi.pack.entity.User;
import com.aozhi.pack.service.account.ShiroDbRealm.ShiroUser;
import com.aozhi.pack.service.record.RecordService;
import com.google.common.collect.Maps;

/**
 * Record管理的Controller, 使用Restful风格的Urls:
 * 
 * List page : GET /record/
 * Create page : GET /record/create
 * Create action : POST /record/create
 * Update page : GET /record/update/{id}
 * Update action : POST /record/update
 * Delete action : GET /record/delete/{id}
 * 
 * @author calvin
 */
@Controller
@RequestMapping(value = "/record")
public class RecordController {

	private static final String PAGE_SIZE = "3";

	private static Map<String, String> sortTypes = Maps.newLinkedHashMap();
	static {
		sortTypes.put("auto", "自动");
		sortTypes.put("title", "标题");
	}

	@Autowired
	private RecordService recordService;

	@RequestMapping(method = RequestMethod.GET)
	public String list(@RequestParam(value = "page", defaultValue = "1") int pageNumber,
			@RequestParam(value = "page.size", defaultValue = PAGE_SIZE) int pageSize,
			@RequestParam(value = "sortType", defaultValue = "auto") String sortType, Model model,
			ServletRequest request) {
		Map<String, Object> searchParams = Servlets.getParametersStartingWith(request, "search_");
		Long userId = getCurrentUserId();

		Page<Record> records = recordService.getUserRecord(userId, searchParams, pageNumber, pageSize, sortType);

		model.addAttribute("records", records);
		model.addAttribute("sortType", sortType);
		model.addAttribute("sortTypes", sortTypes);
		// 将搜索条件编码成字符串，用于排序，分页的URL
		model.addAttribute("searchParams", Servlets.encodeParameterStringWithPrefix(searchParams, "search_"));

		return "record/recordList";
	}

	@RequestMapping(value = "create", method = RequestMethod.GET)
	public String createForm(Model model) {
		model.addAttribute("record", new Record());
		model.addAttribute("action", "create");
		return "record/recordForm";
	}

	@RequestMapping(value = "create", method = RequestMethod.POST)
	public String create(@Valid Record newRecord, RedirectAttributes redirectAttributes) {
		User user = new User(getCurrentUserId());
		//newRecord.setUser(user);

		recordService.saveRecord(newRecord);
		redirectAttributes.addFlashAttribute("message", "创建任务成功");
		return "redirect:/record/";
	}

	@RequestMapping(value = "update/{id}", method = RequestMethod.GET)
	public String updateForm(@PathVariable("id") Long id, Model model) {
		model.addAttribute("record", recordService.getRecord(id));
		model.addAttribute("action", "update");
		return "record/recordForm";
	}

	@RequestMapping(value = "update", method = RequestMethod.POST)
	public String update(@Valid @ModelAttribute("record") Record record, RedirectAttributes redirectAttributes) {
		recordService.saveRecord(record);
		redirectAttributes.addFlashAttribute("message", "更新任务成功");
		return "redirect:/record/";
	}

	@RequestMapping(value = "delete/{id}")
	public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
		recordService.deleteRecord(id);
		redirectAttributes.addFlashAttribute("message", "删除任务成功");
		return "redirect:/record/";
	}

	/**
	 * 所有RequestMapping方法调用前的Model准备方法, 实现Struts2 Preparable二次部分绑定的效果,先根据form的id从数据库查出Record对象,再把Form提交的内容绑定到该对象上。
	 * 因为仅update()方法的form中有id属性，因此仅在update时实际执行.
	 */
	@ModelAttribute
	public void getRecord(@RequestParam(value = "id", defaultValue = "-1") Long id, Model model) {
		if (id != -1) {
			model.addAttribute("record", recordService.getRecord(id));
		}
	}

	/**
	 * 取出Shiro中的当前用户Id.
	 */
	private Long getCurrentUserId() {
		ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		return user.id;
	}
}
