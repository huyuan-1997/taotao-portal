package com.taotao.portal.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.common.utils.CookieUtils;
import com.taotao.pojo.TbUser;
import com.taotao.portal.service.impl.UserServiceImpl;

public class LoginInterceptor implements HandlerInterceptor {
	@Autowired 
	private UserServiceImpl userService;
	//现在是拦截Item商品界面
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// TODO Auto-generated method stub
		//从cookie中取token
		String token = CookieUtils.getCookieValue(request,"TT_TOKEN");
		//调用service层根据token取用户信息
		TbUser user = userService.getUserByToken(token);
		//取不到跳转到登录界面，将用户的url传递给登录界面
		if(null==user){
			response.sendRedirect(userService.SSO_BASE_URL + userService.SSO_PAGE_LOGIN + "?redirect=" + request.getRequestURL());;
			return false;
		}
		//取到用户信息
		request.setAttribute("user", user);//在订单系统中有作用
		return true;
		
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub

	}

}
