import StarRatings from 'react-star-ratings';
import Moment from 'react-moment';
import ReactPaginate from 'react-paginate';
import { Rating } from '../models/Rating';

export interface Props {
  ratingList: Rating[] | null;
  pageNo: number;
  totalPages: number;
  totalElements: number;
  handlePageChange: (selectedItem: { selected: number }) => void;
}

export default function RatingList({
  ratingList,
  pageNo,
  totalPages,
  totalElements,
  handlePageChange,
}: Props) {
  return (
    <>
      {totalElements == 0 ? (
        <>No reviews for now</>
      ) : (
        <>
          {ratingList?.map((rating: Rating) => (
            <div className="review-item" key={rating.id}>
              <p style={{ fontWeight: 'bold' }}>
                {rating.lastName == null && rating.firstName == null ? (
                  <>Anonymous</>
                ) : (
                  <>
                    {rating.firstName} {rating.lastName}{' '}
                    <span className="ms-2">
                      <StarRatings
                        rating={rating.star}
                        starRatedColor="#FFBF00"
                        numberOfStars={5}
                        starDimension="16px"
                        starSpacing="1px"
                      />
                    </span>
                  </>
                )}
              </p>
              <div className="d-flex justify-content-between">
                <p className="mx-2">{rating.content}</p>
                <p className="mx-5">
                  <Moment fromNow ago>
                    {rating.createdOn}
                  </Moment>{' '}
                  ago
                </p>
              </div>
            </div>
          ))}
          {/* PAGINATION */}
          <ReactPaginate
            forcePage={pageNo}
            previousLabel={'Previous'}
            nextLabel={'Next'}
            pageCount={totalPages}
            onPageChange={handlePageChange}
            containerClassName={'paginationBtns'}
            previousLinkClassName={'previousBtn'}
            nextClassName={'nextBtn'}
            disabledClassName={'paginationDisabled'}
            activeClassName={'paginationActive'}
          />
        </>
      )}
    </>
  );
}
