import clsx from 'clsx';
import { useEffect, useState } from 'react';
import { Image } from 'react-bootstrap';

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
  const [srcUrl, setSrcUrl] = useState<string>(src);

  useEffect(() => {
    setSrcUrl(src);
    setFallBack(null);
  }, [src]);

  return (
    <Image
      width={width}
      height={height}
      style={style}
      className={clsx(className)}
      src={fallBack || srcUrl}
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
