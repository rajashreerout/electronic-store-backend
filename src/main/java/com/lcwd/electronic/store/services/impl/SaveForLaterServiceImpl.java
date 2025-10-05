package com.lcwd.electronic.store.services.impl;

import com.lcwd.electronic.store.dtos.SaveForLaterDto;
import com.lcwd.electronic.store.entities.SaveForLater;
import com.lcwd.electronic.store.entities.Product;
import com.lcwd.electronic.store.entities.User;
import com.lcwd.electronic.store.entities.Cart;
import com.lcwd.electronic.store.entities.CartItem;
import com.lcwd.electronic.store.exceptions.ResourceNotFoundException;
import com.lcwd.electronic.store.repositories.SaveForLaterRepository;
import com.lcwd.electronic.store.repositories.UserRepository;
import com.lcwd.electronic.store.repositories.ProductRepository;
import com.lcwd.electronic.store.repositories.CartRepository;
import com.lcwd.electronic.store.services.SaveForLaterService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SaveForLaterServiceImpl implements SaveForLaterService {

    private static final String USER_NOT_FOUND_MESSAGE = "User not found with id: ";
    private static final String ITEM_NOT_FOUND_MESSAGE = "Save for later item not found with id: ";
    private static final String PRODUCT_NOT_FOUND_MESSAGE = "Product not found with id: ";

    private final SaveForLaterRepository saveForLaterRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final ModelMapper modelMapper;

    public SaveForLaterServiceImpl(
            SaveForLaterRepository saveForLaterRepository,
            UserRepository userRepository,
            ProductRepository productRepository,
            CartRepository cartRepository,
            ModelMapper modelMapper) {
        this.saveForLaterRepository = saveForLaterRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public SaveForLaterDto saveForLater(String userId, String productId, String source) {
        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE + userId));

        // Find product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND_MESSAGE + productId));

        // Create save for later item
        SaveForLater saveForLater = SaveForLater.builder()
                .id(UUID.randomUUID().toString())
                .user(user)
                .product(product)
                .quantity(1) // Default quantity
                .price(product.getDiscountedPrice())
                .addedFrom(source)
                .build();

        // Save and return
        SaveForLater saved = saveForLaterRepository.save(saveForLater);
        return modelMapper.map(saved, SaveForLaterDto.class);
    }

    @Override
    @Transactional
    public void moveToCart(String userId, String saveForLaterId) {
        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE + userId));

        // Find save for later item
        SaveForLater saveForLater = saveForLaterRepository.findById(saveForLaterId)
                .orElseThrow(() -> new ResourceNotFoundException(ITEM_NOT_FOUND_MESSAGE + saveForLaterId));

        // Get or create cart
        Cart cart = user.getCart();
        if (cart == null) {
            cart = Cart.builder()
                    .cartId(UUID.randomUUID().toString())
                    .user(user)
                    .build();
        }

        // Add item to cart
        CartItem cartItem = CartItem.builder()
                .product(saveForLater.getProduct())
                .quantity(saveForLater.getQuantity())
                .totalPrice((int)(saveForLater.getPrice() * saveForLater.getQuantity()))
                .cart(cart)
                .build();

        cart.getItems().add(cartItem);
        cartRepository.save(cart);

        // Remove from save for later
        saveForLaterRepository.delete(saveForLater);
    }

    private void removeFromSaveForLater(String userId, String saveForLaterId) {
        // Find save for later item
        SaveForLater saveForLater = saveForLaterRepository.findById(saveForLaterId)
                .orElseThrow(() -> new ResourceNotFoundException(ITEM_NOT_FOUND_MESSAGE + saveForLaterId));

        // Verify ownership
        if (!saveForLater.getUser().getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Save for later item not found for user");
        }

        // Remove item
        saveForLaterRepository.delete(saveForLater);
    }

    @Override
    @Transactional
    public void moveToWishlist(String userId, String saveForLaterId) {
        removeFromSaveForLater(userId, saveForLaterId);
    }

    @Override
    @Transactional
    public void remove(String userId, String saveForLaterId) {
        removeFromSaveForLater(userId, saveForLaterId);
    }

    @Override
    public List<SaveForLaterDto> getUserSavedItems(String userId) {
        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE + userId));

        // Get saved items
        List<SaveForLater> savedItems = saveForLaterRepository.findByUser(user);

        // Convert to DTOs
        return savedItems.stream()
                .map(item -> modelMapper.map(item, SaveForLaterDto.class))
                .toList();
    }
}