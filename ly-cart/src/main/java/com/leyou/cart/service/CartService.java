package com.leyou.cart.service;

import com.leyou.auth.entity.UserInfo;
import com.leyou.cart.interceptor.UserInterceptor;
import com.leyou.cart.pojo.Cart;
import com.leyou.common.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author Jiang-gege
 * 2019/12/823:10
 */
@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "cart:user:id";

//    public List<Cart> queryCartList() {
//        //获取登录用户
//        UserInfo user = UserInterceptor.getUser();
//        //key
//        String key = KEY_PREFIX + user.getId();
//
//        //获取登录用户的所有购物车数据
//        BoundHashOperations<String, Object, Object> operation = redisTemplate.boundHashOps(key);
//        final List<Cart> cartList = operation.values().stream().map(o -> JsonUtils.toBean(o.toString(), Cart.class))
//                .collect(Collectors.toList());
//
//        return cartList;
//    }
//    public void addCart(Cart cart) {
//
//        //获取登录用户
//        UserInfo user = UserInterceptor.getUser();
//        //key
//        String key = KEY_PREFIX + user.getId();
//        //hashkey
//        String hashKey = cart.getSkuId().toString(); //为商品spu的id
//        //绑定redis里的key，减少重复操作
//        BoundHashOperations<String, Object, Object> operation = redisTemplate.boundHashOps(key);
//        //判断当前购物车商品是否存在
//        if (operation.hasKey(hashKey)) {
//            //是，修改数量
//            String json = operation.get(hashKey).toString();
//            Cart casheCart = JsonUtils.toBean(json,Cart.class);//工具类出错  未完成
//            casheCart.setNum(casheCart.getNum() + cart.getNum());
//
//        }
//            //写回redis
//            operation.put(hashKey,JsonUtils.toString(casheCart));
//
//    }
}