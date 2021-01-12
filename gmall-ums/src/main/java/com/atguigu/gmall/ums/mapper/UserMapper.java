package com.atguigu.gmall.ums.mapper;

import com.atguigu.gmall.ums.entity.UserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表
 * 
 * @author lxm
 * @email lxm@atguigu.com
 * @date 2020-12-30 22:18:22
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
	
}
