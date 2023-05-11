import React, { createContext, useContext, useState } from 'react';

type UserInfo = {
  firstName: string;
  lastName: string;
  email: string;
};

export const UserInfoContext = createContext({
  firstName: '',
  lastName: '',
  email: '',
  setUserInfo: (info: UserInfo) => {},
});

export function UserInfoProvider({ children }: React.PropsWithChildren) {
  const [email, setEmail] = useState('');
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');

  const setUserInfo = (info: UserInfo) => {
    setFirstName(info.firstName);
    setLastName(info.lastName);
    setEmail(info.email);
  };

  const value = {
    email,
    firstName,
    lastName,
    setUserInfo,
  };

  return <UserInfoContext.Provider value={value}>{children}</UserInfoContext.Provider>;
}

export const useUserInfoContext = () => {
  const { email, firstName, lastName, setUserInfo } = useContext(UserInfoContext);
  return { email, firstName, lastName, setUserInfo };
};
