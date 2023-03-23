import StarRatings from 'react-star-ratings';

export interface Props {
  star: number;
}

export default function Star({ star }: Props) {
  return (
    <StarRatings
      rating={star ? star : 0}
      starRatedColor="#fb6e2e"
      numberOfStars={5}
      starDimension="18px"
      starSpacing="0"
      name="rating-header"
    />
  );
}
