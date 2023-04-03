import { NextPage } from 'next';

import { Banner, Category, FeaturedProduct } from 'modules/home/components';

const Home: NextPage = () => {
  return (
    <div className="homepage-container">
      <Banner />

      <Category />

      <FeaturedProduct />
    </div>
  );
};
export default Home;
