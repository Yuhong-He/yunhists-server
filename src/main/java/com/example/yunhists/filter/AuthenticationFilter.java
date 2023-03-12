package com.example.yunhists.filter;

import com.alibaba.fastjson2.JSON;
import com.example.yunhists.common.BaseContext;
import com.example.yunhists.common.Result;
import com.example.yunhists.entity.User;
import com.example.yunhists.enumeration.ResultCodeEnum;
import com.example.yunhists.service.UserService;
import com.example.yunhists.utils.AuthenticationPathHelper;
import com.example.yunhists.utils.JwtHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = "/*")
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthenticationFilter implements Filter {

    @Autowired
    private UserService userService;

    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 1. Get request url and auth path
        String requestURI = request.getRequestURI();
        String[] unregistered = AuthenticationPathHelper.unregistered();

        // 2. Check the request is for unregistered user
        if(check(unregistered, requestURI)){
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Check login and authentication
        if ("OPTIONS".equals(request.getMethod())) { // https://blog.csdn.net/wxw1997a/article/details/106472081
            filterChain.doFilter(request, servletResponse);
        } else {
            String token = request.getHeader("token");

            // a. check token exist
            if(token != null && !token.isEmpty()) {

                // b. check token expired
                if(!JwtHelper.isExpiration(token)) {
                    try{
                        // c. check token valid
                        Long userId = JwtHelper.getUserId(token);

                        // d. check userid exist (valid token should contain a valid userid)
                        if(userId != null) {
                            Integer userIdInt = Math.toIntExact(userId);
                            User user = userService.getUserById(userIdInt);

                            // e. check user exist
                            if(user != null) {
                                BaseContext.setCurrentId(userId);
                                filterChain.doFilter(request, response);

                            } else {
                                response.getWriter().write(JSON.toJSONString(Result.error(ResultCodeEnum.NO_USER)));
                            }
                        } else {
                            response.getWriter().write(JSON.toJSONString(Result.error(ResultCodeEnum.TOKEN_ERROR)));
                        }
                    } catch (Exception e) {
                        response.getWriter().write(JSON.toJSONString(Result.error(ResultCodeEnum.TOKEN_ERROR)));
                    }
                } else {
                    response.getWriter().write(JSON.toJSONString(Result.error(ResultCodeEnum.TOKEN_EXPIRED)));
                }
            } else {
                response.getWriter().write(JSON.toJSONString(Result.error(ResultCodeEnum.MISS_TOKEN)));
            }
        }

    }

    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            if(PATH_MATCHER.match(url, requestURI)){
                return true;
            }
        }
        return false;
    }
}
