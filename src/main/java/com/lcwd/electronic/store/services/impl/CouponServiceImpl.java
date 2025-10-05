package com.lcwd.electronic.store.services.impl;

import com.lcwd.electronic.store.dtos.CouponDto;
import com.lcwd.electronic.store.dtos.PageableResponse;
import com.lcwd.electronic.store.entities.Coupon;
import com.lcwd.electronic.store.exceptions.BadApiRequestException;
import com.lcwd.electronic.store.exceptions.ResourceNotFoundException;
import com.lcwd.electronic.store.helper.Helper;
import com.lcwd.electronic.store.repositories.CouponRepository;
import com.lcwd.electronic.store.services.CouponService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private ModelMapper mapper;

    @Override
    public CouponDto create(CouponDto couponDto) {
        // Check if coupon code already exists
        if (couponRepository.existsByCode(couponDto.getCode())) {
            throw new BadApiRequestException("Coupon code already exists");
        }
        Coupon coupon = mapper.map(couponDto, Coupon.class);
        Coupon savedCoupon = couponRepository.save(coupon);
        return mapper.map(savedCoupon, CouponDto.class);
    }

    @Override
    public CouponDto update(CouponDto couponDto, Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id: " + couponId));
        
        coupon.setCode(couponDto.getCode());
        coupon.setDescription(couponDto.getDescription());
        coupon.setDiscountPercent(couponDto.getDiscountPercent());
        coupon.setMaxDiscount(couponDto.getMaxDiscount());
        coupon.setMinPurchaseAmount(couponDto.getMinPurchaseAmount());
        coupon.setValidFrom(couponDto.getValidFrom());
        coupon.setValidUntil(couponDto.getValidUntil());
        coupon.setActive(couponDto.isActive());
        coupon.setUsageLimit(couponDto.getUsageLimit());

        Coupon updatedCoupon = couponRepository.save(coupon);
        return mapper.map(updatedCoupon, CouponDto.class);
    }

    @Override
    public void delete(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id: " + couponId));
        couponRepository.delete(coupon);
    }

    @Override
    public CouponDto get(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id: " + couponId));
        return mapper.map(coupon, CouponDto.class);
    }

    @Override
    public CouponDto getByCode(String code) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with code: " + code));
        return mapper.map(coupon, CouponDto.class);
    }

    @Override
    public PageableResponse<CouponDto> getAll(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("desc")) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Coupon> page = couponRepository.findAll(pageable);
        return Helper.getPageableResponse(page, CouponDto.class);
    }

    @Override
    public CouponDto validateCoupon(String code, double orderAmount) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with code: " + code));

        LocalDateTime now = LocalDateTime.now();
        
        if (!coupon.isActive()) {
            throw new BadApiRequestException("Coupon is not active");
        }
        
        if (now.isBefore(coupon.getValidFrom()) || now.isAfter(coupon.getValidUntil())) {
            throw new BadApiRequestException("Coupon is not valid at this time");
        }
        
        if (coupon.getTimesUsed() >= coupon.getUsageLimit()) {
            throw new BadApiRequestException("Coupon usage limit exceeded");
        }
        
        if (orderAmount < coupon.getMinPurchaseAmount()) {
            throw new BadApiRequestException("Order amount is less than minimum required amount for this coupon");
        }

        return mapper.map(coupon, CouponDto.class);
    }

    @Override
    public void incrementUsage(String code) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with code: " + code));
        coupon.setTimesUsed(coupon.getTimesUsed() + 1);
        couponRepository.save(coupon);
    }
}