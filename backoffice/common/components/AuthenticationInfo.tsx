import Link from 'next/link';
import React, { useEffect, useState } from 'react';

export default function AuthenticationInfo() {
  type AuthenticatedUser = {
    username: string;
  };

  const [authenticatedUser, setAuthenticatedUser] = useState<AuthenticatedUser>({ username: '' });

  async function getAuthenticatedUser() {
    const res = await fetch(`/authentication/user`);
    return await res.json();
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
