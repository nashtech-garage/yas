import PromotionGeneralInformation from 'modules/promotion/components/PromotionGeneralInformation';
import { PromotionDto } from 'modules/promotion/models/Promotion';
import { cancel, createPromotion } from 'modules/promotion/services/PromotionService';
import { NextPage } from 'next';
import { useRouter } from 'next/router';
import { useState } from 'react';
import { useForm } from 'react-hook-form';

const PromotionCreate: NextPage = () => {
  const router = useRouter();

  const {
    register,
    handleSubmit,
    formState: { errors },
    setValue,
    trigger,
  } = useForm<PromotionDto>();

  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmitPromotion = async (event: PromotionDto) => {
    let promotion: PromotionDto = {
      slug: event.slug,
      couponCode: event.couponCode,
      name: event.name,
      applyTo: event.applyTo,
      startDate: event.startDate,
      endDate: event.endDate,
      discountAmount: event.discountAmount ?? 0,
      discountPercentage: event.discountPercentage ?? 0,
      usageLimit: event.usageLimit,
      usageType: event.usageType,
      discountType: event.discountType,
      description: event.description,
      brandIds: event.brandIds,
      categoryIds: event.categoryIds,
      productIds: event.productIds,
      isActive: event.isActive,
    };

    createPromotion(promotion).then((response) => {
      if (response.status === 201) {
        router.replace('/promotion/manager-promotion');
      }
    });
  };

  const submitForm = () => {
    setIsSubmitting(true);
    handleSubmit(handleSubmitPromotion)();
  };

  return (
    <div className="row mt-5">
      <div className="col-md-8">
        <h2>Create Promotion</h2>
        <form>
          <PromotionGeneralInformation
            register={register}
            errors={errors}
            setValue={setValue}
            trigger={trigger}
            isSubmitting={isSubmitting}
          />
          <div className="mt-5">
            <button
              className="btn btn-primary"
              style={{ marginRight: '20px' }}
              type="button"
              onClick={submitForm}
            >
              Save
            </button>
            <button className="btn btn-danger ml-4" type="button" onClick={cancel}>
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default PromotionCreate;
