export async function getInvoices() {
    const response = await fetch(`/api/order/backoffice/orders`);
    return await response.json();
}

export async function getInvoice(id: number) {
    const response = await fetch(`/api/order/backoffice/orders/${id}`);
    return response.json();
}