import Head from 'next/head';
import { Navbar } from 'react-bootstrap';
import { ToastContainer } from 'react-toastify';
import styles from '../../styles/Layout.module.css';
import AuthenticationInfo from './AuthenticationInfo';
import { useRef, useState } from 'react';
import Link from 'next/link';

type Props = {
  children: React.ReactNode;
};

export default function Layout({ children }: Props) {
  const [isSidebarActive, setIsSidebarActive] = useState(false);

  const sidebarToogleClick = () => {
    setIsSidebarActive((current) => !current);
  };

  return (
    <>
      <Head>
        <title>Yas - Backoffice</title>
        <meta name="description" content="Yet another shop backoffice" />
        <link rel="icon" href="/favicon.ico" />
      </Head>
      <div className="wrapper d-flex align-items-stretch">
        <nav id="sidebar" className={isSidebarActive ? 'active' : ''}>
          <div className="p-4 pt-5">
            <h1>
              <a href="/" className="logo">
                Yas
              </a>
            </h1>
            <ul className="list-unstyled components mb-5">
              <li className="active">
                <a
                  href="#catalogSubmenu"
                  data-target="#catalogSubmenu"
                  data-bs-toggle="collapse"
                  aria-controls="catalogSubmenu"
                  aria-expanded="false"
                  className="dropdown-toggle"
                >
                  <span className="fa fa-product-hunt" aria-hidden="true"></span> Catalog
                </a>
                <ul className="collapse show list-unstyled" id="catalogSubmenu">
                  <li>
                    <Link href="/catalog/brands">Brands</Link>
                  </li>
                  <li>
                    <Link href="/catalog/categories">Catgegories</Link>
                  </li>
                  <li>
                    <Link href="/catalog/products">Products</Link>
                  </li>
                  <li>
                    <Link href="/catalog/product-options">Product Options</Link>
                  </li>
                  <li>
                    <Link href="/catalog/product-attributes">Product Attributes</Link>
                  </li>
                  <li>
                    <Link href="/catalog/product-attribute-groups">Product Attributes Group</Link>
                  </li>
                </ul>
              </li>
              <li>
                <a
                  href="#customerSubmenu"
                  data-target="#customerSubmenu"
                  data-bs-toggle="collapse"
                  aria-controls="customerSubmenu"
                  aria-expanded="false"
                  className="dropdown-toggle"
                >
                  <span className="fa fa-user"></span> Cusomters
                </a>
                <ul className="collapse list-unstyled" id="customerSubmenu">
                  <li>
                    <Link href="/customers">Customers</Link>
                  </li>
                </ul>
              </li>
            </ul>
          </div>
        </nav>
        <div id="content">
          <Navbar collapseOnSelect bg="dark" variant="dark" className={styles.header}>
            <button
              type="button"
              id="sidebarCollapse"
              onClick={sidebarToogleClick}
              className="btn btn-primary"
            >
              <i className="fa fa-bars"></i>
              <span className="sr-only">Toggle Menu</span>
            </button>
            <Navbar.Collapse className="justify-content-end">
              <Navbar.Text>
                <AuthenticationInfo />
              </Navbar.Text>
            </Navbar.Collapse>
          </Navbar>
          <main className="container-fluid" id={styles.main}>
            {children}
          </main>
          <footer className={styles.footer}>
            <a
              href="https://github.com/nashtech-garage/yas"
              target="_blank"
              rel="noopener noreferrer"
            >
              Powered by {'Yas - 2022 '}
            </a>
          </footer>
        </div>
      </div>
      <ToastContainer
        position="top-center"
        autoClose={3000}
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
        theme="colored"
      />
    </>
  );
}
