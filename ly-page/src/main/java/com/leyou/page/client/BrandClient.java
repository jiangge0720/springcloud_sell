package com.leyou.page.client;

import com.leyou.item.api.BrandApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author Jiang-gege
 * 2019/11/2023:31
 */
@FeignClient("item-service")
public interface BrandClient extends BrandApi {
}
