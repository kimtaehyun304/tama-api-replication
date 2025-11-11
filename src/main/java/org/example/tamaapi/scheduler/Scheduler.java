package org.example.tamaapi.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tamaapi.common.cache.BestItem;
import org.example.tamaapi.common.cache.MyCacheType;
import org.example.tamaapi.domain.item.Category;
import org.example.tamaapi.domain.user.Authority;
import org.example.tamaapi.domain.user.Member;
import org.example.tamaapi.domain.user.coupon.Coupon;
import org.example.tamaapi.domain.user.coupon.MemberCoupon;
import org.example.tamaapi.dto.requestDto.CustomPageRequest;
import org.example.tamaapi.query.MemberCouponQueryRepository;
import org.example.tamaapi.query.MemberQueryRepository;
import org.example.tamaapi.query.item.CategoryQueryRepository;
import org.example.tamaapi.query.item.dynamicQuery.ItemDynamicQueryRepository;
import org.example.tamaapi.query.item.dynamicQuery.dto.CategoryBestItemQueryResponse;
import org.example.tamaapi.command.CouponRepository;
import org.example.tamaapi.command.MemberCouponRepository;

import org.example.tamaapi.command.CacheService;
import org.example.tamaapi.common.util.ErrorMessageUtil;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class Scheduler {

    private final ItemDynamicQueryRepository itemDynamicQueryRepository;
    private final CategoryQueryRepository categoryQueryRepository;

    private final MemberCouponQueryRepository memberCouponQueryRepository;
    private final MemberQueryRepository memberQueryRepository;
    private final CouponRepository couponRepository;
    private final MemberCouponRepository memberCouponRepository;

    private final CacheService cacheService;
    private final JobLauncher jobLauncher;
    private final Job completeOrderJob;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    private void saveBestItemCache(){
        CustomPageRequest customPageRequest = new CustomPageRequest(1,10);

        // 전체, 아우터, 상의, 하의 총 4개 경우 캐시 저장
        for (BestItem bestItem : BestItem.values()) {
            List<Long> categoryIds = new ArrayList<>();

            if(bestItem.getCategoryId() != null){
                Category category = categoryQueryRepository.findWithChildrenById(bestItem.getCategoryId())
                        .orElseThrow(() -> new IllegalArgumentException(ErrorMessageUtil.NOT_FOUND_CATEGORY));
                categoryIds.add(bestItem.getCategoryId());
                categoryIds.addAll(category.getChildren().stream().map(Category::getId).toList());
            }

            // 캐시 저장
            List<CategoryBestItemQueryResponse> bestItems = itemDynamicQueryRepository.findCategoryBestItemWithPaging(categoryIds, customPageRequest);
            cacheService.save(MyCacheType.BEST_ITEM, bestItem.name(), bestItems);
        }
    }

    //사용하지 않는 이미지를 주기적으로 제거하려했는데, 이미지 수정할 때 비동기로 지워주면 됨!

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    //@EventListener(ApplicationReadyEvent.class)
    public void runCompleteOrderJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLocalDate("time", LocalDate.now())
                    .toJobParameters();
            jobLauncher.run(completeOrderJob, jobParameters);
        } catch (Exception e) {
            log.error("자동 구매확정 배치 실행 실패: " + e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    //체험용 계정에 쿠폰 발급 (다 썼을 경우)
    //체험용 계정은 면접관이 기능 체험할때 쓰라고 만든 계정
    public void giveCoupon() {
        Member experienceAccount = memberQueryRepository.findAllByAuthority(Authority.MEMBER).get(1);
        boolean isAllCouponsUsed = !memberCouponQueryRepository.existsByMemberIdAndIsUsedIsFalse(experienceAccount.getId());

        if(isAllCouponsUsed){
            List<Coupon> coupons = couponRepository.findAllById(List.of(1L, 2L, 3L, 4L, 5L, 6L));
            memberCouponRepository.save(new MemberCoupon(coupons.get(0), experienceAccount, false));
            memberCouponRepository.save(new MemberCoupon(coupons.get(1), experienceAccount, false));
            memberCouponRepository.save(new MemberCoupon(coupons.get(2), experienceAccount, false));

            memberCouponRepository.save(new MemberCoupon(coupons.get(3), experienceAccount, false));
            memberCouponRepository.save(new MemberCoupon(coupons.get(4), experienceAccount, false));
            memberCouponRepository.save(new MemberCoupon(coupons.get(5), experienceAccount, false));
        }
    }
}
