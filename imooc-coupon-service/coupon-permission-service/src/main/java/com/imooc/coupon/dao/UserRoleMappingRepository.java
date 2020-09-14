package com.imooc.coupon.dao;

import com.imooc.coupon.entity.UserRoleMapping;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * UserRoleMapping Dao
 */
public interface UserRoleMappingRepository extends JpaRepository<UserRoleMapping,Long> {

    /**
     * 通过 userId 查找记录
     * @param userId
     * @return
     */
    UserRoleMapping findByUserId(String userId);
}
