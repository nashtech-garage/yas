export async function createUserAddress(id: number) {
  const response = await fetch(`/api/customer/storefront/user-address/${id}`, {
    method: 'POST',
  });
  return response;
}

export async function getAddressIds() {
  const response = await fetch(`/api/customer/storefront/user-address`);
  return response.json();
}

export async function deleteUserAddress(id: number) {
  const response = await fetch(`/api/customer/storefront/user-address/${id}`, {
    method: 'DELETE',
  });
  return await response;
}
