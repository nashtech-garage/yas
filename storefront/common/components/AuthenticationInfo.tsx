import React, { useEffect, useState } from 'react';

export default function AuthenticationInfo() {
  type AuthenticatedUser = {
    username: string;
  };

  type AuthenticationInfoVm = {
    isAuthenticated: boolean;
    authenticatedUser: AuthenticatedUser;
  };

  const [authenticatedInfoVm, setAuthenticatedInfoVm] = useState<AuthenticationInfoVm>({
    isAuthenticated: false,
    authenticatedUser: { username: '' },
  });

  async function getAuthenticationInfo() {
    const res = await fetch(`/authentication`);
    return await res.json();
  }

  async function moveCart() {
    await fetch(`/move-cart`);
  }

  useEffect(() => {
    getAuthenticationInfo().then((data) => {
      setAuthenticatedInfoVm(data);
    });
    moveCart();
  }, []);

  return (
    <>
      {authenticatedInfoVm.isAuthenticated ? (
        <div>
          Signed in as: <a href="#">{authenticatedInfoVm.authenticatedUser.username}</a>{' '}
          <a href="/logout">Logout</a>
        </div>
      ) : (
        <div>
          <a href="/oauth2/authorization/keycloak">Login</a>
        </div>
      )}
    </>
  );
}
