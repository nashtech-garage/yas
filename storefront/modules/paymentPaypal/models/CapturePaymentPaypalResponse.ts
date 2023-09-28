export type CapturePaymentPaypalResponse = {
  orderId?: number;
  checkoutId?: string;
  amount?: number;
  paymentFee?: number;
  gatewayTransactionId?: string;
  paymentMethod?: string;
  paymentStatus?: string;
  failureMessage?: string;
};
