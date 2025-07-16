package com.moneydiary.backend.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moneydiary.backend.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    private ObjectMapper objectMapper=new ObjectMapper();

        @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        AntPathMatcher pathMatcher = new AntPathMatcher();

        log.info(request.getRequestURI());
        if(whiteList(request, pathMatcher)){
            return true;
        }

        if(session == null || session.getAttribute("user")==null){
            log.info("사용자 세션 만료 uri={} ] method={}",request.getRequestURI(),request.getMethod());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            ApiResponse apiResponse=new ApiResponse(false,"인증이 만료 되었습니다.");
            String result = objectMapper.writeValueAsString(apiResponse);
            response.getWriter().write(result);
            return false;
        }
        return true;
    }

    private boolean whiteList(HttpServletRequest request, AntPathMatcher pathMatcher) {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        //현재는 제외 패턴 많이 없음..늘어나면 배열로 관리..

        if(method.equals("OPTIONS"))return true;
        if(pathMatcher.match("/users/*",uri) && (method.equals("OPTIONS") || method.equals("POST"))){
            log.info(" uri={} | method={} |인증 제외 패턴",uri,method);
            return true;
        }
        return false;
    }
}
