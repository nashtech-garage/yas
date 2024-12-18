import ModalDeleteCustom from '@commonItems/ModalDeleteCustom';
import { DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE } from '@constants/Common';
import { PromotionListRequest, PromotionPage } from 'modules/promotion/models/Promotion';
import { deletePromotion, getPromotions } from 'modules/promotion/services/PromotionService';
import { NextPage } from 'next';
import Link from 'next/link';
import { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import Pagination from 'common/components/Pagination';
import usePagination from '@commonHooks/usePagination';

const PromotionList: NextPage = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [promotionPage, setPromotionPage] = useState<PromotionPage>();
  const [couponCode, setCouponCode] = useState<string>('');
  const [promotionName, setPromotionName] = useState<string>('');
  const [showModalDelete, setShowModalDelete] = useState<boolean>(false);
  const [promotionNameWantToDelete, setPromotionNameWantToDelete] = useState<string>('');
  const [promotionIdWantToDelete, setPromotionIdWantToDelete] = useState<number>(-1);

  const { pageNo, totalPage, setTotalPage, changePage } = usePagination({
    initialPageNo: DEFAULT_PAGE_NUMBER,
    initialItemsPerPage: DEFAULT_PAGE_SIZE,
  });

  useEffect(() => {
    setIsLoading(true);
    getPromotionList();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [pageNo]);

  const getPromotionList = () => {
    getPromotions(createRequestParams()).then((data) => {
      setIsLoading(false);
      setTotalPage(data.totalPage);
      setPromotionPage(data);
    });
  };

  const createRequestParams = (): PromotionListRequest => {
    return {
      couponCode: couponCode,
      pageNo: pageNo,
      pageSize: DEFAULT_PAGE_SIZE,
      promotionName: promotionName,
    };
  };

  const convertToStringDate = (date: Date | string) => {
    if (typeof date === 'string') {
      date = new Date(date);
    }
    const month = date.getMonth() + 1;
    const dateNumber = date.getDate();
    return `${date.getFullYear()}-${month > 9 ? month : '0' + month}-${
      dateNumber > 9 ? dateNumber : '0' + dateNumber
    }`;
  };

  const handleClose: any = () => setShowModalDelete(false);

  const handleDeletePromotion = () => {
    deletePromotion(promotionIdWantToDelete).then(() => {
      setShowModalDelete(false);
      getPromotionList();
    });
  };

  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2 className="text-danger font-weight-bold mb-3">Promotions</h2>
        </div>
        <div className="col-md-4 text-right">
          <Link href="/promotion/manager-promotion/create">
            <Button>Add new Promotion</Button>
          </Link>
        </div>
      </div>
      <div className="row mt-5 mb-5">
        <div className="col-md-4">
          <input
            type="text"
            className="form-control"
            placeholder="Coupon Code"
            value={couponCode}
            onChange={(e) => setCouponCode(e.target.value)}
          />
        </div>
        <div className="col-md-4">
          <input
            type="text"
            className="form-control"
            placeholder="Promotion Name"
            value={promotionName}
            onChange={(e) => setPromotionName(e.target.value)}
          />
        </div>
        <div className="col-md-4">
          <Button onClick={getPromotionList}>Search</Button>
        </div>
      </div>
      {!isLoading && (
        <Table striped bordered hover>
          <thead>
            <tr>
              <th>Name</th>
              <th>Coupon code</th>
              <th>Slug</th>
              <th>Description</th>
              <th>Is active</th>
              <th>Discount Type</th>
              <th>Usage count</th>
              <th>Usage limit</th>
              <th>Apply To</th>
              <th>Start date</th>
              <th>End date</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {promotionPage?.promotionDetailVmList?.map((promotion) => (
              <tr key={promotion.id}>
                <td>{promotion.name}</td>
                <td>{promotion.couponCode}</td>
                <td>{promotion.slug}</td>
                <td>{promotion.description}</td>
                <td>{promotion.isActive ? 'Yes' : 'No'}</td>
                <td>{promotion.discountType}</td>
                <td>{promotion.usageCount}</td>
                <td>{promotion.usageLimit}</td>
                <td>{promotion.applyTo}</td>
                <td>{convertToStringDate(promotion.startDate)}</td>
                <td>{convertToStringDate(promotion.endDate)}</td>
                <td>
                  <Link href={`/promotion/manager-promotion/${promotion.id}/edit`}>
                    <button className="btn btn-outline-primary btn-sm" type="button">
                      Edit
                    </button>
                  </Link>
                  &nbsp;
                  <button
                    className="btn btn-outline-danger btn-sm"
                    type="button"
                    onClick={() => {
                      setShowModalDelete(true);
                      setPromotionIdWantToDelete(promotion.id);
                      setPromotionNameWantToDelete(promotion.name);
                    }}
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </Table>
      )}
      <ModalDeleteCustom
        showModalDelete={showModalDelete}
        handleClose={handleClose}
        nameWantToDelete={promotionNameWantToDelete}
        handleDelete={handleDeletePromotion}
        action="delete"
      />
      {totalPage > 1 && (
        <Pagination pageNo={pageNo} totalPage={totalPage} onPageChange={changePage} />
      )}
    </>
  );
};
export default PromotionList;
