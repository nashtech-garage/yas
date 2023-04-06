import Link from 'next/link';
import { useEffect, useRef, useState } from 'react';
import { Image } from 'react-bootstrap';

import {
  data_menu_top_no_login,
  DATA_SEARCH_SUGGESTION,
  SUGGESTION_NUMBER,
} from '../../../asset/data/data_header_client';
import promoteImage from '../../images/search-promote-image.png';
import { cartEventSource } from 'modules/cart/services/CartService';

type Props = {
  children: React.ReactNode;
};

const Header = ({ children }: Props) => {
  const formRef = useRef<HTMLFormElement>(null);
  const [showDropdown, setShowDropdown] = useState(false);
  const [isExpand, setIsExpand] = useState(false);
  const [numberItemIncart, setNumberItemIncart] = useState(0);
  const [searchSuggestions, setSearchSuggestions] = useState<string[]>(["1", "2", "3"]);
  const [searchInput, setSearchInput] = useState<string>('');

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
    cartEventSource((numberItem) => {
      setNumberItemIncart(numberItem);
    });
  }, []);

  const handleInputFocus = () => {
    setShowDropdown(true);
  };

  const handleSearchInput = (e: any) => {
    setSearchInput(e.target.value)

  }

  const handleSearchInputLostFocus = () => {
    setSearchInput("");
    setSearchSuggestions([]);
  }

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
                <input
                  id="header-search"
                  className="search-input"
                  onBlur={handleSearchInputLostFocus}
                  onFocus={handleInputFocus}
                  onChange={handleSearchInput}
                />
                {searchInput.length && showDropdown && (
                  <div className="search-auto-complete">
                    {/* <div className="top-widgets">
                      <div className="item-promos">
                        <Link href={''} className="item-promos-link" />
                        <div className="item-promos-keyword">Super product Galaxy A 2023</div>
                        <div className="item-promos-image">
                          <Image src={promoteImage.src} alt="promote image" />
                        </div>
                      </div>
                    </div> */}
                    <div className="suggestion">
                      {searchSuggestions.slice(0, SUGGESTION_NUMBER).map((item) => (
                        <Link href={'#'} className="search-suggestion-item" key={item}>
                          <div className="icon">
                            <i className="bi bi-search"></i>
                          </div>
                          <div className="keyword">{item}</div>
                        </Link>
                      ))}
                      {/* <div className="search-suggestion-action">
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
                      </div> */}
                    </div>
                    <div className="bottom-widgets"></div>
                  </div>
                )}

                <button className="search-button">Search</button>
              </form>
            </div>

            {/* <div className="search-suggestion">
              {searchSuggestions.map((item) => (
                <Link href={'#'} key={item}>
                  <span>{item}</span>
                </Link>
              ))}
            </div> */}
          </div>

          {/* <!-- Cart --> */}
          <Link className="header-cart" href="/cart">
            <div className="icon-cart">
              <i className="bi bi-cart3"></i>
            </div>
            <div className="quantity-cart">{numberItemIncart}</div>
          </Link>
        </nav>

        <nav className="limiter-menu-desktop container"></nav>
      </div>

      {searchInput.length && showDropdown && <div className="container-layer"></div>}
      <div className="lower-container"></div>
    </header>
  );
};

export default Header;
