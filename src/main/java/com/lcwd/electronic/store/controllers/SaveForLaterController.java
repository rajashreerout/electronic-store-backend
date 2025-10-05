package com.lcwd.electronic.store.controllers;

import com.lcwd.electronic.store.dtos.SaveForLaterDto;
import com.lcwd.electronic.store.dtos.ApiResponseMessage;
import com.lcwd.electronic.store.services.SaveForLaterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/save-for-later")
public class SaveForLaterController {

    @Autowired
    private SaveForLaterService saveForLaterService;

    @PostMapping("/{productId}")
    public ResponseEntity<SaveForLaterDto> saveForLater(
            @PathVariable String userId,
            @PathVariable String productId,
            @RequestParam String source) {
        return new ResponseEntity<>(saveForLaterService.saveForLater(userId, productId, source), HttpStatus.CREATED);
    }

    @PostMapping("/{saveForLaterId}/move-to-cart")
    public ResponseEntity<ApiResponseMessage> moveToCart(
            @PathVariable String userId,
            @PathVariable String saveForLaterId) {
        saveForLaterService.moveToCart(userId, saveForLaterId);
        ApiResponseMessage message = ApiResponseMessage.builder()
                .message("Item moved to cart successfully !!")
                .success(true)
                .status(HttpStatus.OK)
                .build();
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PostMapping("/{saveForLaterId}/move-to-wishlist")
    public ResponseEntity<ApiResponseMessage> moveToWishlist(
            @PathVariable String userId,
            @PathVariable String saveForLaterId) {
        saveForLaterService.moveToWishlist(userId, saveForLaterId);
        ApiResponseMessage message = ApiResponseMessage.builder()
                .message("Item moved to wishlist successfully !!")
                .success(true)
                .status(HttpStatus.OK)
                .build();
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @DeleteMapping("/{saveForLaterId}")
    public ResponseEntity<ApiResponseMessage> remove(
            @PathVariable String userId,
            @PathVariable String saveForLaterId) {
        saveForLaterService.remove(userId, saveForLaterId);
        ApiResponseMessage message = ApiResponseMessage.builder()
                .message("Item removed successfully !!")
                .success(true)
                .status(HttpStatus.OK)
                .build();
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<SaveForLaterDto>> getUserSavedItems(@PathVariable String userId) {
        return new ResponseEntity<>(saveForLaterService.getUserSavedItems(userId), HttpStatus.OK);
    }
}