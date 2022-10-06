import '../../styles/Sidebar.module.css';
import Link from 'next/link';
import { Button } from 'react-bootstrap';
import styles from '../../styles/Sidebar.module.css';
import ListGroup from './ListGroup';
import { FaMicrosoft, FaMobileAlt, FaTasks } from 'react-icons/fa';
import { SiBrandfolder } from 'react-icons/si';

let menu_admin_item_data: any = [
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
];

export default function Sidebar() {
  return (
    <aside id="drawer" className={styles.drawer}>
      <div className="p-3">
        <h5 className="font-weight-black text-center text-white mt-4">
          {/* <FaUsers /> */}
          Catalog
        </h5>
        <div className="py-5">
          <ListGroup data={menu_admin_item_data} />
        </div>
        <div className="d-flex justify-content-center ">
          <Button className="btn-danger">Logout</Button>
        </div>
      </div>
    </aside>
  );
}
