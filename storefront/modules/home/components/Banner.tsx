import ImageWithFallBack from 'common/components/ImageWithFallback';
import Link from 'next/link';
import { Carousel, Container } from 'react-bootstrap';

import mainBanner1 from '../../../asset/images/main-banner-1.png';
import mainBanner2 from '../../../asset/images/main-banner-2.png';
import mainBanner3 from '../../../asset/images/main-banner-3.png';
import subBanner from '../../../asset/images/sub-banner.jpg';

const listMainBanner = [mainBanner1.src, mainBanner2.src, mainBanner3.src];

const Banner = () => {
  return (
    <Container className="home-banner-container">
      <div className="home-banner-wrapper">
        <div className="main-banner">
          <Carousel>
            {listMainBanner.map((item, index) => (
              <Carousel.Item key={item}>
                <Link href="/products" key={item}>
                  <ImageWithFallBack
                    className="d-block w-100"
                    src={item}
                    alt={`Banner ${index + 1}`}
                  />
                </Link>
              </Carousel.Item>
            ))}
          </Carousel>
        </div>
        <div className="sub-banner">
          <Link href="/products">
            <ImageWithFallBack src={subBanner.src} alt={`sub-banner`} />
          </Link>
        </div>
      </div>
    </Container>
  );
};

export default Banner;
