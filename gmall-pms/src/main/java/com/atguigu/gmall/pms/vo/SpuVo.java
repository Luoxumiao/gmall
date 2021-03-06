package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.eneity.SpuEntity;
import lombok.Data;

import java.util.List;

@Data
public class SpuVo extends SpuEntity {

    private List<String> spuImages;

    private List<SpuAttrValueVo> baseAttrs;

    private List<SkuVo> skus;

}
