package com.lcwd.electronic.store.controllers;

import com.lcwd.electronic.store.dtos.ApiResponseMessage;
import com.lcwd.electronic.store.dtos.WishlistSectionDto;
import com.lcwd.electronic.store.services.WishlistSectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/wishlist-sections")
public class WishlistSectionController {

    @Autowired
    private WishlistSectionService sectionService;

    @PostMapping
    public ResponseEntity<WishlistSectionDto> createSection(
            @PathVariable String userId,
            @RequestBody WishlistSectionDto sectionDto) {
        sectionDto.setUserId(userId);
        return new ResponseEntity<>(sectionService.createSection(sectionDto), HttpStatus.CREATED);
    }

    @PutMapping("/{sectionId}")
    public ResponseEntity<WishlistSectionDto> updateSection(
            @PathVariable Long sectionId,
            @RequestBody WishlistSectionDto sectionDto) {
        return new ResponseEntity<>(sectionService.updateSection(sectionId, sectionDto), HttpStatus.OK);
    }

    @DeleteMapping("/{sectionId}")
    public ResponseEntity<ApiResponseMessage> deleteSection(@PathVariable Long sectionId) {
        sectionService.deleteSection(sectionId);
        ApiResponseMessage response = ApiResponseMessage.builder()
                .message("Section deleted successfully!!")
                .status(HttpStatus.OK)
                .success(true)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<WishlistSectionDto>> getUserSections(@PathVariable String userId) {
        return new ResponseEntity<>(sectionService.getUserSections(userId), HttpStatus.OK);
    }

    @GetMapping("/{sectionId}")
    public ResponseEntity<WishlistSectionDto> getSection(@PathVariable Long sectionId) {
        return new ResponseEntity<>(sectionService.getSection(sectionId), HttpStatus.OK);
    }
}