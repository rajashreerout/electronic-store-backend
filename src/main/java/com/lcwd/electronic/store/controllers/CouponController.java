package com.lcwd.electronic.store.controllers;

import com.lcwd.electronic.store.dtos.ApiResponseMessage;
import com.lcwd.electronic.store.dtos.CouponDto;
import com.lcwd.electronic.store.dtos.PageableResponse;
import com.lcwd.electronic.store.services.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/coupons")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CouponDto> create(@Valid @RequestBody CouponDto couponDto) {
        return new ResponseEntity<>(couponService.create(couponDto), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{couponId}")
    public ResponseEntity<CouponDto> update(
            @Valid @RequestBody CouponDto couponDto,
            @PathVariable Long couponId) {
        return new ResponseEntity<>(couponService.update(couponDto, couponId), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{couponId}")
    public ResponseEntity<ApiResponseMessage> delete(@PathVariable Long couponId) {
        couponService.delete(couponId);
        return new ResponseEntity<>(
                ApiResponseMessage.builder()
                    .message("Coupon deleted successfully")
                    .success(true)
                    .status(HttpStatus.OK)
                    .build(),
                HttpStatus.OK);
    }

    @GetMapping("/{couponId}")
    public ResponseEntity<CouponDto> get(@PathVariable Long couponId) {
        return new ResponseEntity<>(couponService.get(couponId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PageableResponse<CouponDto>> getAll(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "code") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        return new ResponseEntity<>(
                couponService.getAll(pageNumber, pageSize, sortBy, sortDir),
                HttpStatus.OK);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<CouponDto> getByCode(@PathVariable String code) {
        return new ResponseEntity<>(couponService.getByCode(code), HttpStatus.OK);
    }

    @GetMapping("/validate")
    public ResponseEntity<CouponDto> validateCoupon(
            @RequestParam String code,
            @RequestParam double orderAmount) {
        return new ResponseEntity<>(
                couponService.validateCoupon(code, orderAmount),
                HttpStatus.OK);
    }
}