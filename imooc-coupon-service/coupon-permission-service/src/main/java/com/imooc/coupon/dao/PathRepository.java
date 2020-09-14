package com.imooc.coupon.dao;

import com.imooc.coupon.entity.Path;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Path Dao
 */
public interface PathRepository extends JpaRepository<Path,Integer>{

    /**
     * 根据服务名称查询 Path记录
     * @param serviceName
     * @return
     */
    List<Path> findAllByServiceName(String serviceName);

    /**
     * 根据 路径模式 + 请求类型 查找数据记录
     * @param pathPattern
     * @param httpMethod
     * @return
     */
    Path findByPathPatternAndHttpMethod(String pathPattern,String httpMethod);
}
