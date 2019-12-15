package com.leyou.search.repository;

import com.leyou.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author Jiang-gege
 * 2019/11/2023:34
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {
}
