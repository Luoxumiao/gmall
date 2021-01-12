package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.mapper.CategoryMapper;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CategoryServiceImplTest {

    @Autowired
    private CategoryMapper categoryMapper;

    @Test
    void queryCategoriesWithSubsByPid() {
        System.out.println(categoryMapper.queryCategoriesWithSubsByPid(1l));
    }
}