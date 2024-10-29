import { BreadcrumbModel } from '@/modules/breadcrumb/model/BreadcrumbModel';
import { Button, Container } from 'react-bootstrap';
import BreadcrumbComponent from '../../common/components/BreadcrumbComponent';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import { capturePaymentPaypal } from '@/modules/paymentPaypal/services/PaymentPaypalService';
import SpinnerComponent from '@/common/components/SpinnerComponent';
import Link from 'next/link';
import { CapturePaymentPaypalResponse } from '@/modules/paymentPaypal/models/CapturePaymentPaypalResponse';
import { PaymentPaypalFailureMessage } from '@/modules/paymentPaypal/models/PaymentPaypalFailureMesasge';
import { CapturePaymentRequest } from '@/modules/paymentPaypal/models/CapturePaymentRequest';

const crumb: BreadcrumbModel[] = [
  {
    pageName: 'Home',
    url: '/',
  },
  {
    pageName: 'Complete Payment',
    url: '/complete-payment',
  },
];

const CompletePayment = () => {
  const router = useRouter();
  const { token , paymentMethod } = router.query;
  const [isPaymentSuccess, setIsPaymentSuccess] = useState(false);
  const [isAlreadyPaid, setIsAlreadyPaid] = useState(false);
  const [isCancelPayment, setIsCancelPayment] = useState(false);
  const [isPaymentUnsuccessful, setIsPaymentUnsuccessful] = useState(false);
  const [isShowSpinner, setIsShowSpinner] = useState(false);
  useEffect(() => {
    if (token) {
      const capturePaymentRequestVM : CapturePaymentRequest = {
        token: token as string,
        paymentMethod: paymentMethod as string,
      };
      fetchCapturePaymentPaypal(capturePaymentRequestVM).then();
    }
  }, [router.query]);

  const fetchCapturePaymentPaypal = async (capturePaymentRequestVM: CapturePaymentRequest) => {
    setIsShowSpinner(true);
    const res = await capturePaymentPaypal(capturePaymentRequestVM);
    if (res.paymentStatus == 'COMPLETED') {
      setIsPaymentSuccess(true);
    } else {
      extractPaymentPaypalFailure(res);
    }
    setIsShowSpinner(false);
  };

  const extractPaymentPaypalFailure = (res: CapturePaymentPaypalResponse) => {
    const failureMessage: PaymentPaypalFailureMessage = JSON.parse(res.failureMessage!!);
    const details = failureMessage.details;
    const issue = details[0].issue;
    switch (issue) {
      case 'ORDER_NOT_APPROVED':
        setIsCancelPayment(true);
        break;
      case 'ORDER_ALREADY_CAPTURED':
        setIsAlreadyPaid(true);
        break;
      default:
        setIsPaymentUnsuccessful(true);
    }
  };

  return (
    <>
      <Container>
        <section className="spad">
          <div className="container">
            <BreadcrumbComponent props={crumb} />
            <SpinnerComponent show={isShowSpinner}></SpinnerComponent>
            <div className="complete-payment">
              <div className="payment-result">
                <div hidden={!isPaymentSuccess} className="payment-success">
                  <h1>
                    <i className="bi bi-check2"></i> YOUR ORDER PAID SUCCESSFUL
                  </h1>
                </div>
                <div hidden={!isCancelPayment} className="payment-fail">
                  <h1>
                    <i className="bi bi-exclamation-triangle"></i> YOUR PAYMENT IS CANCELED
                  </h1>
                </div>
                <div hidden={!isAlreadyPaid} className="payment-fail">
                  <h1>
                    <i className="bi bi-exclamation-triangle"></i> YOUR ORDER ALREADY PAID
                  </h1>
                </div>
                <div hidden={!isPaymentUnsuccessful} className="payment-fail">
                  <h1>
                    <i className="bi bi-exclamation-triangle"></i> YOUR ORDER PURCHASE UNSUCCESSFUL{' '}
                  </h1>
                </div>
              </div>
              <div>
                <Link href={'/'}>
                  <Button className="back-to-home-btn">
                    <i className="bi bi-house-fill"></i> CONTINUE SHOPPING
                  </Button>
                </Link>
              </div>
            </div>
          </div>
        </section>
      </Container>
    </>
  );
};

export default CompletePayment;
