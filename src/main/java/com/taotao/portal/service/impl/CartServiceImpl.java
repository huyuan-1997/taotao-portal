package com.taotao.portal.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.CookieUtils;
import com.taotao.common.utils.HttpClientUtil;
import com.taotao.common.utils.JsonUtils;
import com.taotao.pojo.TbItem;
import com.taotao.portal.pojo.CartItem;
import com.taotao.portal.service.CartService;

@Service
public class CartServiceImpl implements CartService {
	@Value("${REST_BASE_URL}")
	private String REST_BASE_URL;
	@Value("${ITME_INFO_URL}")
	private String ITME_INFO_URL;

	/**
	 * 添加购物车商品
	 */
	@Override
	public TaotaoResult addCartItem(Long itemId, Integer num, HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		// 取商品的信息
		CartItem cartItem = null;
		// 取购物车商品列表
		List<CartItem> itemList = getCartItemList(request);
		// 判断商品是否存在
		for (CartItem cItem : itemList) {
			// 如果存在这个商品，数量增加一
			if (cItem.getId() == itemId) {
				// 数量增加一
				cItem.setNum(cItem.getNum() + num);
				cartItem = cItem;
				break;
			}
		}
		//购物车没有这个信息，就将他写入到购物车里
		if (cartItem == null) {
			cartItem = new CartItem();
			// 根据商品id查询商品基本信息。
			String json = HttpClientUtil.doGet(REST_BASE_URL + ITME_INFO_URL + itemId);
			// 把json转换成java对象
			TaotaoResult taotaoResult = TaotaoResult.formatToPojo(json, TbItem.class);
			if (taotaoResult.getStatus() == 200) {
				TbItem item = (TbItem) taotaoResult.getData();
				cartItem.setId(item.getId());
				cartItem.setTitle(item.getTitle());
				cartItem.setImage(item.getImage() == null ? "" : item.getImage().split(",")[0]);
				cartItem.setNum(num);
				cartItem.setPrice(item.getPrice());
			}
			// 添加到购物车列表
			itemList.add(cartItem);
		}
		// 将购物车列表写入cookie中
		CookieUtils.setCookie(request, response, "TT_CART", JsonUtils.objectToJson(itemList), true);
		return TaotaoResult.ok();
	}

	/**
	 * 从cookie中取商品列表
	 * 
	 * @param request
	 * @return
	 */
	private List<CartItem> getCartItemList(HttpServletRequest request) {
		// TODO Auto-generated method stub
		// 从Cookie中取商品列表,第三参数是否要进行解码，为了让其他人看不懂
		String cartJson = CookieUtils.getCookieValue(request, "TT_CART", true);
		if (cartJson == null) {
			return new ArrayList<>();
		}
		// 将json转化成商品列表
		// 把json转换成商品列表
		try {
			List<CartItem> list = JsonUtils.jsonToList(cartJson, CartItem.class);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ArrayList<>();
	}
	/**
	 * 取车商品购物车列表
	 */
	@Override
	public List<CartItem> getCartItemList(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		List<CartItem> itemList = getCartItemList(request);
		return itemList;
	}
	/**
	 * 删除商品
	 */
	public TaotaoResult deleteCartItem(Long itemId,HttpServletRequest request, HttpServletResponse response){
		//从cookie中取出商品列表
		List<CartItem> itemList = getCartItemList(request);
		//从商品列表中移除然后重新写入到cookie
		for (CartItem cartItem : itemList) {
			if (cartItem.getId() == itemId) {
				itemList.remove(cartItem);
				break;
			}

		}
		//将购物车列表重新写入到cookie中
		CookieUtils.setCookie(request, response, "TT_CART", JsonUtils.objectToJson(itemList),true);
		return TaotaoResult.ok();	
	}

	

}
