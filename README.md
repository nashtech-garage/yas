## Usage

[Helm](https://helm.sh) must be installed to use the charts.  Please refer to
Helm's [documentation](https://helm.sh/docs) to get started.

Once Helm has been set up correctly, add the repo as follows:
```shell
helm repo add yas https://nashtech-garage.github.io/yas
```

If you had already added this repo earlier, run `helm repo update` to retrieve
the latest versions of the packages.  You can then run `helm search repo yas` to see the charts.

To install the `yas` charts:
```shell
    helm upgrade --install yas-configuration yas/yas-configuration
    helm upgrade --install backoffice-bff yas/backoffice-bff
    helm upgrade --install backoffice-ui yas/backoffice-ui
    helm upgrade --install cart yas/cart
    helm upgrade --install customer yas/customer
    helm upgrade --install inventory yas/inventory
    helm upgrade --install location yas/location
    helm upgrade --install media yas/media
    helm upgrade --install order yas/order
    helm upgrade --install payment-paypal yas/payment-paypal
    helm upgrade --install payment yas/payment
    helm upgrade --install product yas/product
    helm upgrade --install promotion yas/promotion
    helm upgrade --install rating yas/rating
    helm upgrade --install search yas/search
    helm upgrade --install storefront-bff yas/storefront-bff
    helm upgrade --install storefront-ui yas/storefront-ui
    helm upgrade --install swagger-ui yas/swagger-ui
    helm upgrade --install tax yas/tax
```
Detail of values file of chart on [Yas Helm Charts](https://github.com/nashtech-garage/yas/tree/main/charts)

Config for Yas Spring boot microservies on [yas-configuration](https://github.com/nashtech-garage/yas/tree/main/charts/yas-configuration)