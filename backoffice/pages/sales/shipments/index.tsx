import type { NextPage } from 'next';
import { useState, useEffect } from 'react';

const Shipments: NextPage = () => {
  const [isLoading, setLoading] = useState(false);

  if (isLoading) return <p>Loading...</p>;
  return <>Shipments</>;
};

export default Shipments;
