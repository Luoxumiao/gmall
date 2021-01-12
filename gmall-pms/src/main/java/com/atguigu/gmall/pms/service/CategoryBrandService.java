package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.eneity.CategoryBrandEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

/**
 * 品牌分类关联
 *
 * @author lxm
 * @email lxm@atguigu.com
 * @date 2020-12-14 20:43:11
 */
public interface CategoryBrandService extends IService<CategoryBrandEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
}

