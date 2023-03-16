import Head from 'next/head';
import Layout from '../common/components/Layout';

import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap-icons/font/bootstrap-icons.css';

import '../styles/globals.css';
import '../styles/cart.css';
import '../styles/main.css';
import '../styles/util.css';
import '../styles/productDetail.css';
import 'react-toastify/dist/ReactToastify.css';
import { ToastContainer } from 'react-toastify';

import type { AppProps } from 'next/app';

function MyApp({ Component, pageProps }: AppProps) {
  return (
    <>
      <Head>
        <meta name="viewport" content="width=device-width, initial-scale=1" />
      </Head>
      <Layout>
        <Component {...pageProps} />
      </Layout>
      <ToastContainer style={{ marginTop: '70px' }} />
    </>
  );
}

export default MyApp;
