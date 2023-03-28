import Head from 'next/head';
import { useRouter } from 'next/router';
import { useEffect } from 'react';
import { getProductSlug } from '../../modules/catalog/services/ProductService';

const RedirectPage = () => {
  const router = useRouter();
  const { productId } = router.query;

  useEffect(() => {
    if (productId) {
      getProductSlug(Number(productId)).then((res) => {
        router.push({
          pathname: `/products/${res.slug}`,
          query: {
            pvid: res.productVariantId,
          },
        });
      });
    }
  }, [productId]);
};

export default RedirectPage;
