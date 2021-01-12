package com.atguigu.gmall.search.pojo;

import lombok.Data;

import java.util.List;

@Data
public class SearchParamVo {
    //搜索关键字
    private String keyword;

    //品牌id
    private List<Long> brandId;

    //分类id过滤条件
    private List<Long> categoryId;

    //规格参数的过滤条件
    private List<String> props;

    //价格区间的过滤
    private Double priceFrom;
    private Double priceTo;

    //是否有货过滤
    private Boolean store;

    //排序: 0综合排序  2价格升序  1价格降序  3销量的降序  4新品降序
    private Integer sort;

    //分页参数
    private Integer pageNum = 1;
    private final Integer pageSize = 20;
}
