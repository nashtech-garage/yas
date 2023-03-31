import Link from 'next/link';
import { useRouter } from 'next/router';
import { useEffect, useRef, useState } from 'react';
import { Image } from 'react-bootstrap';

import {
  data_menu_top_no_login,
  DATA_SEARCH_SUGGESTION,
} from '../../../asset/data/data_header_client';
import promoteImage from '../../images/search-promote-image.png';
import { getCategories } from 'modules/catalog/services/CategoryService';
import { Category } from 'modules/catalog/models/Category';

type Props = {
  children: React.ReactNode;
};

const Header = ({ children }: Props) => {
  const router = useRouter();
  const [cates, setCates] = useState<Category[]>([]);

  const formRef = useRef<HTMLFormElement>(null);
  const [showDropdown, setShowDropdown] = useState(false);
  const [isExpand, setIsExpand] = useState(false);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (formRef.current && !formRef.current.contains(event.target as Node)) {
        setShowDropdown(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  useEffect(() => {
    getCategories().then((res) => {
      setCates(res);
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  console.log(cates);

  const handleInputFocus = () => {
    setShowDropdown(true);
  };

  return (
    <header>
      <div className="container-header">
        <nav className="top-bar">
          <div className="top-bar-container container">
            <div className="left-top-bar">Free shipping for standard order over $100</div>

            <div className="right-top-bar d-flex h-full">
              {data_menu_top_no_login.map((item) => (
                <Link href={item.links} className="d-flex align-items-center px-4" key={item.id}>
                  {item.icon && (
                    <div className="icon-header-bell-question">
                      <i className={item.icon}></i>
                    </div>
                  )}
                  {item.name}
                </Link>
              ))}
              <div className="d-flex align-items-center px-4">{children}</div>
            </div>
          </div>
        </nav>

        <nav className="limiter-menu-desktop container">
          {/* <!-- Logo desktop --> */}
          <Link href="/" className="header-logo me-3">
            <h3 className="text-black">Yas - Storefront</h3>
          </Link>

          {/* <!-- Search --> */}
          <div className="header-search flex-grow-1">
            <div className="search-wrapper">
              <form className="search-form" ref={formRef}>
                <label htmlFor="header-search" className="search-icon">
                  <i className="bi bi-search"></i>
                </label>
                <input id="header-search" className="search-input" onFocus={handleInputFocus} />

                {showDropdown && (
                  <div className="search-auto-complete">
                    <div className="top-widgets">
                      <div className="item-promos">
                        <Link href={''} className="item-promos-link" />
                        <div className="item-promos-keyword">Super product Galaxy A 2023</div>
                        <div className="item-promos-image">
                          <Image src={promoteImage.src} alt="promote image" />
                        </div>
                      </div>
                    </div>
                    <div className="suggestion">
                      {cates.slice(0, isExpand ? 10 : 3).map((item) => (
                        <Link
                          href={`/products?categorySlug=${item.slug}`}
                          className="search-suggestion-item"
                          key={item.slug}
                        >
                          <div className="icon">
                            <i className="bi bi-search"></i>
                          </div>
                          <div className="keyword">{item.name}</div>
                        </Link>
                      ))}
                      <div className="search-suggestion-action">
                        <span onClick={() => setIsExpand((prev) => !prev)}>
                          {isExpand ? (
                            <>
                              Collapsed <i className="bi bi-chevron-up"></i>
                            </>
                          ) : (
                            <>
                              Expand <i className="bi bi-chevron-down"></i>
                            </>
                          )}
                        </span>
                      </div>
                    </div>
                    <div className="bottom-widgets"></div>
                  </div>
                )}

                <button className="search-button">Search</button>
              </form>
            </div>

            <div className="search-suggestion">
              {cates.map((item) => (
                <Link href={`/products?categorySlug=${item.slug}`} key={item.slug}>
                  <span>{item.name}</span>
                </Link>
              ))}
            </div>
          </div>

          <div className="header-actions">
            {/* <!-- Wishlist --> */}
            <Link className={`action-item ${router.pathname === '/' && 'active'}`} href="/">
              <div className="icon-action">
                {router.pathname === '/' ? (
                  <i className="bi bi-house-fill"></i>
                ) : (
                  <i className="bi bi-house"></i>
                )}
              </div>
              <div>Home</div>
            </Link>
          </div>

          {/* <!-- Cart --> */}
          <Link className="header-cart" href="/cart">
            <div className="icon-cart">
              <i className="bi bi-cart3"></i>
            </div>
            <div className="quantity-cart">0</div>
          </Link>
        </nav>

        <nav className="limiter-menu-desktop container"></nav>
      </div>

      {showDropdown && <div className="container-layer"></div>}
      <div className="lower-container"></div>
    </header>
  );
};

export default Header;
