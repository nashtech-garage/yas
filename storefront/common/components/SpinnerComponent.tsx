import { animated, useSpring } from '@react-spring/web';

type Props = {
  show: boolean;
};

export default function SpinnerComponent({ show }: Props) {
  const springs = useSpring({
    from: { transform: 'rotate(0deg)' },
    to: { transform: 'rotate(360deg)' },
    loop: true,
    config: { duration: 1500 },
  });
  return (
    <div className="spinner" hidden={!show}>
      <animated.div className={'spinner-icon'} style={springs} />
    </div>
  );
}
