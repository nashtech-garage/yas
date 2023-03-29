import { NextPage } from 'next';

import { Category, FeaturedProduct } from 'modules/home/components';

const Home: NextPage = () => {
  return (
    <div className="homepage-container">
      <Category />

      <FeaturedProduct />
    </div>
  );
};
export default Home;
