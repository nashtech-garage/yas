import Link from 'next/link';
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

  async function getAuthenticationInfo(): Promise<AuthenticationInfoVm> {
    const res = await fetch(`/authentication`);
    return await res.json();
  }

  useEffect(() => {
    getAuthenticationInfo().then((data) => {
      setAuthenticatedInfoVm(data);
      if (data.isAuthenticated) {
        fetch(`/move-cart`);
      }
    });
  }, []);

  return (
    <div className="wrap-auth">
      {authenticatedInfoVm.isAuthenticated ? (
        <div>
          Signed in as: <Link href="#">{authenticatedInfoVm.authenticatedUser.username}</Link>{' '}
          <Link href="/logout">Logout</Link>
        </div>
      ) : (
        <div>
          <Link href="/oauth2/authorization/keycloak">Login</Link>
        </div>
      )}
    </div>
  );
}
