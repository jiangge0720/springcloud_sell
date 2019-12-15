package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.SkuMapper;
import com.leyou.item.mapper.SpuDetailMapper;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.item.mapper.StockMapper;
import com.leyou.item.pojo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author Jiang-gege
 * 2019/11/713:48
 */
@Service
public class GoodsService {
    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    public PageResult<Spu> querySpuByPage(Integer page, Integer rows, Boolean saleable, String key) {
        //分页
        PageHelper.startPage(page, rows);

        //过滤
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //搜索字段过滤
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "%");
        }
        //上下架过滤
        if (saleable != null) {
            criteria.andEqualTo("saleable", saleable);
        }
        //默认排序
        example.setOrderByClause("last_update_time DESC");

        //查询
        List<Spu> spus = spuMapper.selectByExample(example);

        //判断
        if (CollectionUtils.isEmpty(spus)) {
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }

        //解析分类和品牌的名称
        loadCategoryAndBrandName(spus);
        //解析分页结果
        PageInfo<Spu> info = new PageInfo<>(spus);
        return new PageResult<>(info.getTotal(), spus);
    }

    private void loadCategoryAndBrandName(List<Spu> spus) {
        for (Spu spu : spus) {
            //处理分类名称
            List<String> names = categoryService.queryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()))
                    .stream().map(Category::getName).collect(Collectors.toList());
            spu.setCname(StringUtils.join(names, "/"));
            //处理品牌名称
            spu.setBname(brandService.queryById(spu.getBrandId()).getName());
        }
    }

    //新增商品
    @Transactional
    public void saveGoods(Spu spu) {
        //新增spu表
        spu.setId(null);
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(spu.getCreateTime());
        spu.setSaleable(true);
        spu.setValid(false);
        int count = spuMapper.insert(spu);
        if (count != 1) {
            throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
        }
        //新增spu_detail表
        SpuDetail spuDetail = spu.getSpuDetail();
        spuDetail.setSpuId(spu.getId());
        spuDetailMapper.insert(spuDetail);

        //定义库存集合
        List<Stock> strokeList = new ArrayList<>();
        //新增sku表
        List<Sku> skus = spu.getSkus();
        for (Sku sku : skus) {
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            sku.setSpuId(spu.getId());

            int count2 = skuMapper.insert(sku);
            if (count2 != 1) {
                throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
            }
            //新增库存表
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());

            strokeList.add(stock);
        }
        //批量新增库存
        stockMapper.insertList(strokeList);
        //发送mq消息
        //amqpTemplate.convertAndSend("item.insert",spu.getId());

    }

    public SpuDetail querySpuDetailById(Long id) {
        return this.spuDetailMapper.selectByPrimaryKey(id);
    }

    public List<Sku> querySkuBySpuId(Long spuId) {
        // 查询sku
        Sku record = new Sku();
        record.setSpuId(spuId);
        List<Sku> skus = skuMapper.select(record);
        for (Sku sku : skus) {
            // 同时查询出库存
            sku.setStock(this.stockMapper.selectByPrimaryKey(sku.getId()).getStock());
        }
        return skus;
    }

    public Spu querySpuById(Long id) {
        //查询spu
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if(spu == null){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }

        //查询sku
        spu.setSkus(querySkuBySpuId(id));

        //查询detail
        spu.setSpuDetail(querySpuDetailById(id));
        return spu;
    }
}

//    //修改商品
//    @Transactional
//    public void update(Spu spu) {
//        // 查询以前sku
//        List<Sku> skus = this.querySkuBySpuId(spu.getId());
//        // 如果以前存在，则删除
//        if(!CollectionUtils.isEmpty(skus)) {
//            List<Long> ids = skus.stream().map(s -> s.getId()).collect(Collectors.toList());
//            // 删除以前库存
//            Example example = new Example(Stock.class);
//            example.createCriteria().andIn("skuId", ids);
//            this.stockMapper.deleteByExample(example);
//
//            // 删除以前的sku
//            Sku record = new Sku();
//            record.setSpuId(spu.getId());
//            this.skuMapper.delete(record);
//
//        }
//
//        //未实现
//        // 新增sku和库存
//        //saveSkuAndStock(spu.getSkus(), spu.getId());
//
//        // 更新spu
//        spu.setLastUpdateTime(new Date());
//        spu.setCreateTime(null);
//        spu.setValid(null);
//        spu.setSaleable(null);
//        this.spuMapper.updateByPrimaryKeySelective(spu);
//
//        // 更新spu详情
//        this.spuDetailMapper.updateByPrimaryKeySelective(spu.getSpuDetail());
//    }
//        //发送mq消息
//        amqpTemplate.convertAndSend("item.update",spu.getId());
//}