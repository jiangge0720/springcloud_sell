package com.leyou.page.client;

import com.leyou.item.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;


/**
 * @author Jiang-gege
 * 2019/11/2022:53
 */
@FeignClient("item-service")
public interface CategoryClient extends CategoryApi{

}
