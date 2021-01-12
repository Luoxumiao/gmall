package com.atguigu.gmall.pms.vo;

import lombok.Data;

import java.util.Set;

//销售属性集合
@Data
public class SaleAttrValueVo {

    private Long attrId;
    private String attrName;
    private Set<String> attrValues;

}
