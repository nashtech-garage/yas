# Paypal checkout API integration 
This document contains some key findings about Paypal API and flow that might be helpful to integrate Paypal payment into Yas general payment flow.
## 1. Implementation Notes
### 1.1. Paypal pop-up window with Paypal Smart Checkout Button

This is the flow recommended by Paypal for integration between e-commerce platforms and their checkout service. However, this approach is fairly different from the approach used by other payment providers, which might requires a separate implementation for Paypal.

Paypal recommend the use of the Smart Checkout button through the Paypal JS SDK. To use it, we need to implement the `createOrder()` and `onApprove()` props in the `PaypalButtons` component. 
- The `createOrder()` function should send a request to Backend to initiate a process to create an `Order` object on Paypal's system, then obtain and return the `id` of the created `Order` object
  - In the current design, the `createOrder()` function will be responsible for calling the `POST /order/checkouts/{checkout_id}/process-payment` API and repeatedly loop the `GET /payment/payments/checkout/{checkout_id}` until the payment status is `PROCESSING` and `payment_provider_id` for the order exists.
- The `onApprove()` function will be the entry point for handling the `Order` after the customer has finished authorizing.
  - For Paypal's system, after the customer has fulfiled all the payment information and authorize the payment, the merchant's system (YAS in this case) should send a request to Paypal to `CAPTURE` the payment to complete the transaction.
  - The aforementioned `CAPTURE` action can be triggered synchronously by the `onApprove()` function or asynchronously through an `CHECKOUT.ORDER.APPROVED` event sent to a webhook registered to Paypal.
### 1.2. Redirect flow
We can also implement a redirecting payment flow similar to the one used by Stripe. This is not the recommended flow by Paypal, but it is closer to the flow used by others payment providers which can simplify the integration process for other payment providers as well as provide us with more control over the process.

To do so, when making a `POST /v2/checkout/orders` request, we need to provide a `returnUrl` in the request body (Example will be provided in the API section). We will also need to manually redirect user to the payment page after the `Checkout.Order` object has been created on Paypal's system.

The `CAPTURE` payment action in this case should be triggered asynchronously through an `CHECKOUT.ORDER.APPROVED` event sent to a webhook registered to Paypal.
## 2. Paypal REST API Notes
  Here are some notable Paypal REST API neccessary for the process described above:
#### POST /v1/oauth2/token
- This endpoint is used to obtain an OAUTH access token for Paypal REST API. 

- The request needs to include:
  - Request header: 
    - `Content-Type`: `application/x-www-form-urlencoded`
    - `Basic` `Authorization` header with `base64` encoded valid `client_id` and `client_secret` provided by Paypal
  - Request body:
    - `grant_type` equals `client_credentials`

- Paypal will response with a JSON contains an `access_token`, `token-type`, and `expires_in` field. We can cache this token for reuse, as well as refresh this token when it's about to expired.
#### POST /v2/checkout/orders
- This endpoint is used to create an `Order` which is an object kept by Paypal that provides a basis for the Checkout process.
- The request needs to provide:
  - Request headers:
    - `Authorization` with Bearer Access Token
    - `Content-Type`: `application/json`
  - Request body:
    - Include a json similar to the example below
      ``` JSON
      {   
          "intent": "CAPTURE", // this field is static
          "purchase_units": [
              {
                  "amount": {
                      "currency_code": "USD",
                      "value": "100.00"
                  }
              }
          ]
      }
      ```
    - Each purchase units can be also be clarified with additional optional information similar to the example below
      ``` JSON
      {
        "reference_id": "PUHF",
        "description": "Sporting Goods",
        "invoice_id": "e9fa8d3e-fbf7-4988-b511-0b0d5c60eb89",
        "custom_id": "CUST-HighFashions",
        "soft_descriptor": "HighFashions",
        "amount": {
            "currency_code": "USD",
            "value": "240.00",
            "breakdown": {
              "item_total": {
                "currency_code": "USD",
                "value": "200.00"
            },
            "shipping": {
              "currency_code": "USD",
              "value": "20.00"
            },
            "handling": {
              "currency_code": "USD",
              "value": "10.00"
            },
            "tax_total": {
              "currency_code": "USD",
              "value": "20.00"
            },
              "shipping_discount": {
                "currency_code": "USD",
                "value": "5.00"
            },
            "discount": {
              "currency_code": "USD",
              "value": "5.00"
            }
          } 
        }
      }
      ```
    - For redirect flow, the request should also includes a `returnUrl` specified similar to the example below:
      ``` JSON
      {   
        "intent": "CAPTURE",
        "purchase_units": [
            {
                "amount": {
                    "currency_code": "USD",
                    "value": "100.00"
                }
            }
        ],
        "payment_source": {
            "paypal": {
                "experience_context": {
                    "return_url": "http://storefront/checkout/success",
                    // This is optional and can be used to redirect when customer want to go back
                    "cancel_url": "http://storefront/checkout/cancelled" 
                }
            }
        }
      } 
      ```
- Paypal will then response with a `Checkout.Order` object representation in a JSON as followed:
  ```JSON
  {
      "id": "8PF767230N276553D", // This is the external checkout id
      "status": "PAYER_ACTION_REQUIRED",
      "links": [
          {
              "href": "https://api.sandbox.paypal.com/v2/checkout/orders/8PF767230N276553D",
              "rel": "self",
              "method": "GET"
          },
          {
              "href": "https://www.sandbox.paypal.com/checkoutnow?token=8PF767230N276553D",
              "rel": "payer-action",
              "method": "GET"
          }
      ]
  }
  ```
#### POST /v2/checkout/orders/{order_id}/capture
- This endpoint is used to capture the payment approved by the customer, move the fund to the shop's Paypal account and complete the payment process.
- The request required:
  - Request headers:
    - `Authorization` with Bearer Access Token
    - `Content-Type`: `application/json`
  - Request body: Empty
- Paypal will return a JSON similar to the one below:
  ```JSON
    {
      "id": "8VT93975UH091800G",
      "status": "COMPLETED",
      "payment_source": {
          "paypal": {
              "email_address": "yas@local.com",
              "account_id": "6U6P8866635S4",
              "account_status": "UNVERIFIED",
              "name": {
                  "given_name": "Hoang",
                  "surname": "Nhat"
              },
              "phone_number": {
                  "national_number": "840918942282"
              },
              "address": {
                  "address_line_1": "123 Cong Hoa",
                  "admin_area_2": "Ho Chi Minh",
                  "admin_area_1": "HỒ CHÍ MINH",
                  "country_code": "VN"
              }
          }
      },
      "purchase_units": [
          {
              "reference_id": "default",
              "shipping": {
                  "name": {
                      "full_name": "Nhat Hoang"
                  },
                  "address": {
                      "address_line_1": "123 Cong Hoa",
                      "admin_area_2": "Ho Chi Minh",
                      "admin_area_1": "HỒ CHÍ MINH",
                      "country_code": "VN"
                  }
              },
              "payments": {
                  "captures": [
                      {
                          "id": "1VG31767GC4601156",
                          "status": "COMPLETED",
                          "amount": {
                              "currency_code": "USD",
                              "value": "100.00"
                          },
                          "final_capture": true,
                          "disbursement_mode": "INSTANT",
                          "seller_protection": {
                              "status": "ELIGIBLE",
                              "dispute_categories": [
                                  "ITEM_NOT_RECEIVED",
                                  "UNAUTHORIZED_TRANSACTION"
                              ]
                          },
                          "seller_receivable_breakdown": {
                              "gross_amount": {
                                  "currency_code": "USD",
                                  "value": "100.00"
                              },
                              "paypal_fee": {
                                  "currency_code": "USD",
                                  "value": "5.48"
                              },
                              "net_amount": {
                                  "currency_code": "USD",
                                  "value": "94.52"
                              }
                          },
                          "links": [
                              {
                                  "href": "https://api.sandbox.paypal.com/v2/payments/captures/1VG31767GC4601156",
                                  "rel": "self",
                                  "method": "GET"
                              },
                              {
                                  "href": "https://api.sandbox.paypal.com/v2/payments/captures/1VG31767GC4601156/refund",
                                  "rel": "refund",
                                  "method": "POST"
                              },
                              {
                                  "href": "https://api.sandbox.paypal.com/v2/checkout/orders/8VT93975UH091800G",
                                  "rel": "up",
                                  "method": "GET"
                              }
                          ],
                          "create_time": "2024-09-24T10:17:28Z",
                          "update_time": "2024-09-24T10:17:28Z"
                      }
                  ]
              }
          }
      ],
      "payer": {
          "name": {
              "given_name": "Hoang",
              "surname": "Nhat"
          },
          "email_address": "yas@local.com",
          "payer_id": "6U6P8866635S4",
          "phone": {
              "phone_number": {
                  "national_number": "840918942282"
              }
          },
          "address": {
              "address_line_1": "123 Cong Hoa",
              "admin_area_2": "Ho Chi Minh",
              "admin_area_1": "HỒ CHÍ MINH",
              "country_code": "VN"
          }
      },
      "links": [
          {
              "href": "https://api.sandbox.paypal.com/v2/checkout/orders/8VT93975UH091800G",
              "rel": "self",
              "method": "GET"
          }
      ]
  }
  ```
## 3. Reference
- https://developer.paypal.com/docs/checkout/
- https://developer.paypal.com/docs/api/orders/v2/
- https://stackoverflow.com/questions/68828891/paypal-rest-api-v2-checkout-orders-does-not-redirect-back-after-approval
- https://developer.paypal.com/api/rest/webhooks/event-names/#orders