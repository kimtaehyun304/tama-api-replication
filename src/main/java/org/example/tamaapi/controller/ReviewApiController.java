package org.example.tamaapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tamaapi.common.auth.CustomPrincipal;
import org.example.tamaapi.domain.user.Member;
import org.example.tamaapi.domain.item.Review;
import org.example.tamaapi.domain.order.OrderItem;
import org.example.tamaapi.dto.requestDto.CustomPageRequest;
import org.example.tamaapi.dto.requestDto.CustomSort;
import org.example.tamaapi.dto.requestDto.item.save.SaveReviewRequest;
import org.example.tamaapi.dto.responseDto.MyPageReview;
import org.example.tamaapi.dto.responseDto.SimpleResponse;
import org.example.tamaapi.dto.responseDto.review.ReviewResponse;
import org.example.tamaapi.dto.validator.SortValidator;
import org.example.tamaapi.common.exception.MyBadRequestException;
import org.example.tamaapi.command.MemberRepository;
import org.example.tamaapi.command.item.ReviewRepository;
import org.example.tamaapi.command.order.OrderItemRepository;
import org.example.tamaapi.common.util.ErrorMessageUtil;
import org.example.tamaapi.query.MemberQueryRepository;
import org.example.tamaapi.query.item.ReviewQueryRepository;
import org.example.tamaapi.query.order.OrderItemQueryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

import static org.example.tamaapi.common.util.ErrorMessageUtil.NOT_FOUND_ORDER_ITEM;

@RestController
@RequiredArgsConstructor
public class ReviewApiController {

    private final ReviewQueryRepository reviewQueryRepository;
    private final MemberQueryRepository memberQueryRepository;
    private final OrderItemQueryRepository orderItemQueryRepository;

    private final ReviewRepository reviewRepository;

    private final SortValidator sortValidator;

    @GetMapping("/api/reviews")
    //select 필드 너무 많아서 dto 조회 개선 필요
    public MyPageReview<ReviewResponse> reviews(@RequestParam Long colorItemId, @Valid CustomPageRequest customPageRequest, @RequestParam CustomSort sort) {

        if(!sort.getProperty().equals("createdAt"))
            throw new MyBadRequestException("유효한 property가 아닙니다");

        sortValidator.validate(sort);

        Double avgRating = reviewQueryRepository.findAvgRatingByColorItemId(colorItemId).orElse(0.0);

        PageRequest pageRequest = PageRequest.of(customPageRequest.getPage()-1, customPageRequest.getSize()
                , Sort.by(new Sort.Order(sort.getDirection(), sort.getProperty()), new Sort.Order(Sort.Direction.DESC, "id")));
        Page<Review> reviews = reviewQueryRepository.findReviewsByColorItemId(colorItemId, pageRequest);

        List<ReviewResponse> reviewResponses = reviews.stream().map(ReviewResponse::new).toList();
        return new MyPageReview<>(avgRating, reviewResponses, reviews.getPageable(), reviews.getTotalPages(), reviews.getTotalElements());
    }


    @PostMapping("/api/reviews")
    public ResponseEntity<SimpleResponse> saveReview(@Valid @RequestBody SaveReviewRequest saveReviewRequest, @AuthenticationPrincipal CustomPrincipal principal) {
        OrderItem orderItem = orderItemQueryRepository.findWithOrderById(saveReviewRequest.getOrderItemId())
                .orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_ORDER_ITEM));

        if(!Objects.equals(orderItem.getOrder().getMember().getId(), principal.getMemberId()))
            throw new IllegalArgumentException("주문자가 아닙니다.");

        Member member = memberQueryRepository.findById(principal.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessageUtil.NOT_FOUND_MEMBER));
        Review newReview = new Review(orderItem, member, saveReviewRequest.getRating(), saveReviewRequest.getComment(),
                saveReviewRequest.getHeight(), saveReviewRequest.getWeight());
        reviewRepository.save(newReview);
        return ResponseEntity.status(HttpStatus.CREATED).body(new SimpleResponse("저장 완료"));
    }

}
