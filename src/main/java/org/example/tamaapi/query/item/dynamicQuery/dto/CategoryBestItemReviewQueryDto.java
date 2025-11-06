package org.example.tamaapi.query.item.dynamicQuery.dto;

import lombok.Getter;

@Getter
public class CategoryBestItemReviewQueryDto {

    private final Long colorItemId;

    private final Double avgRating;

    private final Long reviewCount;

    public CategoryBestItemReviewQueryDto(Long colorItemId, Double avgRating, Long reviewCount) {
        this.colorItemId = colorItemId;
        this.avgRating = avgRating;
        this.reviewCount = reviewCount;
    }
}
