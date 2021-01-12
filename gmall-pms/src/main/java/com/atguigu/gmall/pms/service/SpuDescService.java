package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.eneity.SpuDescEntity;
import com.atguigu.gmall.pms.vo.SpuVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

/**
 * spu信息介绍
 *
 * @author lxm
 * @email lxm@atguigu.com
 * @date 2020-12-14 20:43:11
 */
public interface SpuDescService extends IService<SpuDescEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    public void saveSpuDesc(SpuVo spu, Long spuId);
}

