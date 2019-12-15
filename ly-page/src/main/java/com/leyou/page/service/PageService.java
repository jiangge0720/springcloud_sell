package com.leyou.page.service;

import com.leyou.item.pojo.*;
import com.leyou.page.client.BrandClient;
import com.leyou.page.client.CategoryClient;
import com.leyou.page.client.GoodsClient;
import com.leyou.page.client.SpecificationClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jiang-gege
 * 2019/12/216:45
 */
@Service
public class PageService {

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecificationClient specificationClient;

    //@Autowired
    //private  TemplateEngine templateEngine;

    public Map<String, Object> loadModel(Long spuId) {
        Map<String, Object> model = new HashMap<>();

        Spu spu = goodsClient.querySpuById(spuId);

        List<Sku> skus = spu.getSkus();

        SpuDetail detail = spu.getSpuDetail();

        Brand brand = brandClient.queryBrandById(spu.getBrandId());

        List<Category> categories = categoryClient.queryCategoryByIds(
                Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3()));

        List<SpecGroup> specs = specificationClient.queryListByCid(spu.getCid3());

        model.put("spu",spu);
        model.put("skus",skus);
        model.put("detail",detail);
        model.put("brand",brand);
        model.put("categories",categories);
        model.put("specs",specs);

        return model;
    }

    public void deleteHtml(Long spuId) {
        File dest = new File("F:/upload/",spuId + ".html");
        if(dest.exists()){
            dest.delete();
        }
    }

/*    public void createHtml(Long spuId){
        //上下文
        Context context = new Context();
        context.setVariables(loadModel(spuId));
        //输出流
        File dest = new File("F:/upload/",spuId + ".html");
        if(dest.exists()){
            dest.delete();
        }
        try(PrintWriter writer = new PrintWriter(dest,"UTF-8")){
            templateEngine.process("item",context,writer);
        }catch (Exception e){
            log.error("[静态页服务] 生成静态页异常!",e);
        }
        //生成html

    }*/
}