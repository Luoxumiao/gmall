package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.eneity.CategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.CategoryMapper;
import com.atguigu.gmall.pms.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<CategoryEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public List<CategoryEntity> queryCatgoriesByPid(Long pid) {

        QueryWrapper<CategoryEntity> queryWrapper = new QueryWrapper<>();
        if (pid != -1){
            queryWrapper.eq("parent_id", pid);
        }
        return list(queryWrapper);
    }

    @Override
    public List<CategoryEntity> queryCategoriesWithSubsByPid(Long pid) {

        List<CategoryEntity> categoryEntities = categoryMapper.queryCategoriesWithSubsByPid(pid);

        return categoryEntities;
    }

    @Override
    public List<CategoryEntity> queryLvl123CategoriesByCid3(Long id) {
        //查询三级分类
        CategoryEntity lvl3Category = this.getById(id);
        if (lvl3Category == null) {
            return null;
        }
        CategoryEntity lvl2Category = this.getById(lvl3Category.getParentId());

        CategoryEntity lvl1Category = this.getById(lvl2Category.getParentId());
        return Arrays.asList(lvl3Category,lvl2Category,lvl1Category);
    }

}