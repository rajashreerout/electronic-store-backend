package com.lcwd.electronic.store.services;

import com.lcwd.electronic.store.dtos.WishListDto;
import java.util.List;

public interface WishListService {
    WishListDto addToWishList(String userId, String productId, Long sectionId);
    void removeFromWishList(String userId, String productId);
    List<WishListDto> getUserWishList(String userId);
    boolean isProductInWishList(String userId, String productId);
    List<WishListDto> getWishListBySection(String userId, Long sectionId);
}