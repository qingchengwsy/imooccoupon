package com.imooc.distribution.advice;

import com.imooc.distribution.annotation.IgnoreResponseAdvice;
import com.imooc.distribution.vo.CommonResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * <h1>统一响应</h1>
 */
@RestControllerAdvice   //对所有controller请求进行拦截
public class CommonResponseDataAdvice implements ResponseBodyAdvice<Object> {

    /**
     *<h1>判断是否对响应进行处理</h1>
     * @param methodParameter
     * @param aClass
     * @return
     */
    @SuppressWarnings("all")
    public boolean supports(MethodParameter methodParameter,  //当前controller方法的定义
                            Class<? extends HttpMessageConverter<?>> aClass) {
        //如果当前方法所在的类标识了@IgnoreResponseAdvice 注解,不需要处理
        if(methodParameter.getDeclaringClass().isAnnotationPresent(IgnoreResponseAdvice.class)){
            return false;
        }
        //如果当前方法标识了@IgnoreResponseAdvice 注解,不需要处理
        if (methodParameter.getMethod().isAnnotationPresent(IgnoreResponseAdvice.class)){
            return false;
        }
        //对响应进行处理 执行 beforeBodyWrite 方法
        return true;
    }

    /**
     *<h2>响应之前进行处理beforeBodyWrite</h2>
     */
    @SuppressWarnings("all")
    public Object beforeBodyWrite(Object o,   //方法的响应对象  ,响应之前进行处理beforeBodyWrite
                                  MethodParameter methodParameter,
                                  MediaType mediaType,
                                  Class<? extends HttpMessageConverter<?>> aClass,
                                  ServerHttpRequest serverHttpRequest,
                                  ServerHttpResponse serverHttpResponse) {
        //定义最终的返回对象
        CommonResponse<Object> response=new CommonResponse<>(0,"");
        //如果 o 是 null, 不需要设置data
        if(o==null){
            return response;
            //如果 o 已经是CommonResponse,不需要再次处理
        }else if(o instanceof CommonResponse){
            response=(CommonResponse<Object>) o;
            //否则把响应对象作为 CommonResponse 的data的部分
        }else {
            response.setData(o);
        }
        return response;
    }
}
