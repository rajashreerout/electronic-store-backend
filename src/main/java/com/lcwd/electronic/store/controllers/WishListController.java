package com.lcwd.electronic.store.controllers;

import com.lcwd.electronic.store.dtos.WishListDto;
import com.lcwd.electronic.store.dtos.ApiResponseMessage;
import com.lcwd.electronic.store.services.WishListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/wishlist")
public class WishListController {

    @Autowired
    private WishListService wishListService;

    @PostMapping("/{productId}")
    public ResponseEntity<WishListDto> addToWishList(
            @PathVariable String userId,
            @PathVariable String productId,
            @RequestParam(required = false) Long sectionId) {
        return new ResponseEntity<>(wishListService.addToWishList(userId, productId, sectionId), HttpStatus.CREATED);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponseMessage> removeFromWishList(
            @PathVariable String userId,
            @PathVariable String productId) {
        wishListService.removeFromWishList(userId, productId);
        ApiResponseMessage response = ApiResponseMessage.builder()
            .message("Product removed from wishlist successfully!!")
            .status(HttpStatus.OK)
            .success(true)
            .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<WishListDto>> getUserWishList(
            @PathVariable String userId,
            @RequestParam(required = false) Long sectionId) {
        if (sectionId != null) {
            return new ResponseEntity<>(wishListService.getWishListBySection(userId, sectionId), HttpStatus.OK);
        }
        return new ResponseEntity<>(wishListService.getUserWishList(userId), HttpStatus.OK);
    }

    @GetMapping("/{productId}/check")
    public ResponseEntity<Boolean> checkProductInWishList(
            @PathVariable String userId,
            @PathVariable String productId) {
        return new ResponseEntity<>(wishListService.isProductInWishList(userId, productId), HttpStatus.OK);
    }
}