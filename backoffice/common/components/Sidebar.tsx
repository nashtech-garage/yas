import { Button } from 'react-bootstrap';
import { FaMicrosoft, FaMobileAlt, FaTasks, FaUsers } from 'react-icons/fa';
import { AiOutlineGroup } from 'react-icons/ai';
import { SiBrandfolder, SiAnydesk } from 'react-icons/si';
import { TbBuildingStore, TbReceiptTax } from 'react-icons/tb';

import styles from '../../styles/Sidebar.module.css';
import ListGroup from '../items/ListGroup';
import { useRef, useState } from 'react';
import { FiMenu } from 'react-icons/fi';
import { CgClose } from 'react-icons/cg';
import { RiArrowDropDownLine } from 'react-icons/ri';
import { MdLocationCity } from 'react-icons/md';
import {
  COUNTRY_URL,
  INVENTORY_WAREHOUSE_PRODUCTS_URL,
  STATE_OR_PROVINCE_URL,
  TAX_CLASS_URL,
  TAX_RATE_URL,
  WAREHOUSE_URL,
} from '@constants/Common';
type Props = {
  open: boolean;
  setOpen: (value: boolean) => void;
};

let menu_catalog_item_data: any = [
  {
    id: 1,
    name: 'Categories',
    active: true,
    link: '/catalog/categories',
    icon: <FaMicrosoft />,
  },
  {
    id: 2,
    name: 'Products',
    active: false,
    link: '/catalog/products',
    icon: <FaMobileAlt />,
  },
  {
    id: 3,
    name: 'Brands',
    active: false,
    link: '/catalog/brands',
    icon: <SiBrandfolder />,
  },
  {
    id: 4,
    name: 'Product Attributes',
    active: false,
    link: '/catalog/product-attributes',
    icon: <FaTasks />,
  },
  {
    id: 5,
    name: 'Product Options',
    active: false,
    link: '/catalog/product-options',
    icon: <SiAnydesk />,
  },
  {
    id: 6,
    name: 'Attribute Groups',
    active: false,
    link: '/catalog/product-attribute-groups',
    icon: <AiOutlineGroup />,
  },
];

let menu_customer_item_data: any = [
  {
    id: 1,
    name: 'Customers',
    active: false,
    link: '/customers',
    icon: <FaUsers />,
  },
];

let menu_location_item_data: any = [
  {
    id: 1,
    name: 'Countries',
    active: false,
    link: COUNTRY_URL,
    icon: <MdLocationCity />,
  },
  {
    id: 2,
    name: 'State Or Provinces',
    active: false,
    link: STATE_OR_PROVINCE_URL,
    icon: <MdLocationCity />,
  },
];

let menu_tax_item_data: any = [
  {
    id: 1,
    name: 'taxClasses',
    active: false,
    link: TAX_CLASS_URL,
    icon: <TbReceiptTax />,
  },
  {
    id: 2,
    name: 'taxRates',
    active: false,
    link: TAX_RATE_URL,
    icon: <TbReceiptTax />,
  },
];

let menu_inventory_item_data: any = [
  {
    id: 1,
    name: 'ManageWarehouseProducts',
    active: false,
    link: INVENTORY_WAREHOUSE_PRODUCTS_URL,
    icon: <TbBuildingStore />,
  },
  {
    id: 2,
    name: 'manageWarehouses',
    active: false,
    link: WAREHOUSE_URL,
    icon: <TbBuildingStore />,
  },
];

const Burger = ({ open, setOpen }: Props) => {
  return (
    <span id={styles.burgerButton} onClick={() => setOpen(!open)}>
      <FiMenu />
    </span>
  );
};

const Menu = ({ open, setOpen }: Props) => {
  const [showCatalog, setShowCatalog] = useState(true);
  const [showCustomer, setShowCustomer] = useState(true);
  const [showLocation, setShowLocation] = useState(true);
  const [showTax, setShowTax] = useState(true);
  const [showInventory, setShowInventory] = useState(true);

  return (
    <>
      <aside id="drawer" className={`${styles.drawer} ${!open ? styles.drawerHidden : ''}`}>
        <span id={styles.closeButton} onClick={() => setOpen(false)}>
          <CgClose />
        </span>
        <div className="px-3">
          {/* Catalog */}
          <h5 className="font-weight-black text-center text-white mt-5">
            <span>Catalog</span>
            <span
              id={styles.burgerButton}
              onClick={() => {
                setShowCatalog(!showCatalog);
              }}
            >
              <RiArrowDropDownLine className={`${!showCatalog ? styles.rotateButton180 : ''}`} />
            </span>
          </h5>
          <div className="py-4">{showCatalog && <ListGroup data={menu_catalog_item_data} />}</div>
          {/* Customer */}
          <h5 className="font-weight-black text-center text-white">
            <span>Customer</span>
            <span
              id={styles.burgerButton}
              onClick={() => {
                setShowCustomer(!showCustomer);
              }}
            >
              <RiArrowDropDownLine className={`${!showCustomer ? styles.rotateButton180 : ''}`} />
            </span>
          </h5>
          <div className="py-4">{showCustomer && <ListGroup data={menu_customer_item_data} />}</div>

          {/* Location */}
          <h5 className="font-weight-black text-center text-white">
            <span>Location</span>
            <span
              id={styles.burgerButton}
              onClick={() => {
                setShowLocation(!showLocation);
              }}
            >
              <RiArrowDropDownLine className={`${!showLocation ? styles.rotateButton180 : ''}`} />
            </span>
          </h5>
          <div className="py-4">{showLocation && <ListGroup data={menu_location_item_data} />}</div>

          {/* Tax */}
          <h5 className="font-weight-black text-center text-white">
            <span>Tax</span>
            <span
              id={styles.burgerButton}
              onClick={() => {
                setShowTax(!showTax);
              }}
            >
              <RiArrowDropDownLine className={`${!showTax ? styles.rotateButton180 : ''}`} />
            </span>
          </h5>
          <div className="py-4">{showTax && <ListGroup data={menu_tax_item_data} />}</div>

          <h5 className="font-weight-black text-center text-white">
            <span>Inventory</span>
            <span
              id={styles.burgerButton}
              onClick={() => {
                setShowInventory(!showInventory);
              }}
            >
              <RiArrowDropDownLine className={`${!showInventory ? styles.rotateButton180 : ''}`} />
            </span>
          </h5>
          <div className="py-4">
            {showInventory && <ListGroup data={menu_inventory_item_data} />}
          </div>

          {/* Logout */}
          <div className="d-flex justify-content-center ">
            <Button className="btn-danger">Logout</Button>
          </div>
        </div>
      </aside>
    </>
  );
};

export default function Sidebar() {
  const [open, setOpen] = useState(false);

  return (
    <>
      <Burger open={open} setOpen={setOpen} />
      <Menu open={open} setOpen={setOpen} />
    </>
  );
}
