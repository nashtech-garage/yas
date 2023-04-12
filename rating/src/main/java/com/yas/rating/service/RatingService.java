package com.yas.rating.service;

import com.yas.rating.exception.AccessDeniedException;
import com.yas.rating.exception.NotFoundException;
import com.yas.rating.exception.ResourceExistedException;
import com.yas.rating.model.Rating;
import com.yas.rating.repository.RatingRepository;
import com.yas.rating.utils.AuthenticationUtils;
import com.yas.rating.utils.Constants;
import com.yas.rating.viewmodel.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
public class RatingService {
    private final RatingRepository ratingRepository;
    private final CustomerService customerService;

    private final OrderService orderService;

    public RatingService(RatingRepository ratingRepository,
                         CustomerService customerService,
                         OrderService orderService) {
        this.ratingRepository = ratingRepository;
        this.customerService = customerService;
        this.orderService = orderService;
    }

    public RatingListVm getRatingListByProductId(Long id, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("createdOn").descending());
        Page<Rating> ratings = ratingRepository.findByProductId(id, pageable);

        List<RatingVm> ratingVmList = new ArrayList<>();
        for (Rating rating : ratings.getContent()) {
            ratingVmList.add(RatingVm.fromModel(rating));
        }

        return new RatingListVm(ratingVmList, ratings.getTotalElements(), ratings.getTotalPages());
    }

    public RatingListVm getRatingListWithFilter(String proName, String cusName,
                                                String message, ZonedDateTime createdFrom,
                                                ZonedDateTime createdTo, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("createdOn").descending());

        Page<Rating> ratings = ratingRepository.getRatingListWithFilter(
                proName.toLowerCase(),
                cusName.toLowerCase(), message.toLowerCase(),
                createdFrom, createdTo, pageable);

        List<RatingVm> ratingVmList = new ArrayList<>();
        for (Rating rating : ratings.getContent()) {
            ratingVmList.add(RatingVm.fromModel(rating));
        }

        return new RatingListVm(ratingVmList, ratings.getTotalElements(), ratings.getTotalPages());
    }

    public RatingVm createRating(RatingPostVm ratingPostVm) {
        String userId = AuthenticationUtils.extractUserId();

        if (!orderService.checkOrderExistsByProductAndUserWithStatus(
                ratingPostVm.productId()
        ).isPresent()) {
            throw new AccessDeniedException(Constants.ERROR_CODE.ACCESS_DENIED);
        }
        if (ratingRepository.existsByCreatedByAndProductId(userId, ratingPostVm.productId())) {
            throw new ResourceExistedException(Constants.ERROR_CODE.RESOURCE_ALREADY_EXISTED);
        }

        Rating rating = new Rating();
        rating.setRatingStar(ratingPostVm.star());
        rating.setContent(ratingPostVm.content());
        rating.setProductId(ratingPostVm.productId());
        rating.setProductName(ratingPostVm.productName());

        CustomerVm customerVm = customerService.getCustomer();
        rating.setLastName(customerVm.lastName());
        rating.setFirstName(customerVm.firstName());

        Rating savedRating = ratingRepository.saveAndFlush(rating);
        return RatingVm.fromModel(savedRating);
    }

    public ResponeStatusVm deleteRating(long ratingId) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.RATING_NOT_FOUND, ratingId));

        ratingRepository.delete(rating);
        return new ResponeStatusVm("Delete Rating", Constants.MESSAGE.SUCCESS_MESSAGE, HttpStatus.OK.toString());
    }

    public Double calculateAverageStar(Long productId) {
        List<Object[]> totalStarsAndRatings = ratingRepository.getTotalStarsAndTotalRatings(productId);
        if (ObjectUtils.isEmpty(totalStarsAndRatings.get(0)[0]))
            return 0.0;
        int totalStars = (Integer.parseInt(totalStarsAndRatings.get(0)[0].toString()));
        int totalRatings = (Integer.parseInt(totalStarsAndRatings.get(0)[1].toString()));

        Double averageStars = (totalStars * 1.0) / totalRatings;
        log.info("Average Star: " + averageStars);
        return averageStars;
    }
}
