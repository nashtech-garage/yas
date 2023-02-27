import Head from 'next/head';
import AuthenticationInfo from './AuthenticationInfo';
import { Navbar } from 'react-bootstrap';
import Link from 'next/link';

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
      <Navbar collapseOnSelect bg="dark" variant="dark" style={{ marginBottom: 0 }}>
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
      </Navbar>
      <div className="container container-fluid">
        <main className="col-md-12">{children}</main>
      </div>
      <footer className="footer">
        <Link
          href="https://github.com/nashtech-garage/yas"
          target="_blank"
          rel="noopener noreferrer"
        >
          Powered by {'Yas - 2022 '}
        </Link>
      </footer>
    </>
  );
}
