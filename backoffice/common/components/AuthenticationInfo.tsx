import Link from 'next/link';
import React, { useEffect, useState } from 'react';
import apiClientService from '@commonServices/ApiClientService';
import { AUTHENTICATION_USER_ENDPOINT } from '@constants/WhitelistedEndpoints';

const baseUrl = AUTHENTICATION_USER_ENDPOINT;

export default function AuthenticationInfo() {
  type AuthenticatedUser = {
    username: string;
  };

  const [authenticatedUser, setAuthenticatedUser] = useState<AuthenticatedUser>({ username: '' });

  async function getAuthenticatedUser() {
    return (await apiClientService.get(baseUrl)).json();
  }

  useEffect(() => {
    getAuthenticatedUser().then((data) => {
      setAuthenticatedUser(data);
    });
  }, []);

  return (
    <>
      Signed in as: <Link href="/profile">{authenticatedUser.username}</Link>{' '}
      <Link href="#">Logout</Link>
    </>
  );
}
