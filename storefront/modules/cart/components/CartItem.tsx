import Link from 'next/link';
import { FC } from 'react';
import ImageWithFallBack from '@/common/components/ImageWithFallback';
import { CartItemGetDetailsVm } from '@/modules/cart/models/CartItemGetVm';
import { formatPrice } from 'utils/formatPrice';
import { PromotionVerifyResult } from '@/modules/promotion/model/Promotion';

interface CartItemProps {
  item: CartItemGetDetailsVm;
  isLoading: boolean;
  isSelected: boolean;
  promotionApply?: PromotionVerifyResult;
  handleSelectCartItemChange: (productId: number) => void;
  handleDecreaseQuantity: (productId: number) => void;
  handleIncreaseQuantity: (productId: number) => void;
  handleCartItemQuantityOnBlur: (
    productId: number,
    event: React.FocusEvent<HTMLInputElement>
  ) => void;
  handleCartItemQuantityKeyDown: (
    productId: number,
    event: React.KeyboardEvent<HTMLInputElement>
  ) => void;
  handleOpenDeleteConfirmationModal: (productId: number) => void;
}

const calculateProductPrice = (
  item: CartItemGetDetailsVm,
  promotionApply?: PromotionVerifyResult
) => {
  let discount = 0;

  // Check if discountType is 'PERCENTAGE' and calculate accordingly
  if (promotionApply?.discountType === 'PERCENTAGE') {
    discount = (item.price * item.quantity * (promotionApply.discountValue ?? 0)) / 100;
  } else {
    discount = promotionApply?.discountValue ?? 0;
  }

  return formatPrice(item.price * item.quantity - discount);
};

const CartItem: FC<CartItemProps> = ({
  item,
  isLoading,
  isSelected,
  promotionApply,
  handleSelectCartItemChange,
  handleDecreaseQuantity,
  handleIncreaseQuantity,
  handleCartItemQuantityOnBlur,
  handleCartItemQuantityKeyDown,
  handleOpenDeleteConfirmationModal,
}) => {
  return (
    <tr key={item.quantity.toString() + item.productId.toString()}>
      <td>
        <label className="item-checkbox-label" htmlFor="select-item-checkbox">
          {''}
          <input
            className="form-check-input item-checkbox"
            type="checkbox"
            checked={isSelected}
            onChange={() => handleSelectCartItemChange(item.productId)}
          />
        </label>
      </td>
      <td className="cart__product__item d-flex align-items-center">
        <div className="h-100">
          <Link
            href={{
              pathname: '/redirect',
              query: { productId: item.productId },
            }}
          >
            <ImageWithFallBack
              src={item.thumbnailUrl}
              alt={item.productName}
              style={{ width: '120px', height: '120px', cursor: 'pointer' }}
            />
          </Link>
        </div>
        <div className="cart__product__item__title pt-0">
          <Link
            href={{
              pathname: '/redirect',
              query: { productId: item.productId },
            }}
          >
            <h6 className="product-link">{item.productName}</h6>
          </Link>
        </div>
      </td>
      <td className="cart__price">
        {promotionApply?.productId === item.productId && (
          <div style={{ textDecorationLine: 'line-through' }}>{formatPrice(item.price)}</div>
        )}

        <div>
          {promotionApply?.discountType === 'PERCENTAGE'
            ? formatPrice(item.price - item.price * (promotionApply.discountValue / 100)) // Calculate percentage discount
            : formatPrice(
                item.price - (promotionApply?.discountValue ?? 0)
              ) // Fixed discount
          }
        </div>
      </td>
      <td className="cart__quantity">
        <div className="pro-qty">
          <div className={`quantity buttons_added ${isLoading ? 'disabled' : ''}`}>
            <button
              id="minus-button"
              type="button"
              className="minus"
              onClick={() => handleDecreaseQuantity(item.productId)}
              disabled={isLoading}
            >
              -
            </button>

            <input
              id="quanity"
              type="number"
              step="1"
              min="1"
              max=""
              name="quantity"
              defaultValue={item.quantity}
              onBlur={(e) => handleCartItemQuantityOnBlur(item.productId, e)}
              onKeyDown={(e) => handleCartItemQuantityKeyDown(item.productId, e)}
              title="Qty"
              className="input-text qty text"
              disabled={isLoading}
            />
            <button
              id="plus-button"
              type="button"
              className="plus"
              onClick={() => handleIncreaseQuantity(item.productId)}
              disabled={isLoading}
            >
              +
            </button>
          </div>
        </div>
      </td>
      <td className="cart__total">{calculateProductPrice(item, promotionApply)}</td>
      <td className="cart__close">
        {' '}
        <button
          className="remove_product"
          onClick={() => handleOpenDeleteConfirmationModal(item.productId)}
        >
          <i className="bi bi-x-lg fs-5"></i>
        </button>{' '}
      </td>
    </tr>
  );
};

export default CartItem;
