package com.lcwd.electronic.store.services;

import com.lcwd.electronic.store.dtos.CouponDto;
import com.lcwd.electronic.store.dtos.PageableResponse;

public interface CouponService {
    CouponDto create(CouponDto couponDto);
    CouponDto update(CouponDto couponDto, Long couponId);
    void delete(Long couponId);
    CouponDto get(Long couponId);
    CouponDto getByCode(String code);
    PageableResponse<CouponDto> getAll(int pageNumber, int pageSize, String sortBy, String sortDir);
    CouponDto validateCoupon(String code, double orderAmount);
    void incrementUsage(String code);
}