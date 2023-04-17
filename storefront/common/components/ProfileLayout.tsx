import Head from 'next/head';
import { Container } from 'react-bootstrap';
import UserProfileLeftSideBar from './UserProfileLeftSideBar';
import { BreadcrumbModel } from '@/modules/breadcrumb/model/BreadcrumbModel';
import BreadcrumbComponent from './BreadcrumbComponent';
type Props = {
  children: React.ReactNode;
  breakcrumb: BreadcrumbModel[];
  title?: string;
  menuActive: string;
};

export default function ProfileLayout({ children, breakcrumb, title, menuActive }: Props) {
  return (
    <Container>
      <Head>
        <title>{title ?? 'Profile'}</title>
      </Head>
      <div
        className="d-flex justify-content-between pt-5 col-md-12 mb-2"
        style={{ height: '100px' }}
      >
        <BreadcrumbComponent props={breakcrumb} />
      </div>
      <div className="container mb-5">
        <div className="row">
          <div className="col-md-3 p-0">
            <UserProfileLeftSideBar type={menuActive} />
          </div>
          <div className="col-md-9">{children}</div>
        </div>
      </div>
    </Container>
  );
}
