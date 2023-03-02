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
      {/* <Navbar collapseOnSelect bg="dark" variant="dark" style={{ marginBottom: 0 }}>
        <div className="container" style={{ marginBottom: 0 }}>
          <Navbar.Brand href="/">Yas - Storefront</Navbar.Brand>
          <Navbar.Collapse style={{ marginLeft: 50 }}>
            <Navbar.Text>
              <Link href={'/products'}>Our Store</Link>
            </Navbar.Text>
          </Navbar.Collapse>

          <Navbar.Toggle />
          <Navbar.Collapse className="justify-content-end">
            <Navbar.Brand>
              <Link href={'/cart'}>
                <button style={{ backgroundColor: 'transparent', border: 0 }}>
                  <i className="bi bi-cart4" style={{ color: 'white' }}></i>
                </button>
              </Link>
            </Navbar.Brand>{' '}
            &nbsp;
            <Navbar.Text>
              <AuthenticationInfo />
            </Navbar.Text>
          </Navbar.Collapse>
        </div>
      </Navbar> */}
      <Header>
        <AuthenticationInfo />
      </Header>
      <div className="container container-fluid py-5">
        <main className="col-md-12">{children}</main>
      </div>
      <Footer />
    </>
  );
}
