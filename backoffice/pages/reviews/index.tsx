import moment from 'moment';
import type { NextPage } from 'next';
import queryString from 'query-string';
import { useEffect, useState } from 'react';
import { Stack, Table } from 'react-bootstrap';
import { SubmitHandler, useForm } from 'react-hook-form';
import { toast } from 'react-toastify';

import RatingSearch from 'modules/rating/components/RatingSearch';
import { RatingSearchForm } from 'modules/rating/models/RatingSearchForm';
import type { Rating } from '../../modules/rating/models/Rating';
import { deleteRatingById, getRatings } from '../../modules/rating/services/RatingService';
import { DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE } from 'constants/Common';
import Pagination from 'common/components/Pagination';
import usePagination from '@commonHooks/usePagination';

const Reviews: NextPage = () => {
  const { register, watch, handleSubmit } = useForm<RatingSearchForm>();
  const [isLoading, setLoading] = useState(false);
  const [ratingList, setRatingList] = useState<Rating[]>([]);
  const [isDelete, setDelete] = useState<boolean>(false);

  const { pageNo, totalPage, setTotalPage, changePage } = usePagination();

  const watchAllFields = watch(); // when pass nothing as argument, you are watching everything

  const handleGetRating = () => {
    getRatings(
      queryString.stringify({
        ...watchAllFields,
        pageNo: pageNo,
        pageSize: DEFAULT_PAGE_SIZE,
        createdFrom: moment(watchAllFields.createdFrom).format(),
        createdTo: moment(watchAllFields.createdTo).format(),
      })
    )
      .then((res) => {
        setRatingList(res.ratingList);
        setTotalPage(res.totalPages);
      })
      .catch((error) => console.log(error));
  };

  useEffect(() => {
    setLoading(true);
    handleGetRating();
    setLoading(false);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [pageNo, isDelete]);

  const onSubmitSearch: SubmitHandler<RatingSearchForm> = async (data) => {
    handleGetRating();
    changePage({ selected: DEFAULT_PAGE_NUMBER });
  };

  const handleDeleteRating = (ratingId: number) => {
    deleteRatingById(ratingId)
      .then(() => {
        toast.success('Delete rating successfully');
        setDelete(!isDelete);
      })
      .catch((error) => console.log(error));
  };

  if (isLoading) return <p>Loading...</p>;
  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2 className="text-danger font-weight-bold mb-3">Product Reviews</h2>
        </div>
      </div>
      {/* Filter */}

      <div className="accordion" id="accordionExample">
        <div className="accordion-item">
          <h2 className="accordion-header" id="headingOne">
            <button
              className="accordion-button"
              type="button"
              data-bs-toggle="collapse"
              data-bs-target="#collapseOne"
              aria-expanded="true"
              aria-controls="collapseOne"
            >
              Search
            </button>
          </h2>
          <div
            id="collapseOne"
            className="accordion-collapse collapse show"
            aria-labelledby="headingOne"
            data-bs-parent="#accordionExample"
          >
            <div className="accordion-body">
              <form onSubmit={handleSubmit(onSubmitSearch)}>
                <RatingSearch register={register} />
              </form>
            </div>
          </div>
        </div>
      </div>

      <Table striped bordered hover>
        <thead>
          <tr>
            <th>ID</th>
            <th style={{ width: '40%' }}>Content</th>
            <th>Product Name</th>
            <th>Customer Id</th>
            <th>Customer Name</th>
            <th>Date Post</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {Array.isArray(ratingList) &&
            ratingList.map((rating) => (
              <tr key={rating.id}>
                <td>{rating.id}</td>
                <td style={{ width: '35%' }}>{rating.content}</td>
                <td>{rating.productName}</td>
                <td>{rating.createdBy}</td>
                <td>
                  {rating.lastName} {rating.firstName}
                </td>
                <td>{moment(rating.createdOn).format('MMMM Do YYYY, h:mm:ss a')}</td>

                <td>
                  <Stack direction="horizontal" gap={3}>
                    <button
                      className="btn btn-outline-danger btn-sm"
                      type="button"
                      onClick={() => handleDeleteRating(rating.id)}
                    >
                      Delete
                    </button>
                  </Stack>
                </td>
              </tr>
            ))}
        </tbody>
      </Table>
      {totalPage > 0 && (
        <Pagination pageNo={pageNo} totalPage={totalPage} onPageChange={changePage} />
      )}
    </>
  );
};

export default Reviews;
