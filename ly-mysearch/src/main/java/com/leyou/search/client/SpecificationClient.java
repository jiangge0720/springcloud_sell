package com.leyou.search.client;

import com.leyou.item.api.SpecificationApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author Jiang-gege
 * 2019/11/2023:31
 */
@FeignClient("item-service")
public interface SpecificationClient extends SpecificationApi {
}
