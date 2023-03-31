type Props = {
  title: string;
};

const Banner = ({ title }: Props) => {
  return (
    <>
      <section className="banner-section">
        <div className="container">
          <div className="row">
            <div className="col-lg-12 text-center">
              <div className="banner__text ">
                <h2>{title}</h2>
                <div className="banner__option">
                  <a href="./">Home &nbsp; &nbsp;- </a>
                  <span>{title}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </>
  );
};

export default Banner;
