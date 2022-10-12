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
        <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.9.1/font/bootstrap-icons.css">
        </link>
        <link rel="icon" href="/favicon.ico" />
      </Head>
      <Navbar collapseOnSelect bg="dark" variant="dark">
        <div className="container">
          <Navbar.Brand href="/">Yas - Storefront</Navbar.Brand>
          <Navbar.Toggle />
          <Navbar.Collapse className="justify-content-end">
            <Navbar.Brand>
              <Link href={"/cart"}>
              <i className="bi bi-cart4" type = "button" ></i>
              </Link>
              </Navbar.Brand> &nbsp;
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
        <a href="https://github.com/nashtech-garage/yas" target="_blank" rel="noopener noreferrer">
          Powered by {'Yas - 2022 '}
        </a>
      </footer>
    </>
  );
}
