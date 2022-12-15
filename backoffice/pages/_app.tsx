import Head from 'next/head';
import Layout from '../common/components/Layout';
import Script from 'next/script';

import 'bootstrap/dist/css/bootstrap.min.css';
import '../styles/globals.css';
import '../styles/common/style.css';
import 'bootstrap-icons/font/bootstrap-icons.css';
import 'react-toastify/dist/ReactToastify.css';

import type { AppProps } from 'next/app';

function MyApp({ Component, pageProps }: AppProps) {
  return (
    <>
      <Head>
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <link
          rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css"
        ></link>
      </Head>
      <Script
        src="https://code.jquery.com/jquery-3.6.2.min.js"
        integrity="sha256-2krYZKh//PcchRtd+H+VyyQoZ/e3EcrkxhM8ycwASPA="
        crossOrigin="anonymous"
        onLoad={() => {
          jQuery('#sidebarCollapse').on('click', function () {
            jQuery('#sidebar').toggleClass('active');
          });
        }}
      />
      <Script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js" />
      <Layout>
        <Component {...pageProps} />
      </Layout>
    </>
  );
}

export default MyApp;
