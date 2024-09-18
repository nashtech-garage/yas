# Order Processing flow

## Overview

![Order Flow overview](./imgs/orders.drawio.png)

Checkout will be created based on the product items on Shopping Cart
Order will be created based on the Checkout

## Checkout Flow

### Checkout Lifecycle
![Checkout Lifecycle](./imgs/Checkout%20Lifecycle.png)

Checkout is an intemediate object created to **snap shot** and **seal the items** for the order placing process.
 - It will be created with the `NEW` status when the user start the checkout process click "Proceed to checkout" button on cart page. 
 - After an order has been placed, the Checkout has served its purpose, thus its state will be changed to `FULFILLED` and then auto removed after some amount of time (2 days). 
 - In case the checkout process is discontinued and the order is not placed after some amount of time (2 days), the Checkout object will be delete automatically.

### Checkout flow

![Checkout Flow](./imgs/Checkout%20Flow.png)
The Checkout flow happens between when the user begin the checkout process and when the order is placed. This process includes:
- User start this process by navigating to the cart page and click on "Proceed to check out" after choosing all the desired items and their amount.
  - The system will start the checkout process by creating a Checkout object by copying the user's current cart and then clear it. 
- The system then prompt the user for additional information that is necessary for the order placing process.
  - This information includes payment method, delivery method, shipping address, billing address
- The user will then confirmed and placed their order. After that, an Order object will be created and inventory stock will be updated.

## Order Processing

### Order Lifecycle

![Order Lifecycle](./imgs/Order%20Lifecycle.png)
