package com.yas.payment.service;

import com.yas.payment.saga.CompletePaymentSaga;
import com.yas.payment.saga.data.CompletePaymentSagaData;
import com.yas.payment.viewmodel.CapturedPayment;
import io.eventuate.tram.sagas.orchestration.SagaInstanceFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentSagaService {

    private final SagaInstanceFactory sagaInstanceFactory;
    private final CompletePaymentSaga completePaymentSaga;
    public void completePayment(CapturedPayment capturedPayment) {
        CompletePaymentSagaData data = CompletePaymentSagaData
                .builder()
                .capturedPayment(capturedPayment)
                .build();
        sagaInstanceFactory.create(completePaymentSaga, data);
    }
}
