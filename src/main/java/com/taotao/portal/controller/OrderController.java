package com.taotao.portal.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.taotao.pojo.TbUser;
import com.taotao.portal.pojo.CartItem;
import com.taotao.portal.pojo.Order;
import com.taotao.portal.service.CartService;
import com.taotao.portal.service.OrderService;

@Controller
@RequestMapping("/order")
public class OrderController {
	@Autowired
	private CartService cartService;
	@Autowired
	private OrderService orderService;
	/**
	 * 跳转到订单页面
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping("/order-cart")
	public String showOrderCart(HttpServletRequest request, HttpServletResponse response, Model model) {
		// 取购物车商品列表
		List<CartItem> list = cartService.getCartItemList(request, response);
		// 传递给页面
		model.addAttribute("cartList", list);
		return "order-cart";
	}
	@RequestMapping("/create")
	public String createOrder(Order order,Model model,HttpServletRequest request){
		try{
			//从request中取用户信息
			TbUser user = (TbUser) request.getAttribute("user");
			order.setUserId(user.getId());
			order.setBuyerNick(user.getUsername());
			//调用服务
			String orderId = orderService.createOrder(order);
			model.addAttribute("orderId",orderId);
			model.addAttribute("payment",order.getPayment());
			model.addAttribute("date", new DateTime().plusDays(3).toString("yyyy-MM-dd"));
			return "success";
		}catch(Exception e){
			e.getStackTrace();
			model.addAttribute("message", "订单创建失败");
			return "error/exception";
		}
	}
	

}
