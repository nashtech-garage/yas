import { useUserInfoContext } from '@/context/UserInfoContext';
import Link from 'next/link';
import { useEffect, useState } from 'react';
import { Dropdown } from 'react-bootstrap';
import { getMyProfile } from '@/modules/profile/services/ProfileService';

export default function AuthenticationInfo() {
  const { setUserInfo } = useUserInfoContext();
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
      // getMyProfile().then((res) => {
      //   setUserInfo(res);
      // });
      setAuthenticatedInfoVm(data);
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <>
      {authenticatedInfoVm.isAuthenticated ? (
        <Dropdown>
          <Dropdown.Toggle
            id="user-dropdown"
            className="bg-transparent"
            style={{ border: 'none', color: '#b2b2b2' }}
          >
            Signed in as:{' ' + authenticatedInfoVm.authenticatedUser.username}
          </Dropdown.Toggle>

          <Dropdown.Menu variant="dark" style={{ backgroundColor: '#222' }}>
            <Dropdown.Item>
              <Link href="/profile" className="d-block h-full">
                Profile
              </Link>
            </Dropdown.Item>
            <Dropdown.Item>
              <Link href="/my-orders" className="d-block h-full">
                My orders
              </Link>
            </Dropdown.Item>
            <Dropdown.Item>
              <Link href="/logout" className="d-block h-full">
                Logout
              </Link>
            </Dropdown.Item>
          </Dropdown.Menu>
        </Dropdown>
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
