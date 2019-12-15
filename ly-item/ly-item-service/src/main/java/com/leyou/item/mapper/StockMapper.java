package com.leyou.item.mapper;


import com.leyou.item.pojo.Stock;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author Jiang-gege
 * 2019/11/80:14
 */
public interface StockMapper extends Mapper<Stock>,InsertListMapper<Stock> {
}
