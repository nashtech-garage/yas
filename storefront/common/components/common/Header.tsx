import Link from 'next/link';
import { useCallback, useEffect, useRef, useState } from 'react';

import { data_menu_top_no_login, SUGGESTION_NUMBER } from '../../../asset/data/data_header_client';
import { cartEventSource } from 'modules/cart/services/CartService';
import { getSuggestions } from '@/modules/search/services/SearchService';
import { SearchSuggestion } from '@/modules/rating/models/SearchSuggestion';

type Props = {
  children: React.ReactNode;
};

const Header = ({ children }: Props) => {
  const formRef = useRef<HTMLFormElement>(null);
  const [showDropdown, setShowDropdown] = useState(false);
  const [numberItemIncart, setNumberItemIncart] = useState(0);
  const [searchSuggestions, setSearchSuggestions] = useState<SearchSuggestion[]>([]);
  const [searchInput, setSearchInput] = useState<string>('');
  const _ = require('lodash');

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

  const debounceDropDown = useCallback(
    _.debounce((keyword: string) => fetchSearchSuggestions(keyword), 200),
    []
  );

  const fetchSearchSuggestions = (keyword: string) => {
    getSuggestions(keyword)
      .then((suggestions) => {
        setSearchSuggestions(suggestions.productNames);
      })
      .catch((err) => {
        console.error(`Failed to get search suggestions. ${err}`);
      });
  };

  const handleInputFocus = () => {
    setShowDropdown(true);
  };

  const handleSearchInput = (e: any) => {
    setSearchInput(e.target.value);
    debounceDropDown(e.target.value);
  };

  const handleSuggestionOnClick = (content: string) => {
    setSearchInput(content);
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
                <input
                  id="header-search"
                  className="search-input"
                  onFocus={handleInputFocus}
                  onChange={handleSearchInput}
                  value={searchInput}
                />
                {searchInput !== '' && showDropdown && (
                  <div className="search-auto-complete">
                    <div className="suggestion">
                      {searchSuggestions?.slice(0, SUGGESTION_NUMBER).map((item) => (
                        <div
                          className="search-suggestion-item"
                          key={item.name}
                          onClick={() => handleSuggestionOnClick(item.name)}
                        >
                          <div className="icon">
                            <i className="bi bi-search"></i>
                          </div>
                          <div className="keyword">{item.name}</div>
                        </div>
                      ))}
                    </div>
                    <div className="bottom-widgets"></div>
                  </div>
                )}

                <button className="search-button">Search</button>
              </form>
            </div>
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

      {searchInput !== '' && showDropdown && <div className="container-layer"></div>}
      <div className="lower-container"></div>
    </header>
  );
};

export default Header;
