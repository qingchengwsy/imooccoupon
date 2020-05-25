package com.imooc.distribution.advice;

import com.imooc.distribution.exception.CouponException;
import com.imooc.distribution.vo.CommonResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理
 */

@RestControllerAdvice   //对所有controller请求进行拦截
public class GlobaExceptionAdvice {

    /**
     * <h2>对CouponException进行统一异常处理</h2>
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler(value = CouponException.class)  //ExceptionHandler: 可以对指定的异常进行拦截
    public CommonResponse<String> handlerCouponException(HttpServletRequest request,
                                                         CouponException ex){
        // 统一异常接口的响应
        // 优化: 定义不同类型的异常枚举(异常码和异常信息)
          CommonResponse<String> response=new CommonResponse<>(-1,"business error");
          response.setData(ex.getMessage());
          return response;
    }
}
