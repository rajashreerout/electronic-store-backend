package com.lcwd.electronic.store.helper;

import com.lcwd.electronic.store.dtos.PageableResponse;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;

import java.util.List;


public class Helper {

    private Helper() {
        // Private constructor to hide implicit public one
    }

    public static String sanitizeHtml(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("<[^>]*>", "").trim();
    }

    public static <U, V> PageableResponse<V> getPageableResponse(Page<U> page, Class<V> type) {
        List<U> entity = page.getContent();
        List<V> dtoList = entity.stream().map(object -> new ModelMapper().map(object, type)).toList();

        PageableResponse<V> response = new PageableResponse<>();
        response.setContent(dtoList);
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setLastPage(page.isLast());

        return response;
    }
}
