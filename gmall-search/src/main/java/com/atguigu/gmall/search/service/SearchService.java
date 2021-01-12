package com.atguigu.gmall.search.service;

import com.atguigu.gmall.search.pojo.SearchParamVo;
import com.atguigu.gmall.search.pojo.SearchResponseVo;

public interface SearchService {
    public SearchResponseVo search(SearchParamVo searchParamVo);
}
