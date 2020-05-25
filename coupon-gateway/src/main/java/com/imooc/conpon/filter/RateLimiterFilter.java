package com.imooc.conpon.filter;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 限流过滤器
 */
@Slf4j
@Component
@SuppressWarnings("all") //忽略所有错误
public class RateLimiterFilter extends AbstractPreZuuleFilter {

     //每秒可以获得两个令牌,限流器
        RateLimiter rateLimiter=RateLimiter.create(2.0);

        protected Object cRun() {
            HttpServletRequest request=requestContext.getRequest();
            if (rateLimiter.tryAcquire()){//尝试去获取令牌
                log.info("get rate token success");
                return success();
            }else {
                log.error("rate limit {}",request.getRequestURI());
                return fail(402,"error: rate limit");
            }
    }

    @Override
    public int filterOrder() {
        return 2;
    }
}
