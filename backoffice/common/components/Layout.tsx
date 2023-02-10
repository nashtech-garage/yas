import Head from 'next/head';
import { Navbar } from 'react-bootstrap';
import { ToastContainer } from 'react-toastify';
import styles from '../../styles/Layout.module.css';
import AuthenticationInfo from './AuthenticationInfo';
import { useState } from 'react';
import Link from 'next/link';

interface DataProps {
  id: number;
  name: string;
  link: string;
}

let menu_catalog_item_data = [
  {
    id: 1,
    name: 'Brands',
    link: '/catalog/brands',
  },
  {
    id: 2,
    name: 'Categories',
    link: '/catalog/categories',
  },
  {
    id: 3,
    name: 'Products',
    link: '/catalog/products',
  },
  {
    id: 4,
    name: 'Product Options',
    link: '/catalog/product-options',
  },
  {
    id: 5,
    name: 'Product Attributes',
    link: '/catalog/product-attributes',
  },
  {
    id: 6,
    name: 'Product Attribute Groups',
    link: '/catalog/product-attribute-groups',
  },
];

let menu_customer_item_data = [
  {
    id: 1,
    name: 'Customers',
    link: '/customers',
  },
];

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
          <Sidebar />
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

const Sidebar = () => {
  return (
    <div className="p-4 pt-5">
      <h1>
        <Link href="/" className="logo">
          Yas
        </Link>
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
            <ListItem data={menu_catalog_item_data} />
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
            <ListItem data={menu_customer_item_data} />
          </ul>
        </li>
      </ul>
    </div>
  );
};

interface DataListProps {
  data: DataProps[];
}

const ListItem: React.FC<DataListProps> = (props) => {
  return (
    <>
      {props.data.map((obj) => (
        <>
          <li key={obj.id}>
            <Link href={obj.link}>{obj.name}</Link>
          </li>
        </>
      ))}
    </>
  );
};
