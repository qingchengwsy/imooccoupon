package com.imooc.conpon.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 在过滤器中存取客户端发起请求的时间戳
 */
@Slf4j
@Component
public class PreRequestFilter extends AbstractPreZuuleFilter {
    @Override
    protected Object cRun() {
        requestContext.set("startTime",System.currentTimeMillis());
        return success();
    }

    @Override
    public int filterOrder() {
        return 0;
    }
}
