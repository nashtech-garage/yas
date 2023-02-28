import axios from 'axios';
import queryString from 'query-string';

const axiosClient = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE_PATH,
  headers: {
    'content-type': 'application/json',
  },
  paramsSerializer: {
    encode: (params) => queryString.stringify(params),
  },
});

axiosClient.interceptors.request.use(async (config) => {
  return config;
});

axiosClient.interceptors.response.use(
  (response) => {
    if (response.data) {
      return response.data;
    }
    return response;
  },
  (error) => {
    switch (error.response.status) {
      case 500:
        console.log('Server error');
        break;
      default:
        console.log('Something went wrong');
        console.log('--------------------');
        console.log(`URL: ${error.response.config.url}`);
        console.log(`HTTP Code: ${error.response.status}`);
        console.log(`HTTP Message: ${error.response.statusText}`);
        console.log('-------------------- ');
        return error.response;
    }

    return Promise.reject(error);
  }
);

export default axiosClient;
