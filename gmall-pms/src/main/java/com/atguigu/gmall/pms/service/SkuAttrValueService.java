package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.eneity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.vo.SaleAttrValueVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import java.util.List;

/**
 * sku销售属性&值
 *
 * @author lxm
 * @email lxm@atguigu.com
 * @date 2020-12-14 20:43:12
 */
public interface SkuAttrValueService extends IService<SkuAttrValueEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    List<SkuAttrValueEntity> querySearchSkuAttrValuesByCidAndSkuId(Long cid, Long skuId);

    List<SaleAttrValueVo> querySaleAttrValueBySpuId(Long spuId);

    String querySaleAttrValuesMappingSkuIdBySpuId(Long spuId);
}

