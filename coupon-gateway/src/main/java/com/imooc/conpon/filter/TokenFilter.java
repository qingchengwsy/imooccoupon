package com.imooc.conpon.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 校验请求中传递的 token
 */
@Slf4j
//@Component
public class TokenFilter extends AbstractPreZuuleFilter {
    @Override
    protected Object cRun() {
        HttpServletRequest request=requestContext.getRequest();
        log.info(String.format("%s request to %s",
                request.getMethod(),request.getRequestURL().toString()));
        Object token=request.getParameter("token");
        if(token==null){
            log.error("error: token is empty");
            return fail(401,"error: token is empty");
        }
        return success();
    }

    @Override
    public int filterOrder() {
        return 1;
    }
}
