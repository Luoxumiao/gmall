package com.atguigu.gmall.sms.service.impl;

import com.atguigu.gmall.sms.entity.SkuFullReductionEntity;
import com.atguigu.gmall.sms.entity.SkuLadderEntity;
import com.atguigu.gmall.sms.mapper.SkuFullReductionMapper;
import com.atguigu.gmall.sms.mapper.SkuLadderMapper;
import com.atguigu.gmall.sms.vo.ItemSaleVo;
import com.atguigu.gmall.sms.vo.SkuSaleVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.sms.mapper.SkuBoundsMapper;
import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.atguigu.gmall.sms.service.SkuBoundsService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("skuBoundsService")
public class SkuBoundsServiceImpl extends ServiceImpl<SkuBoundsMapper, SkuBoundsEntity> implements SkuBoundsService {

    @Autowired
    private SkuFullReductionMapper skuFullReductionMapper;

    @Autowired
    private SkuLadderMapper skuLadderMapper;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SkuBoundsEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SkuBoundsEntity>()
        );

        return new PageResultVo(page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveSales(SkuSaleVo skuSaleVo) {
        //3.1  保存sku的积分优惠：sms_sku_bounds
        SkuBoundsEntity skuBoundsEntity = new SkuBoundsEntity();
        BeanUtils.copyProperties(skuSaleVo, skuBoundsEntity);
        List<Integer> works = skuSaleVo.getWork();
        if (!CollectionUtils.isEmpty(works)){
            skuBoundsEntity.setWork(works.get(3)*8+works.get(2)*4+works.get(1)*2+works.get(0));

        }
        this.save(skuBoundsEntity);
        //3.2保存sku的满减信息：sms_sku_full-reduction
        SkuFullReductionEntity reductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuSaleVo, reductionEntity);
        reductionEntity.setAddOther(skuSaleVo.getFullAddOther());
        this.skuFullReductionMapper.insert(reductionEntity);

        //3.3 保存sku的打折信息：sms_sku_ladder
        SkuLadderEntity ladderEntity = new SkuLadderEntity();
        BeanUtils.copyProperties(skuSaleVo, ladderEntity);
        ladderEntity.setAddOther(skuSaleVo.getLadderAddOther());
        skuLadderMapper.insert(ladderEntity);
    }

    @Override
    public List<ItemSaleVo> querySalesBySkuId(Long skuId) {

        List<ItemSaleVo> saleVos = new ArrayList<>();

        //查询积分优惠信息
        SkuBoundsEntity skuBoundsEntity = this.getOne(new QueryWrapper<SkuBoundsEntity>().eq("sku_id", skuId));
        if (skuBoundsEntity != null){
            ItemSaleVo itemSaleVo = new ItemSaleVo();
            itemSaleVo.setType("积分");
            itemSaleVo.setDesc("送" + skuBoundsEntity.getGrowBounds() + "成长积分，送" + skuBoundsEntity.getBuyBounds() + "购买积分");
            saleVos.add(itemSaleVo);
        }

        //查询满减优惠信息
        SkuFullReductionEntity skuFullReductionEntity = skuFullReductionMapper.selectOne(new QueryWrapper<SkuFullReductionEntity>().eq("sku_id", skuId));
        if (skuFullReductionEntity != null){
            ItemSaleVo itemSaleVo = new ItemSaleVo();
            itemSaleVo.setType("满减");
            itemSaleVo.setDesc("满" + skuFullReductionEntity.getFullPrice() + "减" + skuFullReductionEntity.getReducePrice());
            saleVos.add(itemSaleVo);
        }
        //查询打折优惠信息
        SkuLadderEntity skuLadderEntity = skuLadderMapper.selectOne(new QueryWrapper<SkuLadderEntity>().eq("sku_id", skuId));
        if (skuLadderEntity != null){
            ItemSaleVo itemSaleVo = new ItemSaleVo();
            itemSaleVo.setType("打折");
            itemSaleVo.setDesc("满" + skuLadderEntity.getFullCount() + "件打" + skuLadderEntity.getDiscount().divide(new BigDecimal(10)) + "折");
            saleVos.add(itemSaleVo);
        }

        return saleVos;
    }

}