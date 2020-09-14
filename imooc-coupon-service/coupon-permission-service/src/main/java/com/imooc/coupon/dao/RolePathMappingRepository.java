package com.imooc.coupon.dao;

import com.imooc.coupon.entity.RolePathMapping;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * RolePathMapping Dao
 */
public interface RolePathMappingRepository extends JpaRepository<RolePathMapping,Integer> {

    /**
     * 通过 角色Id + 路径Id 查找数据记录
     * @param roleId
     * @param pathId
     * @return
     */
    RolePathMapping findByRoleIdAndPathId(String roleId,String pathId);
}
