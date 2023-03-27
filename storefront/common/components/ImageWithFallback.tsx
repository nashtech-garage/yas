import clsx from 'clsx';
import { Image } from 'react-bootstrap';
import { useState } from 'react';

type Props = {
  src: string;
  alt: string;
  width?: number | string;
  height?: number | string;
  className?: string;
  fallBack?: string;
  style?: React.CSSProperties;
};

const ImageWithFallBack = ({
  width = 500,
  height = 500,
  src,
  alt,
  className,
  style,
  fallBack: customFallBack = '/static/images/default-fallback-image.png',
  ...props
}: Props) => {
  const [fallBack, setFallBack] = useState<string | null>(null);

  return (
    <Image
      width={width}
      height={height}
      style={style}
      className={clsx(className)}
      src={fallBack || src}
      alt={alt}
      {...props}
      onError={(event) => {
        event.currentTarget.onerror = null;
        setFallBack(customFallBack);
      }}
    />
  );
};

export default ImageWithFallBack;
