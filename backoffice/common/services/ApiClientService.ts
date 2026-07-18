interface RequestOptions {
  method: string;
  headers: {
    [key: string]: string;
  };
  body?: string;
}

const sendRequest = async (
  method: string,
  endpoint: string,
  data: any = null,
  contentType: string | null = null
) => {
  const defaultContentType = 'application/json; charset=UTF-8';
  const requestOptions: RequestOptions = {
    method: method.toUpperCase(),
    headers: {
      'Content-type': contentType ?? defaultContentType,
    },
  };

  if (data) {
    if (data instanceof FormData) {
      delete requestOptions.headers['Content-type'];
    }
    requestOptions.body = data;
  }

  try {
    // Use redirect: 'manual' to intercept cross-origin redirects (e.g. to Keycloak login)
    // before the browser sends a CORS preflight and gets blocked.
    const fetchOptions = method === 'GET'
      ? { redirect: 'manual' as RequestRedirect }
      : { ...requestOptions, redirect: 'manual' as RequestRedirect };

    const response = await fetch(endpoint, fetchOptions);

    // Type 'opaqueredirect' means the server returned a redirect to a cross-origin URL.
    // Navigate through the BFF login entrypoint instead of the API endpoint.
    // Otherwise Spring Security saves the API URL and returns the browser to JSON after login.
    if (response.type === 'opaqueredirect') {
      window.location.href = '/oauth2/authorization/api-client';
      return response;
    }

    return response;
  } catch (error) {
    console.error('API call error:', error);
    throw error;
  }
};

const apiClientService = {
  get: (endpoint: string) => sendRequest('GET', endpoint),
  post: (endpoint: string, data: any, contentType: string | null = null) =>
    sendRequest('POST', endpoint, data, contentType),
  put: (endpoint: string, data: any, contentType: string | null = null) =>
    sendRequest('PUT', endpoint, data, contentType),
  delete: (endpoint: string) => sendRequest('DELETE', endpoint),
};

export default apiClientService;
