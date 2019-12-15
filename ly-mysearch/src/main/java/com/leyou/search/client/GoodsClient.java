package com.leyou.search.client;

import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author Jiang-gege
 * 2019/11/2023:07
 */
@FeignClient("item-service")
public interface GoodsClient extends GoodsApi{
}
