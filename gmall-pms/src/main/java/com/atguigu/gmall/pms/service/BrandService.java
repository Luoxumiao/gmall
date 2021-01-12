package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.eneity.BrandEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

/**
 * 品牌
 *
 * @author lxm
 * @email lxm@atguigu.com
 * @date 2020-12-14 20:43:12
 */
public interface BrandService extends IService<BrandEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
}

