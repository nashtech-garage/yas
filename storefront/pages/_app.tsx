import type {AppProps} from 'next/app';
import Head from 'next/head';
import {ToastContainer} from 'react-toastify';

import 'bootstrap-icons/font/bootstrap-icons.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'react-toastify/dist/ReactToastify.css';

import '../styles/spinner.css';
import '../styles/completePayment.css';
import '../styles/MyOrder.css';
import '../styles/Footer.css';
import '../styles/Header.css';
import '../styles/HomePage.css';
import '../styles/cart.css';
import '../styles/checkout.css';
import '../styles/form.css';
import '../styles/globals.css';
import '../styles/productDetail.css';
import '../styles/util.css';

import Layout from '@/common/components/Layout';
import {AppProvider} from '@/context/AppContext';

function MyApp({ Component, pageProps }: AppProps) {
  return (
    <AppProvider>
      <Head>
        <meta name="viewport" content="width=device-width, initial-scale=1" />
      </Head>
      <Layout>
        <Component {...pageProps} />
      </Layout>
      <ToastContainer style={{ marginTop: '70px' }} />
    </AppProvider>
  );
}

export default MyApp;
