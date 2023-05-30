import { useRouter } from 'next/router';
import { useEffect } from 'react';

import { getProductSlug } from '@/modules/catalog/services/ProductService';

const RedirectPage = () => {
  const router = useRouter();
  const { productId } = router.query;

  useEffect(() => {
    if (productId) {
      getProductSlug(Number(productId))
        .then((res) => {
          router
            .push({
              pathname: `/products/${res.slug}`,
              query: {
                pvid: res.productVariantId,
              },
            })
            .catch((err) => console.log(err));
        })
        .catch((err) => console.log(err));
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [productId]);
};

export default RedirectPage;
