package com.imooc.conpon.filter;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

public abstract class AbstractPreZuuleFilter extends AbstractZuuleFilter

{
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }
}
