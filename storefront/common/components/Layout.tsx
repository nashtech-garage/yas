import Head from 'next/head';
import AuthenticationInfo from './AuthenticationInfo';
import Footer from './common/Footer';
import Header from './common/Header';

type Props = {
  children: React.ReactNode;
};

export default function Layout({ children }: Props) {
  return (
    <>
      <Head>
        <title>Yas - Storefront</title>
        <meta name="description" content="Yet another shop storefront" />
        <link rel="icon" href="/favicon.ico" />
      </Head>
      <Header>
        <AuthenticationInfo />
      </Header>
      <div className="container container-fluid py-5" style={{ minHeight: '70vh' }}>
        <main className="col-md-12">{children}</main>
      </div>
      <Footer />
    </>
  );
}
