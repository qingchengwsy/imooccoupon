package com.imooc.coupon.dao;

import com.imooc.coupon.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Role Dao
 */
public interface RoleRepositort extends JpaRepository<Role,Integer> {
}
