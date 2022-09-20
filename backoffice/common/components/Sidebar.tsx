import '../../styles/Sidebar.module.css'
import Link from 'next/link'

export default function Sidebar(){
  return (
    <nav id="sidebarMenu" className='col-md-3 col-lg-2 d-md-block bg-light sidebar collapse'>
      <div className="position-sticky pt-3">
        <h6 className="sidebar-heading d-flex justify-content-between align-items-center px-3 mt-4 mb-1 text-muted text-uppercase">
          <span>Catalog</span>
          <a className="link-secondary" href="#" aria-label="Add a new report">
            <span data-feather="plus-circle" className="align-text-bottom"></span>
          </a>
        </h6>
        <ul className="nav flex-column mb-2">
          <li className="nav-item">
            <Link href='/catalog/categories'>
              <a className="nav-link">
                <span data-feather="file-text" className="align-text-bottom"></span>
                Categories
              </a>
            </Link>
          </li>
          <li className="nav-item">
            <Link href="/catalog/products">
              <a className="nav-link">
                <span data-feather="file-text" className="align-text-bottom"></span>
                Products
              </a>         
            </Link>
          </li>
          <li className="nav-item">
            <Link href="/catalog/brands">
              <a className="nav-link">
                <span data-feather="file-text" className="align-text-bottom"></span>
                Brands
              </a>
            </Link>
          </li>
          <li className="nav-item">
            <Link href="/catalog/productAttributes">
              <a className="nav-link">
                <span data-feather="file-text" className="align-text-bottom"></span>
                Product Attributes
              </a>
            </Link>
          </li>
        </ul>
      </div>
    </nav>
  );
}
