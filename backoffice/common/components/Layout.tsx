import Head from 'next/head';
import Link from 'next/link';
import { MouseEventHandler, useState } from 'react';
import { Navbar } from 'react-bootstrap';
import { ToastContainer } from 'react-toastify';
import {
  menu_catalog_item_data,
  menu_customer_item_data,
  menu_inventory_item_data,
  menu_location_item_data,
  menu_promotion_item_data,
  menu_sale_item_data,
  menu_system_item_data,
  menu_tax_item_data,
} from '../../asset/data/sidebar';
import styles from '../../styles/Layout.module.css';
import AuthenticationInfo from './AuthenticationInfo';

interface DataProps {
  id: number;
  name: string;
  link: string;
}
interface MenuProps {
  name: string;
  childActive: string;
  changeMenu: MouseEventHandler;
  changeChildMenu: MouseEventHandler;
}

type Props = {
  children: React.ReactNode;
};

export default function Layout({ children }: Props) {
  const [isSidebarActive, setIsSidebarActive] = useState(false);
  const [menuActive, setMenuActive] = useState('catalog');
  const [childActive, setChildActive] = useState('Brands');

  const sidebarToogleClick = () => {
    setIsSidebarActive((current) => !current);
  };

  const menuChangeClick = (name: any) => {
    setMenuActive(name);
  };

  const childChangeClick = (name: any) => {
    setChildActive(name);
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
          <Sidebar
            changeChildMenu={childChangeClick}
            childActive={childActive}
            changeMenu={menuChangeClick}
            name={menuActive}
          />
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
            <Link
              href="https://github.com/nashtech-garage/yas"
              target="_blank"
              rel="noopener noreferrer"
            >
              Powered by {'Yas - 2022 '}
            </Link>
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

const Sidebar = (menu: MenuProps) => {
  const menuActive = menu.name;
  const changeMenu = (name: any) => {
    menu.changeMenu(name);
  };
  return (
    <div className="p-4 pt-5">
      <h1>
        <Link href="/" className="logo">
          Yas
        </Link>
      </h1>
      <ul className="list-unstyled components mb-5">
        <li
          className={menuActive == 'catalog' ? 'active' : ''}
          onClick={() => changeMenu('catalog')}
        >
          <Link
            href="#catalogSubmenu"
            data-target="#catalogSubmenu"
            data-bs-toggle="collapse"
            aria-controls="catalogSubmenu"
            aria-expanded="false"
            className="dropdown-toggle"
          >
            <span className="fa fa-product-hunt" aria-hidden="true"></span> Catalog
          </Link>
          <ul className="collapse show list-unstyled" id="catalogSubmenu">
            <ListItem
              data={menu_catalog_item_data}
              changeChildMenu={menu.changeChildMenu}
              childActive={menu.childActive}
            />
          </ul>
        </li>
        <li
          className={menuActive == 'customer' ? 'active' : ''}
          onClick={() => changeMenu('customer')}
        >
          <Link
            href="#customerSubmenu"
            data-target="#customerSubmenu"
            data-bs-toggle="collapse"
            aria-controls="customerSubmenu"
            aria-expanded="false"
            className="dropdown-toggle"
          >
            <span className="fa fa-user"></span> Customers
          </Link>
          <ul className="collapse list-unstyled" id="customerSubmenu">
            <ListItem
              data={menu_customer_item_data}
              childActive={menu.childActive}
              changeChildMenu={menu.changeChildMenu}
            />
          </ul>
        </li>

        <li className={menuActive == 'sales' ? 'active' : ''} onClick={() => changeMenu('sales')}>
          <Link
            href="#salesSubmenu"
            data-target="#salesSubmenu"
            data-bs-toggle="collapse"
            aria-controls="salesSubmenu"
            aria-expanded="true"
            className="dropdown-toggle"
          >
            <span className="fa fa-shopping-cart"></span> Sales
          </Link>
          <ul className="collapse list-unstyled" id="salesSubmenu">
            <ListItem
              data={menu_sale_item_data}
              childActive={menu.childActive}
              changeChildMenu={menu.changeChildMenu}
            />
          </ul>
        </li>

        <li
          className={menuActive == 'location' ? 'active' : ''}
          onClick={() => changeMenu('location')}
        >
          <Link
            href="#locationSubmenu"
            data-target="#locationSubmenu"
            data-bs-toggle="collapse"
            aria-controls="locationSubmenu"
            aria-expanded="false"
            className="dropdown-toggle"
          >
            <span className="fa fa-location-arrow"></span> Location
          </Link>
          <ul className="collapse list-unstyled" id="locationSubmenu">
            <ListItem
              data={menu_location_item_data}
              childActive={menu.childActive}
              changeChildMenu={menu.changeChildMenu}
            />
          </ul>
        </li>

        <li
          className={menuActive == 'inventory' ? 'active' : ''}
          onClick={() => changeMenu('inventory')}
        >
          <Link
            href="#inventorySubmenu"
            data-target="#inventorySubmenu"
            data-bs-toggle="collapse"
            aria-controls="inventorySubmenu"
            aria-expanded="false"
            className="dropdown-toggle"
          >
            <span className="fa fa-archive"></span> Inventory
          </Link>
          <ul className="collapse list-unstyled" id="inventorySubmenu">
            <ListItem
              data={menu_inventory_item_data}
              childActive={menu.childActive}
              changeChildMenu={menu.changeChildMenu}
            />
          </ul>
        </li>

        <li className={menuActive == 'promotion' ? 'active' : ''}>
          <Link
            href="#promotionSubmenu"
            data-target="#promotionSubmenu"
            data-bs-toggle="collapse"
            aria-controls="promotionSubmenu"
            aria-expanded="false"
            className="dropdown-toggle"
            onClick={() => changeMenu('promotion')}
          >
            <span className="fa fa-money"></span> Promotion
          </Link>
          <ul className="collapse list-unstyled" id="promotionSubmenu">
            <ListItem
              data={menu_promotion_item_data}
              childActive={menu.childActive}
              changeChildMenu={menu.changeChildMenu}
            />
          </ul>
        </li>

        <li className={menuActive == 'tax' ? 'active' : ''} onClick={() => changeMenu('tax')}>
          <Link
            href="#taxSubmenu"
            data-target="#taxSubmenu"
            data-bs-toggle="collapse"
            aria-controls="taxSubmenu"
            aria-expanded="false"
            className="dropdown-toggle"
          >
            <span className="fa fa-percent"></span> Tax
          </Link>
          <ul className="collapse list-unstyled" id="taxSubmenu">
            <ListItem
              data={menu_tax_item_data}
              childActive={menu.childActive}
              changeChildMenu={menu.changeChildMenu}
            />
          </ul>
        </li>

        <li className={menuActive == 'system' ? 'active' : ''} onClick={() => changeMenu('system')}>
          <Link
            href="#systemSubmenu"
            data-target="#systemSubmenu"
            data-bs-toggle="collapse"
            aria-controls="systemSubmenu"
            aria-expanded="false"
            className="dropdown-toggle"
          >
            <span className="fa fa-cog"></span> System
          </Link>
          <ul className="collapse list-unstyled" id="systemSubmenu">
            <ListItem
              data={menu_system_item_data}
              childActive={menu.childActive}
              changeChildMenu={menu.changeChildMenu}
            />
          </ul>
        </li>
      </ul>
    </div>
  );
};

interface DataListProps {
  data: DataProps[];
  childActive: string;
  changeChildMenu: MouseEventHandler;
}

const ListItem: React.FC<DataListProps> = (props) => {
  const changeChildMenu = (name: any) => {
    props.changeChildMenu(name);
  };
  return (
    <>
      {props.data.map((obj) => (
        <li
          key={obj.id}
          className={obj.name == props.childActive ? 'active' : ''}
          onClick={() => changeChildMenu(obj.name)}
        >
          <Link href={obj.link}>{obj.name}</Link>
        </li>
      ))}
    </>
  );
};
