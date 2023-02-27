export const concatQueryString = (arr: string[], url: string) => {
  if (arr.length > 0) {
    for (const index in arr) {
      url += index === '0' ? `?${arr[index]}` : `&${arr[index]}`;
    }
  }

  return url;
};
