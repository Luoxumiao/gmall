package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.eneity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.eneity.SkuImagesEntity;
import com.atguigu.gmall.pms.eneity.SpuAttrValueEntity;
import com.atguigu.gmall.pms.eneity.SpuEntity;
import com.atguigu.gmall.pms.feign.GmallPmsClient;
import com.atguigu.gmall.pms.mapper.SkuMapper;
import com.atguigu.gmall.pms.mapper.SpuDescMapper;
import com.atguigu.gmall.pms.service.*;
import com.atguigu.gmall.pms.vo.SkuVo;
import com.atguigu.gmall.pms.vo.SpuAttrValueVo;
import com.atguigu.gmall.pms.vo.SpuVo;
import com.atguigu.gmall.sms.vo.SkuSaleVo;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.SpuMapper;
import org.springframework.util.CollectionUtils;


@Service("spuService")
public class SpuServiceImpl extends ServiceImpl<SpuMapper, SpuEntity> implements SpuService {

    @Autowired
    private SpuDescService spuDescService;

    @Autowired
    private SpuDescMapper descMapper;

    @Autowired
    private SpuAttrValueService spuAttrValueService;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuAttrValueService skuAttrValueService;

    @Autowired
    private GmallPmsClient gmallPmsClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SpuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SpuEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public PageResultVo querySpuByCidAndPage(Long cid, PageParamVo paramVo) {

        QueryWrapper<SpuEntity> queryWrapper = new QueryWrapper<>();

        //如果用户选择了分类，并且查询本类
        if (cid != 0){
            queryWrapper.eq("category_id", cid);
        }

        String key = paramVo.getKey();
        //判断关键字是否为空
        if (StringUtils.isNotBlank(key)){
            queryWrapper.and(t -> t.eq("id",key).or().like("name", key));
        }
        IPage<SpuEntity> page = this.page(
                paramVo.getPage(),queryWrapper
        );

        return new PageResultVo(page);
    }

    //@Transactional(rollbackFor = Exception.class)//,readOnly = true)//timeout = 3)//noRollbackFor = ArithmeticException.class
    @GlobalTransactional
    @Override
    public void bigSave(SpuVo spu) {
        //1.保存spu相关信息
        //1.1保存spu的基本信息：pms_spu
        Long spuId = saveSpu(spu);
        //1.2 保存spu的描述信息：pms_spu_desc
        //this.saveSpuDesc(spu, spuId);
        this.spuDescService.saveSpuDesc(spu, spuId);

        //int i = 1/0;

        //1.3 保存spu的基本属性信息：pms_spu_attr_value
        saveBaseAttr(spu, spuId);
        //2.保存sku的相关信息
        saveSkuInfo(spu, spuId);

        //int i = 1/0;
        rabbitTemplate.convertAndSend("PMS_ITEM_EXCHANGE", "item.insert", spuId);
    }

    private void saveSkuInfo(SpuVo spu, Long spuId) {
        List<SkuVo> skus = spu.getSkus();
        if (CollectionUtils.isEmpty(skus)){
            return;
        }
        //2.1  保存sku的基本信息：pms_sku
        skus.forEach(sku -> {
            sku.setSpuId(spuId);
            sku.setBrandId(spu.getBrandId());
            sku.setCatagoryId(spu.getCategoryId());
            List<String> images = sku.getImages();
            //设置默认图片
            if (!CollectionUtils.isEmpty(images)){
                sku.setDefaultImage(StringUtils.isNotBlank(sku.getDefaultImage())? sku.getDefaultImage() : images.get(0));
            }
            this.skuMapper.insert(sku);
            Long skuId = sku.getId();

            //2.2 保存sku的图片信息：pms_sku_images
            if (!CollectionUtils.isEmpty(images)){
                skuImagesService.saveBatch(images.stream().map(image ->{
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setUrl(image);
                    skuImagesEntity.setDefaultStatus(StringUtils.equals(sku.getDefaultImage(), image)?1:0);
                    return skuImagesEntity;
                }).collect(Collectors.toList()));
            }
            //2.3 保存sku的销售属性：pms_sku_sttr_value
            List<SkuAttrValueEntity> saleAttrs = sku.getSaleAttrs();
            if (!CollectionUtils.isEmpty(saleAttrs)){
                saleAttrs.forEach(skuAttrValueEntity -> skuAttrValueEntity.setSkuId(skuId));
                skuAttrValueService.saveBatch(saleAttrs);
            }

            //3.保存sku的营销信息
            SkuSaleVo skuSaleVo = new SkuSaleVo();
            BeanUtils.copyProperties(sku, skuSaleVo);
            skuSaleVo.setSkuId(skuId);
            gmallPmsClient.saveSales(skuSaleVo);

        });
    }

    private void saveBaseAttr(SpuVo spu, Long spuId) {
        List<SpuAttrValueVo> baseAttrs = spu.getBaseAttrs();
        if (!CollectionUtils.isEmpty(baseAttrs)){
            spuAttrValueService.saveBatch(baseAttrs.stream().map(spuAttrValueVo ->{
                SpuAttrValueEntity spuAttrValueEntity = new SpuAttrValueEntity();
                BeanUtils.copyProperties(spuAttrValueVo, spuAttrValueEntity);
                spuAttrValueEntity.setSpuId(spuId);
                return spuAttrValueEntity;
            }).collect(Collectors.toList()));
        }
    }



    private Long saveSpu(SpuVo spu) {
        spu.setCreateTime(new Date());
        spu.setUpdateTime(spu.getCreateTime());
        this.save(spu);
        return spu.getId();
    }
}
