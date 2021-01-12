package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.eneity.CommentEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

/**
 * 商品评价
 *
 * @author lxm
 * @email lxm@atguigu.com
 * @date 2020-12-14 20:43:11
 */
public interface CommentService extends IService<CommentEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
}

