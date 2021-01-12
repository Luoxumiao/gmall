package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.eneity.AttrGroupEntity;
import com.atguigu.gmall.pms.vo.ItemGroupVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import java.util.List;

/**
 * 属性分组
 *
 * @author lxm
 * @email lxm@atguigu.com
 * @date 2020-12-14 20:43:12
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    List<AttrGroupEntity> queryGroupWithAttrsByCid(Long cid);

    List<ItemGroupVo> queryGroupsWithAttrsAndValuesByCidAndSpuIdAndSkuId(Long cid, Long skuId, Long spuId);
}

