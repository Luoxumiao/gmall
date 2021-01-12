package com.atguigu.gmall.pms.mapper;

import com.atguigu.gmall.pms.eneity.AttrEntity;
import com.atguigu.gmall.pms.eneity.SkuAttrValueEntity;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 * 
 * @author lxm
 * @email lxm@atguigu.com
 * @date 2020-12-14 20:43:12
 */
@Mapper
public interface SkuAttrValueMapper extends BaseMapper<SkuAttrValueEntity> {

    public List<Map<String,Object>> querySaleAttrValuesMapppingBySkuId(Long spuId);

}
