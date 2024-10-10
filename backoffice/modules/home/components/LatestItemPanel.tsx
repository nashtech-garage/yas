import LatestOrders from './LatestOrders';
import LatestProducts from './LatestProducts';
import LatestRatings from './LatestRatings';

const LatestItemPanel = () => {
  return (
    <>
      <LatestProducts></LatestProducts>
      <LatestOrders></LatestOrders>
      <LatestRatings></LatestRatings>
    </>
  );
};

export default LatestItemPanel;
