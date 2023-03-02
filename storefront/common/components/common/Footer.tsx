import Link from 'next/link';
import iconPay01 from '../../../asset/icons/icon-pay-01.jpg';
import iconPay02 from '../../../asset/icons/icon-pay-02.jpg';
import iconPay03 from '../../../asset/icons/icon-pay-03.jpg';
import iconPay04 from '../../../asset/icons/icon-pay-04.jpg';
import iconPay05 from '../../../asset/icons/icon-pay-05.jpg';
import Image from 'next/image';

const Footer = () => {
  return (
    <footer className="bg3 p-t-75 p-b-32">
      <div className="container">
        <div className="row">
          <div className="col-sm-6 col-lg-3 p-b-50">
            <h4 className="stext-301 cl0 p-b-30">Categories</h4>
            <Link href="/product" className="stext-107 cl7 hov-cl1 trans-04">
              Women
            </Link>
          </div>

          <div className="col-sm-6 col-lg-3 p-b-50">
            <h4 className="stext-301 cl0 p-b-30">Help</h4>
            <Link href="/about" className="stext-107 cl7 hov-cl1 trans-04">
              About
            </Link>
          </div>

          <div className="col-sm-6 col-lg-3 p-b-50">
            <h4 className="stext-301 cl0 p-b-30">GET IN TOUCH</h4>

            <p className="stext-107 cl7 size-201">
              Any questions? Let us know in store at 8th floor, 379 Hudson St, New York, NY 10018 or
              call us on (+1) 96 716 6879
            </p>

            <div className="p-t-27">
              <Link
                href="https://github.com/nashtech-garage/yas/"
                target={'_blank'}
                className="fs-18 cl7 hov-cl1 trans-04 m-r-16"
              >
                <i className="bi bi-facebook"></i>
              </Link>

              <Link
                href="https://github.com/nashtech-garage/yas/"
                target={'_blank'}
                className="fs-18 cl7 hov-cl1 trans-04 m-r-16"
              >
                <i className="bi bi-instagram"></i>
              </Link>

              <Link
                href="https://github.com/nashtech-garage/yas/"
                target={'_blank'}
                className="fs-18 cl7 hov-cl1 trans-04 m-r-16"
              >
                <i className="bi bi-github"></i>
              </Link>
            </div>
          </div>

          <div className="col-sm-6 col-lg-3 p-b-50">
            <h4 className="stext-301 cl0 p-b-30">Newsletter</h4>

            <form>
              <div className="wrap-input1 w-full p-b-4">
                <input
                  className="input1 bg-none plh1 stext-107 cl7"
                  type="text"
                  name="email"
                  placeholder="https://github.com/nashtech-garage/yas/"
                />
                <div className="focus-input1 trans-04"></div>
              </div>

              <div className="p-t-18">
                <button className="flex-c-m stext-101 cl0 size-103 bg1 bor1 hov-btn2 p-lr-15 trans-04">
                  <Link
                    href="https://github.com/nashtech-garage/yas/"
                    target={'_blank'}
                    className="text-dark"
                  >
                    Subscribe
                  </Link>
                </button>
              </div>
            </form>
          </div>
        </div>

        <div className="p-t-40">
          <div className="flex-c-m flex-w p-b-18 fs-2 gap-2">
            <Image src={iconPay01} alt="ICON_PAY" width={50} height={30} />
            <Image src={iconPay02} alt="ICON_PAY" width={50} height={30} />
            <Image src={iconPay03} alt="ICON_PAY" width={50} height={30} />
            <Image src={iconPay04} alt="ICON_PAY" width={50} height={30} />
            <Image src={iconPay05} alt="ICON_PAY" width={50} height={30} />
          </div>

          <p className="stext-107 cl6 txt-center">
            Copyright &copy;
            {`${new Date().getFullYear()} `}
            All rights reserved | Made with
            <i className="fa fa-heart-o" aria-hidden="true"></i> by
            <Link href="" target={'_blank'}>
              Colorlib
            </Link>
            &amp; distributed by
            <Link href="" target={'_blank'}>
              Tresor
            </Link>
          </p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
