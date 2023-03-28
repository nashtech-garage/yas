import clsx from 'clsx';
import { useState } from 'react';
import { Image } from 'react-bootstrap';
import styles from '../../styles/ChooseImage.module.css';

type ChooseImageCommonProps = {
  id: string;
  iconStyle?: React.CSSProperties;
  wrapperStyle?: React.CSSProperties;
  actionStyle?: React.CSSProperties;
  url: string;
  onDeleteImage: React.MouseEventHandler;
};

type Image = {
  id: number;
  url: string;
};

const ChooseImageCommon = ({
  id,
  iconStyle,
  wrapperStyle,
  actionStyle,
  url,
  onDeleteImage,
}: ChooseImageCommonProps) => {
  return (
    <>
      <div style={wrapperStyle} className={styles['product-image']}>
        <div style={actionStyle} className={styles['actions']}>
          <label htmlFor={id} className={styles['icon']} style={iconStyle}>
            <i className="bi bi-arrow-repeat"></i>
          </label>

          <div
            style={iconStyle}
            className={clsx(styles['icon'], styles['delete'])}
            onClick={onDeleteImage}
          >
            <i className="bi bi-x-lg"></i>
          </div>
        </div>
        <Image width={'100%'} src={url} alt="image" />
      </div>
    </>
  );
};

export default ChooseImageCommon;
