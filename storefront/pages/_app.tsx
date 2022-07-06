import Head from "next/head";
import Layout from '../components/Layout'

import 'bootstrap/dist/css/bootstrap.min.css';
import '../styles/globals.css'


import type { AppProps } from 'next/app'

function MyApp({ Component, pageProps }: AppProps) {
  return (
    <>
      <Head>
        <meta name="viewport" content="width=device-width, initial-scale=1" />
      </Head>
      <Layout>
        <Component {...pageProps} />
      </Layout>
    </>)
}

export default MyApp
