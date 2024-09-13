# Current state of order flow
## 1. Overview current placing order flow from cart page order
### 1.1. Sequence diagram
!["Complete flow from cart page"](./img/Current%20Order%20flow.svg)
***Diagram 1.** Complete placing order flow until order is placed*

## 2. Create checkout flow
Check out is created to snap shot the state of the cart as check out time
### 2.1. Sequence diagram
!["Sequence diagram for create checkout flow"](./img/Create%20checkout.png)
***Diagram 2.** Zooming in diagram 1 at create checkout flow*

### 2.2. State diagram
!["Checkout state diagram during this sequence"](./img/Checkout%20states.png)
***Diagram 3.** State diagram for checkout during placing order process*


## 3. Create order flow with paypal
This is the main order placing flow at the moment, the COD flow is still in development.
### 3.1. Sequence diagram
!["Sequence diagram for placing order flow"](./img/Create%20order%20flow.png)
***Diagram 4.** Zooming in diagram 1 at create order flow*

### 3.2. State diagram
This is the states transition of an order during this flow. Please reference the sequence diagram to know when each transition happens.
!["Order state diagram during this sequence"](./img/Order%20State%20diagram.png)
***Diagram 5.** State diagram for order during placing order process*
