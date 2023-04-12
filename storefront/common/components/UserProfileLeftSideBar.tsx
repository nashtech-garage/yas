import { useRouter } from 'next/router';
import { CgProfile } from 'react-icons/cg';
import { TiContacts } from 'react-icons/ti';

type Props = {
  type: String;
};
const UserProfileLeftSideBar = ({ type }: Props) => {
  const router = useRouter();
  return (
    <aside id="drawer">
      <div
        className="left-menu-element font-weight-black text-center text-white p-3 d-flex justify-content-start align-items-center"
        onClick={() => {
          router.push('/profile');
        }}
        style={{ cursor: 'pointer', background: type == 'profile' ? '#eeeeee' : '' }}
      >
        <CgProfile style={{ fontSize: '30px', marginRight: '10px', color: '#666666' }} />
        <h5>User Profile</h5>
      </div>
      <div
        className="left-menu-element font-weight-black text-center text-white p-3 d-flex justify-content-start align-items-center"
        onClick={() => {
          router.push('/address');
        }}
        style={{ cursor: 'pointer', background: type == 'address' ? '#eeeeee' : '' }}
      >
        <TiContacts style={{ fontSize: '30px', marginRight: '10px', color: '#666666' }} />
        <h5>Address</h5>
      </div>
    </aside>
  );
};

export default UserProfileLeftSideBar;
