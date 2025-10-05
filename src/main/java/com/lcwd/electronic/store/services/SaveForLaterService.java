package com.lcwd.electronic.store.services;

import com.lcwd.electronic.store.dtos.SaveForLaterDto;
import java.util.List;

public interface SaveForLaterService {
    SaveForLaterDto saveForLater(String userId, String productId, String source);
    void moveToCart(String userId, String saveForLaterId);
    void moveToWishlist(String userId, String saveForLaterId);
    void remove(String userId, String saveForLaterId);
    List<SaveForLaterDto> getUserSavedItems(String userId);
}