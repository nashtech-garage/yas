import Link from 'next/link';
import React, { useEffect, useState } from 'react';
import apiClientService from '../services/ApiClientService';

export default function AuthenticationInfo() {
  type AuthenticatedUser = {
    username: string;
  };

  const [authenticatedUser, setAuthenticatedUser] = useState<AuthenticatedUser>({ username: '' });

  async function getAuthenticatedUser() {
    return (await apiClientService.get(`/authentication/user`)).json();
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
