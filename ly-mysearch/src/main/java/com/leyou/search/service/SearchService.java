package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.repository.GoodsRepository;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Jiang-gege
 * 2019/11/2023:45
 */
@Service
public class SearchService {

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecificationClient specificationClient;

    @Autowired
    private GoodsRepository repository;

    @Autowired
    private ElasticsearchTemplate template;

    public Goods buildGoods(Spu spu){
        //查询分类
        List<Category> categories = categoryClient.queryCategoryByIds(
                Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        if(CollectionUtils.isEmpty(categories)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        List<String> names = categories.stream().map(Category::getName).collect(Collectors.toList());
        //查询品牌
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        if(brand == null){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }

        //搜索字段
        String all = spu.getTitle() + StringUtils.join(names,"") + brand.getName();

        //查询sku
        List<Sku> skuList = goodsClient.querySkuBySpuId(spu.getId());
        if(CollectionUtils.isEmpty(skuList)){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }

        //对sku进行处理，删减用不到的字段
        List<Map<String,Object>>skus = new ArrayList<>();
        for(Sku sku : skuList){
            Map<String,Object> map = new HashMap<>();
            map.put("id",sku.getId());
            map.put("Title",sku.getTitle());
            map.put("Price",sku.getPrice());
            map.put("Images",StringUtils.substringBefore(sku.getImages(),","));//只要第一张图片
            skus.add(map);
        }
        //价格集合
        List<Long> priceList = skuList.stream().map(Sku::getPrice).collect(Collectors.toList());

        //查询规格参数
//        List<SpecParam> params = specificationClient.queryParamList(null, spu.getCid3(), true);
//        if(CollectionUtils.isEmpty(params)){
//            throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUN);
//        }

//        //查询商品详情
//        val spuDetail = goodsClient.querySpuDetailById(spu.getId());
//
//        //获取通用规格参数
//        String json = spuDetail.getSpecifications();
//        //JsonUtils.toMap(json,String.class ,String.class);   ////工具类有问题
//        //获取特有规格参数
//
//        //规格参数
//        HashMap<String,Object> specs = new HashMap<>();TODO  规格参数未完成

        //构建goods对象
        Goods goods = new Goods();
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setId(spu.getId());
        goods.setAll(all);// 搜索字段，包含标题，分类，品牌，规格等
        goods.setPrice(priceList);// 所有sku价格的集合
        //工具类有问题，先传null
        //goods.setSkus(JsonUtils.toString(skus));//TODO 所有sku的集合的json格式
        goods.setSkus(null);
        goods.setSpecs(null);//TODO 所有的可搜索的规格参数
        goods.setSubTitle(spu.getSubTitle());
        return goods;
    }








    //另一种表结构做法
    //    public Goods buildGoods(SpuBO spu){
//        Long id = spu.getId();
//        //准备数据
//
//        //商品分类名称
//        List<String> names = this.categoryClient.queryNameByIds(Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3()));
//        String all = spu.getTitle()+" "+StringUtils.join(names," ");
//
//        //sku集合
//        List<Sku> skus = this.goodsClient.querySkuBySpuId(id);
//        //处理sku
//        //把商品价格取出单独存放，便于展示
//        List<Long> prices = new ArrayList<>();
//        List<Map<String,Object>> skuList = new ArrayList<>();
//
//
//        for (Sku sku : skus) {
//            prices.add(sku.getPrice());
//            Map<String,Object> skuMap = new HashMap<>();
//            skuMap.put("id",sku.getId());
//            skuMap.put("title",sku.getTitle());
//            skuMap.put("image", StringUtils.isBlank(sku.getImages()) ? "":sku.getImages().split(",")[0]);
//            skuMap.put("price",sku.getPrice());
//            skuList.add(skuMap);
//        }
//
//        //spuDetail
//        SpuDetail spuDetail = this.goodsClient.querySpuDetailById(id);
//        //查询分类对应的规格参数
//        List<SpecParam> params = this.specificationClient.querySpecParam(null, spu.getCid3(), true, null);
//
//        //通用规格参数值
//        Map<Long, String> genericMap =
//                JsonUtils.parseMap(spuDetail.getGenericSpec(), Long.class, String.class);
//
//        //特有规格参数的值
//        Map<Long,List<String>> specialMap = JsonUtils.nativeRead(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<String>>>() {
//        });
//
//        //处理规格参数显示问题，默认显示id+值，处理后显示id对应的名称+值
//        Map<String, Object> specs = new HashMap<>();
//
//        for (SpecParam param : params) {
//            //规格参数的编号id id：1 表示的是品牌，4颜色
//            Long paramId = param.getId();
//
//            //今后显示的名称
//            String name = param.getName();//品牌，机身颜色
//            //通用参数
//            Object value = null;
//            if (param.getGeneric()){
//                //通用参数
//                value = genericMap.get(paramId);
//
//                if (param.getNumeric()){
//                    //数值类型需要加分段
//                    value = this.chooseSegment(value.toString(),param);
//                }
//            }
//            else {//特有参数
//                value = specialMap.get(paramId);
//
//            }
//            if (null==value){
//                value="其他";
//            }
//            specs.put(name,value);
//        }
//
//        Goods goods = new Goods();
//        goods.setId(spu.getId());
//        //这里如果要加品牌，可以再写个BrandClient，根据id查品牌
//        goods.setAll(all);
//        goods.setSubTitle(spu.getSubTitle());
//        goods.setBrandId(spu.getBrandId());
//        goods.setCid1(spu.getCid1());
//        goods.setCid2(spu.getCid2());
//        goods.setCid3(spu.getCid3());
//        goods.setCreateTime(spu.getCreateTime());
//        goods.setPrice(prices);
//        goods.setSkus(JsonUtils.serialize(skuList));
//        goods.setSpecs(specs);
//
//        return goods;
//    }
//
//    private String chooseSegment(String value, SpecParam p) {
//        double val = NumberUtils.toDouble(value);
//        String result = "其它";
//        // 保存数值段
//        for (String segment : p.getSegments().split(",")) {
//            String[] segs = segment.split("-");
//            // 获取数值范围
//            double begin = NumberUtils.toDouble(segs[0]);
//            double end = Double.MAX_VALUE;
//            if(segs.length == 2){
//                end = NumberUtils.toDouble(segs[1]);
//            }
//            // 判断是否在范围内
//            if(val >= begin && val < end){
//                if(segs.length == 1){
//                    result = segs[0] + p.getUnit() + "以上";
//                }else if(begin == 0){
//                    result = segs[1] + p.getUnit() + "以下";
//                }else{
//                    result = segment + p.getUnit();//4.5  4-5英寸
//                }
//                break;
//            }
//        }
//        return result;
//    }


    //根据搜索字段搜索
    public PageResult<Goods> search(SearchRequest request) {
        int page = request.getPage()-1;
        int size = request.getSize();
        //创建查询构造器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //结果过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","subTitle","skus"},null));

        //分页
        queryBuilder.withPageable(PageRequest.of(page,size));

        //搜索条件
        QueryBuilder basicQuery = buildBasicQuery(request);
        queryBuilder.withQuery(basicQuery);

        //聚合分类和品牌
        //1.聚合分类
        String categoryAggName = "category_agg";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        //2.聚合品牌
        String brandAggName = "brand_agg";
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));

        //查询
        AggregatedPage<Goods> result = template.queryForPage(queryBuilder.build(), Goods.class);

        //解析结果
        //1.解析分页结果
        long total = result.getTotalElements();
        long totalPage = result.getTotalPages();
        List<Goods> goodsList = result.getContent();
        //2.解析聚合结果
        Aggregations aggs = result.getAggregations();
        List<Category>categories = parseCategoryAgg(aggs.get(categoryAggName));
        List<Brand>brands = parseBrandAgg(aggs.get(brandAggName));

        //完成规格参数聚合
        List<Map<String,Object>> specs = null;
        if(categories != null && categories.size() == 1){
            //商品分类存在且数量为1，可以聚合规格参数
            specs = buildSpecificationAgg(categories.get(0).getId(),basicQuery);

        }

        return new SearchResult(total,totalPage,goodsList,categories,brands,specs);
    }

    private QueryBuilder buildBasicQuery(SearchRequest request) {
        //创建查询条件
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        //查询条件
        queryBuilder.must(QueryBuilders.matchQuery("all", request.getKey()));

        //过滤条件
        Map<String, String> map = request.getFilter();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            //处理key
            if(!"cid3".equals(key) && !"brandId".equals(key)){
                key = "specs."+key+".keyword";
            }
            String value = entry.getValue();
            queryBuilder.filter(QueryBuilders.termQuery(key,value));
        }

        return queryBuilder;
    }

    private List<Map<String, Object>> buildSpecificationAgg(Long cid, QueryBuilder basicQuery) {
        List<Map<String, Object>> specs = new ArrayList<>();
        //1.查询需要聚合的规格参数
        List<SpecParam> params = specificationClient.queryParamList(null, cid, true);
        //2.聚合
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //2.1带上查询条件
        queryBuilder.withQuery(basicQuery);
        //2.2聚合
        for (SpecParam param : params) {
            String name = param.getName();
            queryBuilder.addAggregation(AggregationBuilders.terms(name).field("specs."+name+".keyword"));
        }
        //3.获取结果
        AggregatedPage<Goods> result = template.queryForPage(queryBuilder.build(), Goods.class);

        //4.解析结果
        Aggregations aggs = result.getAggregations();
        for (SpecParam param : params) {
            //规格参数名称
            String name = param.getName();
            StringTerms terms = aggs.get(name);
            //规格参数待选项
            List<String> options = terms.getBuckets()
                    .stream().map(b -> b.getKeyAsString()).collect(Collectors.toList());
            //准备返回的map
            Map<String,Object> map = new HashMap<>();
            map.put("k",name);
            map.put("options",options);

            specs.add(map);
        }
        return specs;
    }

    private List<Brand> parseBrandAgg(LongTerms terms) {
        try {
            List<Long> ids = terms.getBuckets()
                    .stream().map(b -> b.getKeyAsNumber().longValue()).collect(Collectors.toList());

        List<Brand> brands = brandClient.queryBrandByIds(ids);
        return brands;
    }catch (Exception e){
            return null;
        }
    }

    private List<Category> parseCategoryAgg(LongTerms terms) {
        try {
            List<Long> ids = terms.getBuckets()
                    .stream().map(b -> b.getKeyAsNumber().longValue()).collect(Collectors.toList());

        List<Category> categories = categoryClient.queryCategoryByIds(ids);
        return categories;
    }catch (Exception e){
        return null;
    }
    }

    public void createOrUpdateIndex(Long spuId) {
        //查询spu
        Spu spu = goodsClient.querySpuById(spuId);
        //构建goods
        Goods goods = buildGoods(spu);
        //存入索引库
        repository.save(goods);
    }
}