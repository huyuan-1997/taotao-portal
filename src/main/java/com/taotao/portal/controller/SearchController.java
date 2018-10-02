package com.taotao.portal.controller;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.taotao.portal.pojo.SearchResult;
import com.taotao.portal.service.SearchService;

@Controller
public class SearchController {
	@Autowired
	private SearchService searchService;
	@RequestMapping("/search")
	public String search(@RequestParam("q")String queryString,@RequestParam(defaultValue="1")Integer page,Model model){
		//条件不能是空
		if(queryString != null){
			try{
				queryString = new String(queryString.getBytes("iso8859-1"),"utf-8");
			}catch(UnsupportedEncodingException e){
				e.printStackTrace();
			}
		}
		//调用搜索服务
		SearchResult searchResult = searchService.search(queryString,page);
		//向页面传递参数
		model.addAttribute("query",queryString);//设置页面的title
		model.addAttribute("totalPages",searchResult.getPageCount());//设置总记录数
		model.addAttribute("itemList",searchResult.getItemList());
		model.addAttribute("page",page);//默认就只查询第一页
		return "search";
	}
	
}
