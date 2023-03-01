import { useState } from 'react';
import { Figure } from 'react-bootstrap';

export interface IProductImageGalleryProps {
  listImages: string[];
}

export function ProductImageGallery({ listImages }: IProductImageGalleryProps) {
  const MAX_IMAGES = 3;

  const [currentShowUrl, setCurrentShowUrl] = useState<string>(listImages[0]);
  const [currentIndex, setCurrentIndex] = useState<number>(0);
  const [startSliderIndex, setStartSliderIndex] = useState(0);

  const visibleImages = listImages.slice(startSliderIndex, startSliderIndex + MAX_IMAGES);

  const handleNextClick = () => {
    if (startSliderIndex < listImages.length - MAX_IMAGES) {
      setStartSliderIndex(startSliderIndex + 1);
    }
    if (currentIndex < listImages.length - 1) {
      setCurrentIndex(currentIndex + 1);
      setCurrentShowUrl(listImages[currentIndex + 1]);
    }
  };

  const handlePrevClick = () => {
    if (startSliderIndex > 0) {
      setStartSliderIndex(startSliderIndex - 1);
      setCurrentShowUrl(listImages[startSliderIndex + 1]);
    }
    if (currentIndex > 0) {
      setCurrentIndex((prev) => prev - 1);
      setCurrentShowUrl(listImages[currentIndex - 1]);
    }
  };

  return (
    <>
      <Figure className="main-image">
        <Figure.Image width={500} height={500} alt="photo" src={currentShowUrl} />
      </Figure>
      <div className="image-slider">
        <button disabled={currentIndex === 0} className="slider-button" onClick={handlePrevClick}>
          <i className="bi bi-chevron-left"></i>
        </button>

        <Figure className="list-images">
          {visibleImages.map((item, index) => (
            <div
              className={`wrapper ${currentShowUrl === item ? 'active' : ''}`}
              key={`${item}-${index}`}
              onClick={() => {
                setCurrentShowUrl(item);
                setCurrentIndex(listImages.indexOf(item));
              }}
            >
              <Figure.Image width={100} height={100} alt="photo" src={item} />
            </div>
          ))}
        </Figure>

        <button
          disabled={currentIndex === listImages.length - 1}
          className="slider-button"
          onClick={handleNextClick}
        >
          <i className="bi bi-chevron-right"></i>
        </button>
      </div>
    </>
  );
}
