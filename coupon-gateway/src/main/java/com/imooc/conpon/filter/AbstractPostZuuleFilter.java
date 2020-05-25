package com.imooc.conpon.filter;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

public abstract class AbstractPostZuuleFilter extends AbstractZuuleFilter{
    @Override
    public String filterType() {
        return FilterConstants.POST_TYPE;
    }
}
