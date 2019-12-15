package com.leyou.item.mapper;

import com.leyou.item.pojo.Category;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author Jiang-gege
 * 2019/10/1513:53
 */
public interface CategoryMapper extends Mapper<Category>,IdListMapper<Category,Long> {
}