import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import Checkout from '../checkout';
import { OrderItemPost } from '@/modules/order/models/OrderItemPost';

const RedirectCheckout = () => {
  const router = useRouter();
  const { items, couponCode } = router.query;
  const [orderItems, setOrderItems] = useState<OrderItemPost[]>([]);

  const itemList = items ? JSON.parse(items.toString()) : [];
  useEffect(() => {
    setOrderItems(
      itemList.map((item: any) => ({
        productId: item.productId,
        quantity: item.quantity,
        productName: item.productName,
        productPrice: item.price,
        note: item.note,
        discountAmount: item.discountAmount,
        taxAmount: item.discountAmount,
        taxPercent: item.discountAmount,
      }))
    );
  }, []);

  return <Checkout orderItems={orderItems} couponCode={couponCode?.toString()} />;
};

export default RedirectCheckout;
