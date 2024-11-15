package com.yas.payment.service;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.payment.mapper.CreatePaymentProviderMapper;
import com.yas.payment.mapper.PaymentProviderMapper;
import com.yas.payment.mapper.UpdatePaymentProviderMapper;
import com.yas.payment.model.PaymentProvider;
import com.yas.payment.repository.PaymentProviderRepository;
import com.yas.payment.utils.Constants;
import com.yas.payment.viewmodel.paymentprovider.CreatePaymentVm;
import com.yas.payment.viewmodel.paymentprovider.MediaVm;
import com.yas.payment.viewmodel.paymentprovider.PaymentProviderVm;
import com.yas.payment.viewmodel.paymentprovider.UpdatePaymentVm;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Payment provider service.
 */
@Slf4j
@Service
public class PaymentProviderService {

    private final MediaService mediaService;
    private final PaymentProviderRepository paymentProviderRepository;

    private final PaymentProviderMapper paymentProviderMapper;
    private final CreatePaymentProviderMapper createPaymentProviderMapper;
    private final UpdatePaymentProviderMapper updatePaymentProviderMapper;

    public PaymentProviderService(
        MediaService mediaService,
        PaymentProviderMapper paymentProviderMapper,
        CreatePaymentProviderMapper createPaymentProviderMapper,
        UpdatePaymentProviderMapper updatePaymentProviderMapper,
        PaymentProviderRepository paymentProviderRepository
    ) {
        this.mediaService = mediaService;
        this.paymentProviderMapper = paymentProviderMapper;
        this.createPaymentProviderMapper = createPaymentProviderMapper;
        this.updatePaymentProviderMapper = updatePaymentProviderMapper;
        this.paymentProviderRepository = paymentProviderRepository;
    }

    /**
     * Create payment provider.
     *
     * @param createPaymentVm {@link CreatePaymentVm} payment provider request.
     * @return {@link PaymentProviderVm} created payment provider.
     */
    @Transactional
    public PaymentProviderVm create(CreatePaymentVm createPaymentVm) {
        var pm = createPaymentProviderMapper.toModel(createPaymentVm);
        var pmProvider = paymentProviderRepository.save(pm);
        return createPaymentProviderMapper.toVmResponse(pmProvider);
    }

    /**
     * Update payment provider.
     *
     * @param updatePaymentVm {@link UpdatePaymentVm} payment provider request
     * @return {@link PaymentProviderVm} updated payment provider.
     */
    @Transactional
    public PaymentProviderVm update(UpdatePaymentVm updatePaymentVm) {
        var paymentProvider = findByIdOrElseThrow(updatePaymentVm.getId());
        updatePaymentProviderMapper.partialUpdate(paymentProvider, updatePaymentVm);
        paymentProviderRepository.save(paymentProvider);
        return updatePaymentProviderMapper.toVmResponse(paymentProvider);
    }

    public String getAdditionalSettingsByPaymentProviderId(String paymentProviderId) {
        return findByIdOrElseThrow(paymentProviderId).getAdditionalSettings();
    }

    /**
     * Get enabled payment providers.
     *
     * @return enabled providers.
     */
    public List<PaymentProviderVm> getEnabledPaymentProviders(Pageable pageable) {
        var providers = paymentProviderRepository.findByIsEnabledTrue(pageable);
        if (providers.isEmpty()) {
            log.debug("No enabled payment provider found");
            return Collections.emptyList();
        }

        final Map<Long, MediaVm> mediaVmMap = mediaService.getMediaVmMap(providers);
        return providers.stream()
            .map(provider -> toPaymentProviderVm(provider, mediaVmMap))
            .toList();
    }

    private PaymentProvider findByIdOrElseThrow(String paymentProviderId) {
        return paymentProviderRepository.findById(paymentProviderId)
            .orElseThrow(
                () -> new NotFoundException(Constants.ErrorCode.PAYMENT_PROVIDER_NOT_FOUND, paymentProviderId)
            );
    }

    private PaymentProviderVm toPaymentProviderVm(PaymentProvider provider, Map<Long, MediaVm> mediaVmMap) {
        var vm = paymentProviderMapper.toVm(provider);
        var iconUrl = mediaVmMap.getOrDefault(provider.getMediaId(), MediaVm.builder().build()).getUrl();
        vm.setIconUrl(iconUrl);
        return vm;
    }

}
