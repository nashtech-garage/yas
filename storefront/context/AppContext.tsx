import React from 'react';
import { CartProvider, useCartContext } from './CartContext';
import { UserInfoProvider, useUserInfoContext } from './UserInfoContext';

export const AppContext = React.createContext({});

export function AppProvider({ children }: React.PropsWithChildren<{}>) {
  return (
    <UserInfoProvider>
      <CartProvider>{children}</CartProvider>
    </UserInfoProvider>
  );
}

export const useAppContext = () => {
  const cartContext = useCartContext();
  const userInfoContext = useUserInfoContext();

  return {
    ...cartContext,
    ...userInfoContext,
  };
};

export default AppContext;
