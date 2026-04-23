import { getProduct } from '@catalogServices/ProductService';
import { Rating } from 'modules/rating/models/Rating';
import { getLatestRatings } from 'modules/rating/services/RatingService';
import moment from 'moment';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import { Table } from 'react-bootstrap';

const LatestRatings = () => {
  const [ratings, setRatings] = useState<Rating[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const router = useRouter();

  useEffect(() => {
    const fetchRatings = async () => {
      try {
        const res = await getLatestRatings(5);
        setRatings(res);
      } catch (error) {
        console.log(error);
        setRatings([]);
      } finally {
        setLoading(false);
      }
    };

    fetchRatings();
  }, []);

  const handleDetailsClick = (productId: number) => {
    getProduct(productId)
      .then((data) => {
        if (data.parentId) {
          router.push(`/catalog/products/${data.parentId}/edit`);
        } else {
          router.push(`/catalog/products/${productId}/edit`);
        }
      })
      .catch((error) => console.log(error));
  };

  let tableContent;

  if (loading) {
    tableContent = (
      <tr>
        <td colSpan={5}>Loading...</td>
      </tr>
    );
  } else if (!ratings || ratings.length === 0) {
    tableContent = (
      <tr>
        <td colSpan={5}>No Ratings available</td>
      </tr>
    );
  } else {
    tableContent = ratings.map((rating) => (
      <tr key={rating.id}>
        <td className="id-column">{rating.id}</td>
        <td className="identify-column">{rating.productName}</td>
        <td>{rating.content}</td>
        <td className="created-on-column">
          {moment(rating.createdOn).format('MMMM Do YYYY, h:mm:ss a')}
        </td>
        <td className="details-column">
          <button
            className="btn btn-outline-primary btn-sm"
            type="button"
            onClick={() => handleDetailsClick(rating.productId)}
          >
            Details
          </button>
        </td>
      </tr>
    ));
  }

  return (
    <>
      <h2 className="text-danger font-weight-bold mb-3">List of the 5 latest Ratings</h2>
      <Table striped bordered hover>
        <thead>
          <tr>
            <th className="id-column">ID</th>
            <th className="identify-column">Product Name</th>
            <th>Content</th>
            <th className="created-on-column">Created On</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>{tableContent}</tbody>
      </Table>
    </>
  );
};

export default LatestRatings;
