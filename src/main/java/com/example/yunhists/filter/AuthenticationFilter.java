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
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

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
        // unregisteredRights < bannedRights < userRights < adminRights
        String[] unregisteredRights = AuthenticationPathHelper.unregistered();
        String[] bannedRights = AuthenticationPathHelper.banned();
        String[] userRights = AuthenticationPathHelper.user();
        String[] adminRights = AuthenticationPathHelper.admin();

        // 2. If the request is for unregistered user
        if(match(unregisteredRights, requestURI)){
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Check the request belonging which user rights group
        String rights = "";
        if(match(bannedRights, requestURI)) {
            rights = "Banned";
        } else if(match(userRights, requestURI)) {
            rights = "User";
        } else if(match(adminRights, requestURI)) {
            rights = "Admin";
        }

        // 4. Check login and authentication
        if ("OPTIONS".equals(request.getMethod())) { // https://blog.csdn.net/wxw1997a/article/details/106472081
            filterChain.doFilter(request, servletResponse);
        } else {
            String token = request.getHeader("token");

            // a. check token exist
            if(token != null && !token.isEmpty()) {

                // b. check token expired
                if(!JwtHelper.isExpiration(token)) {

                    // c. check token valid
                    Long userId = JwtHelper.getUserId(token);

                    // d. check userId exist (valid token should contain a valid userid)
                    if(userId != null) {
                        Integer userIdInt = Math.toIntExact(userId);
                        User user = userService.getUserById(userIdInt);

                        // e. check user exist
                        if(user != null) {

                            // f. check user rights
                            switch (rights) {
                                case "Admin":
                                    if (user.getUserRights() >= 1) {
                                        BaseContext.setCurrentId(userId);
                                        filterChain.doFilter(request, response);
                                    } else {
                                        action(response, Result.error(ResultCodeEnum.NO_PERMISSION));
                                    }
                                    break;
                                case "User":
                                    if (user.getUserRights() >= 0) {
                                        BaseContext.setCurrentId(userId);
                                        filterChain.doFilter(request, response);
                                    } else {
                                        action(response, Result.error(ResultCodeEnum.NO_PERMISSION));
                                    }
                                    break;
                                case "Banned":
                                    BaseContext.setCurrentId(userId);
                                    filterChain.doFilter(request, response);
                                    break;
                                default:
                                    action(response, Result.error(ResultCodeEnum.NOT_REGISTERED_API));
                                    break;
                            }
                        } else {
                            action(response, Result.error(ResultCodeEnum.NO_USER));
                        }
                    } else {
                        action(response, Result.error(ResultCodeEnum.TOKEN_ERROR));
                    }
                } else {
                    action(response, Result.error(ResultCodeEnum.TOKEN_EXPIRED));
                }
            } else {
                action(response, Result.error(ResultCodeEnum.MISS_TOKEN));
            }
        }
    }

    public boolean match(String[] urls, String requestURI) {
        for (String url : urls) {
            if(PATH_MATCHER.match(url, requestURI)){
                return true;
            }
        }
        return false;
    }

    public void action(HttpServletResponse response, Result<Object> result) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "token, Content-Type");
        response.setStatus(200);
        response.setContentType("application/json; charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        String resultJson = JSON.toJSON(result).toString();
        OutputStream out = response.getOutputStream();
        out.write(resultJson.getBytes(StandardCharsets.UTF_8));
        out.flush();
    }
}
