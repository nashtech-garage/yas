import Link from 'next/link';
import { useEffect, useState } from 'react';

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
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <>
      {authenticatedInfoVm.isAuthenticated ? (
        <div className="d-flex gap-2">
          Signed in as:{' '}
          <Link href="/profile" className="d-block h-full">
            {authenticatedInfoVm.authenticatedUser.username}
          </Link>{' '}
          <Link href="/logout" className="d-block h-full">
            Logout
          </Link>
        </div>
      ) : (
        <div>
          <Link href="/oauth2/authorization/keycloak" className="d-blockh-full">
            Login
          </Link>
        </div>
      )}
    </>
  );
}
