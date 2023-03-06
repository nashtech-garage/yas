import Link from 'next/link';
import { useRef } from 'react';
import { ListGroup as ListGroupBootstrap } from 'react-bootstrap';
import { data_header_client, data_menu_top_no_login } from '../../../asset/data/data_header_client';

type Props = {
  children: React.ReactNode;
};

const Header = ({ children }: Props) => {
  const ref = useRef(null);

  return (
    <>
      <header>
        <div className="container-menu-desktop">
          <div className="wrap-menu-desktop">
            <nav className="top-bar">
              <div className="content-topbar flex-sb-m h-full container">
                <div className="left-top-bar">Free shipping for standard order over $100</div>

                <div className="right-top-bar flex-w h-full main-menu">
                  <div className="right-top-bar flex-w h-full">
                    {data_menu_top_no_login.map((item, index) => (
                      <Link href={item.links} className="flex-c-m trans-04 p-lr-25" key={item.id}>
                        {item.name}
                      </Link>
                    ))}
                    <div className="flex-c-m trans-04 p-lr-25">{children}</div>
                  </div>
                </div>
              </div>
            </nav>
            <nav className="limiter-menu-desktop container">
              {/* <!-- Logo desktop --> */}
              <Link href="/" className="me-5">
                <h3 className="font-weight-black text-black">Yas - Storefront</h3>
              </Link>

              {/* <!-- Menu desktop --> */}
              <div className="menu-desktop main-menu">
                {data_header_client.map((element) => (
                  <Link href={element.link} key={element.id} className="main-menu-li">
                    <ListGroupBootstrap.Item as="li">
                      <span className="ms-3">{element.name}</span>
                    </ListGroupBootstrap.Item>
                  </Link>
                ))}
              </div>

              {/* <!-- Icon header --> */}
              <div className="wrap-icon-header flex-w flex-r-m">
                {/* start search */}
                <form
                  // onSubmit={handleSubmit}
                  className="icon-header-item cl2 hov-cl1 trans-04 js-show-modal-search d-flex align-items-center search-header"
                >
                  <input
                    ref={ref}
                    type="text"
                    className="form-control"
                    id="exampleFormControlInput1"
                    placeholder="Search..."
                    // onChange={(e) => setDataSearch(e.target.value)}
                  ></input>
                  <button type="submit" className="icon-search">
                    <i className="bi bi-search"></i>
                  </button>
                </form>
                {/* end search */}

                <div className="icon-header-item cl2 hov-cl1 trans-04 p-l-22 p-r-11 icon-header-noti js-show-cart">
                  <i className="bi bi-cart3"></i>
                </div>
              </div>
            </nav>
          </div>
        </div>
      </header>
    </>
  );
};

export default Header;
