package com.imooc.distribution.dao;

import com.imooc.distribution.pojo.CouponTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * CouponTemplate Dao接口定义
 */
public interface CouponTemplateDao extends JpaRepository<CouponTemplate,Integer> {

    /**
     * 根据模板名称查询模板
     * where name=....
     * @param name
     * @return
     */
    CouponTemplate findByName(String name);

    /**
     * 根据available(是否启用)和expired(是否过期)查询模板
     * where available =... and expired=....
     * @param available
     * @param expired
     * @return
     */
    List<CouponTemplate> findAllByAvailableAndExpired(Boolean available, Boolean expired);

    /**
     * 根据expired(是否过期)查询模板
     * where expired=...
     * @param expired
     * @return
     */
    List<CouponTemplate> findAllByExpired(Boolean expired);
}
