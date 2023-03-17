import Head from 'next/head';
import BreadcrumbComponent from '../../common/components/BreadcrumbComponent';
import { BreadcrumbModel } from '../../modules/breadcrumb/model/BreadcrumbModel';

const crumb: BreadcrumbModel[] = [
  {
    pageName: 'Home',
    url: '/',
  },
  {
    pageName: 'About',
    url: '/about',
  },
];

const About = () => {
  return (
    <>
      <Head>
        <title>About</title>
      </Head>
      <BreadcrumbComponent props={crumb} />
    </>
  );
};

export default About;
