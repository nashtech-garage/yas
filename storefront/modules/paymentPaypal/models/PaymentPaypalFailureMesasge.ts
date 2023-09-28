export type PaymentPaypalFailureMessage = {
  name: string;
  details: PaymentPaypalFailureMessageDetail[];
};

export type PaymentPaypalFailureMessageDetail = {
  issue: string;
  description: string;
};
