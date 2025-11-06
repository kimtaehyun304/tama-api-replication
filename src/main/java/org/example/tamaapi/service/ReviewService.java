package org.example.tamaapi.service;

import lombok.RequiredArgsConstructor;
import org.example.tamaapi.domain.item.Review;
import org.example.tamaapi.command.item.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    //테스트 데이터를 위해
    public void updateCreatedAt(Long reviewId){
        Review review = reviewRepository.findById(reviewId).get();
        review.setCreatedAt(LocalDateTime.now().minusDays(1));
    }
}
