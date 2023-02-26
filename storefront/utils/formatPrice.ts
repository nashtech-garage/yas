export function formatPrice(price: number): any {
  const formatter = new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
  });

  return formatter.format(price);
}
