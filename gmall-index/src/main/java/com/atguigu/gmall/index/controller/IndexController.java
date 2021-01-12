package com.atguigu.gmall.index.controller;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.pms.eneity.CategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController {
    @Autowired
    private IndexService indexService;

    @GetMapping({"index.html","/"})
    public String toIndex(Model model, HttpServletRequest request){
        System.out.println(request.getHeader("userId"));
        //三级分类数据
        List<CategoryEntity> categoryEntities = indexService.queryLvl1CategoriesByPid();
        model.addAttribute("categories",categoryEntities);

        //TODO：广告 楼层广告 小轮播广告

        return "index";

    }

    @GetMapping("index/cates/{pid}")
    @ResponseBody
    public ResponseVo<List<CategoryEntity>> querylvl2CategoriesWithSubByPid(@PathVariable("pid") Long pid){
        List<CategoryEntity> categoryEntities = indexService.querylvl2CategoriesWithSubByPid(pid);
        return ResponseVo.ok(categoryEntities);
    }

    @GetMapping("index/test/lock")
    @ResponseBody
    public ResponseVo testLock(){
        indexService.testLock();
        return ResponseVo.ok();
    }

    @ResponseBody
    @GetMapping("index/test/read")
    public ResponseVo testRead(){
        indexService.testRead();
        return ResponseVo.ok("读取成功");
    }


    @ResponseBody
    @GetMapping("index/test/write")
    public ResponseVo testWrite(){
        indexService.testWrite();
        return ResponseVo.ok("写入成功");
    }

    @ResponseBody
    @GetMapping("index/test/latch")
    public ResponseVo testLatch(){
        indexService.testLatch();
        return ResponseVo.ok("锁门了");
    }

    @ResponseBody
    @GetMapping("index/test/countdown")
    public ResponseVo testCountDown(){
        indexService.testCountDown();
        return ResponseVo.ok("出来一个");
    }



}
