# Current state of order flow
## Overview current placing order flow from cart page order
### Sequence diagram
!["Complete flow from cart page"](./img/Current%20Order%20flow.svg)

## Create checkout flow
Check out is created to snap shot the state of the cart as check out time
### Sequence diagram
!["Sequence diagram for create checkout flow"](./img/Create%20checkout.png)

## Create order flow with paypal
This is the main order placing flow at the moment, the COD flow is still in development.
### Sequence diagram
!["Sequence diagram for placing order flow"](./img/Create%20order%20flow.png)

### State diagram
This is the states transition of an order during this flow. Please reference the sequence diagram to know when each transition happens.
!["Order state diagram during this sequence"](./img/Order%20State%20diagram.png)
