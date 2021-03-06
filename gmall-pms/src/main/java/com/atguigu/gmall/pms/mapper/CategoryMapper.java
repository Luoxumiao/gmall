package com.atguigu.gmall.pms.mapper;

import com.atguigu.gmall.pms.eneity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品三级分类
 * 
 * @author lxm
 * @email lxm@atguigu.com
 * @date 2020-12-14 20:43:12
 */
@Mapper
public interface CategoryMapper extends BaseMapper<CategoryEntity> {

    List<CategoryEntity> queryCategoriesWithSubsByPid(Long parentId);

}
