import clsx from 'clsx';
import Image from 'next/image';
import { useState } from 'react';

type Props = {
  src: string;
  alt: string;
  width?: number | `${number}`;
  height?: number | `${number}`;
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
