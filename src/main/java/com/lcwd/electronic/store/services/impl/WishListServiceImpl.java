package com.lcwd.electronic.store.services.impl;

import com.lcwd.electronic.store.dtos.WishListDto;
import com.lcwd.electronic.store.entities.Product;
import com.lcwd.electronic.store.entities.User;
import com.lcwd.electronic.store.entities.WishList;
import com.lcwd.electronic.store.entities.WishlistSection;
import com.lcwd.electronic.store.repositories.WishlistSectionRepository;
import com.lcwd.electronic.store.exceptions.ResourceNotFoundException;
import com.lcwd.electronic.store.repositories.ProductRepository;
import com.lcwd.electronic.store.repositories.UserRepository;
import com.lcwd.electronic.store.repositories.WishListRepository;
import com.lcwd.electronic.store.services.WishListService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class WishListServiceImpl implements WishListService {

    @Autowired
    private WishListRepository wishListRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private WishlistSectionRepository wishlistSectionRepository;

    @Override
    public WishListDto addToWishList(String userId, String productId, Long sectionId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        // Check if already in wishlist
        if (wishListRepository.existsByUserUserIdAndProductProductId(userId, productId)) {
            return getWishListItem(userId, productId);
        }

        WishlistSection section = null;
        if (sectionId != null) {
            section = wishlistSectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + sectionId));
        }

        WishList wishList = WishList.builder()
            .user(user)
            .product(product)
            .section(section)
            .addedDate(new Date())
            .build();

        WishList savedWishList = wishListRepository.save(wishList);
        return mapper.map(savedWishList, WishListDto.class);
    }

    @Override
    @Transactional
    public void removeFromWishList(String userId, String productId) {
        // First check if the wishlist item exists
        if (!wishListRepository.existsByUserUserIdAndProductProductId(userId, productId)) {
            throw new ResourceNotFoundException("Wishlist item not found for user: " + userId + " and product: " + productId);
        }
        wishListRepository.deleteByUserUserIdAndProductProductId(userId, productId);
    }

    @Override
    public List<WishListDto> getUserWishList(String userId) {
        List<WishList> wishLists = wishListRepository.findByUserUserId(userId);
        return wishLists.stream()
            .map(wishList -> {
                WishListDto dto = mapper.map(wishList, WishListDto.class);
                if (wishList.getSection() != null) {
                    dto.setSectionId(wishList.getSection().getId());
                    dto.setSectionName(wishList.getSection().getName());
                }
                return dto;
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<WishListDto> getWishListBySection(String userId, Long sectionId) {
        List<WishList> wishLists = wishListRepository.findByUserUserId(userId)
            .stream()
            .filter(wishList -> wishList.getSection() != null && wishList.getSection().getId().equals(sectionId))
            .collect(Collectors.toList());
        
        return wishLists.stream()
            .map(wishList -> {
                WishListDto dto = mapper.map(wishList, WishListDto.class);
                dto.setSectionId(wishList.getSection().getId());
                dto.setSectionName(wishList.getSection().getName());
                return dto;
            })
            .collect(Collectors.toList());
    }

    @Override
    public boolean isProductInWishList(String userId, String productId) {
        return wishListRepository.existsByUserUserIdAndProductProductId(userId, productId);
    }

    private WishListDto getWishListItem(String userId, String productId) {
        return wishListRepository.findByUserUserId(userId).stream()
            .filter(w -> w.getProduct().getProductId().equals(productId))
            .findFirst()
            .map(wishList -> mapper.map(wishList, WishListDto.class))
            .orElse(null);
    }
}