package com.imooc.conpon.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;


/**
 * 通用的抽象过滤器类
 */
public abstract class AbstractZuuleFilter extends ZuulFilter {

    //用于过滤器之间传递消息,数据保存在每个请求的 ThreadLocal 中
    //扩展了Map
    RequestContext requestContext;

    private final static String NEXT= "next";

    @Override
    public boolean shouldFilter() {
        RequestContext  ctx=RequestContext.getCurrentContext();
        return (Boolean)ctx.getOrDefault(NEXT,true);
    }

    @Override
    public Object run() throws ZuulException {
        requestContext=RequestContext.getCurrentContext();
        return cRun();
    }

    protected abstract Object cRun();

    Object fail(int code,String msg){
        requestContext.set(NEXT,false);
        requestContext.setSendZuulResponse(false);
        requestContext.getResponse().setContentType("text/html;charset=utf-8");
        requestContext.setResponseStatusCode(code);
        requestContext.setResponseBody(String.format("{\"result\": \"%s!\"}",msg));
        return null;
    }

    Object success(){
        requestContext.set(NEXT,true);
        return null;
    }
}
