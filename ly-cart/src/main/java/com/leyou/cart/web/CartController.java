package com.leyou.cart.web;

import com.leyou.cart.pojo.Cart;
import com.leyou.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Jiang-gege
 * 2019/12/823:06
 */
@RestController
public class CartController {

    @Autowired
    private CartService cartService;

    //新增购物车
//    @PostMapping
//    public ResponseEntity<Void> addCart(@RequestBody Cart cart){
//        cartService.addCart(cart);
//        return ResponseEntity.status(HttpStatus.CREATED).build();
//    }

    //查询个人购物车
//    @GetMapping("list")
//    public ResponseEntity<List<Cart>> queryCartList(){
//        return ResponseEntity.ok(cartService.queryCartList());
//    }
}