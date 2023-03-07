import StarRatings from 'react-star-ratings';
import { useForm } from 'react-hook-form';

export interface Props {
  ratingStar: number;
  handleChangeRating: (newRatingStar: number) => void;
  handleCreateRating: (data: any) => void;
  contentRating: string;
  setContentRating: (newContentRating: string) => void;
}

export default function PostRating({
  ratingStar,
  handleChangeRating,
  handleCreateRating,
  contentRating,
  setContentRating,
}: Props) {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm();
  return (
    <>
      <form onSubmit={handleSubmit(handleCreateRating)}>
        <h4>Add a review</h4>

        <div className="d-flex">
          <p>Your rating: </p>
          <span className="ms-2">
            <StarRatings
              rating={ratingStar}
              starRatedColor="#FFBF00"
              numberOfStars={5}
              starDimension="16px"
              starSpacing="1px"
              changeRating={handleChangeRating}
            />
          </span>
        </div>

        <div>
          <textarea
            {...register('content', { required: true })}
            onChange={(e) => setContentRating(e.target.value)}
            value={contentRating}
            placeholder="Great..."
            style={{
              width: '100%',
              minHeight: '100px',
              border: '1px solid lightgray',
              padding: 10,
            }}
          />
          {errors.content && <p className="text-danger">Content review is required.</p>}
        </div>

        <div className="d-flex justify-content-end m-3">
          <button type="submit" className="btn btn-primary" style={{ width: '100px' }}>
            Post
          </button>
        </div>
      </form>
    </>
  );
}
