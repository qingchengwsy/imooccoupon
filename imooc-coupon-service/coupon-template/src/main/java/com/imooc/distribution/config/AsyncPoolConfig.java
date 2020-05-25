package com.imooc.distribution.config;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 自定义异步任务线程池
 */

@Slf4j
@EnableAsync
@Configuration
public class AsyncPoolConfig implements AsyncConfigurer {

    @Bean
    public Executor getAsyncExecutor() {
        //定义线程池实现
        ThreadPoolTaskExecutor executor=new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10); //核心线程数
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(20);  //队列
        executor.setKeepAliveSeconds(60); //空闲时的生存时间
        executor.setThreadNamePrefix("ImoocAsync_");
        executor.setWaitForTasksToCompleteOnShutdown(true); //任务关闭时线程池是否退出
        executor.setAwaitTerminationSeconds(60); //服务关闭时线程的最长等待时间
        executor.setRejectedExecutionHandler(
                new ThreadPoolExecutor.CallerRunsPolicy()  //拒绝策略
        );
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncExceptionHandler();
    }

    class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler{

        //异常捕获Handler
        @Override
        public void handleUncaughtException(Throwable throwable, //异常任务抛出异常
                                            Method method,  //异步任务对应方法
                                            Object... objects){ //异步任务参数数组
                 throwable.printStackTrace(); //打印异常堆栈
              log.error("AsyncError{},method{},Param{}",throwable.getMessage(),
                      method.getName(),JSON.toJSONString(objects));

              //TODO 发送邮件短信,做进一步的处理
        }
    }
}
