package com.lcwd.electronic.store.services;

import com.lcwd.electronic.store.dtos.WishlistSectionDto;
import java.util.List;

public interface WishlistSectionService {
    WishlistSectionDto createSection(WishlistSectionDto sectionDto);
    WishlistSectionDto updateSection(Long sectionId, WishlistSectionDto sectionDto);
    void deleteSection(Long sectionId);
    List<WishlistSectionDto> getUserSections(String userId);
    WishlistSectionDto getSection(Long sectionId);
}