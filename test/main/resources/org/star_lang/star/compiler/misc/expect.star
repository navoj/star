expecting is package{
  expect has type () => ((%a) => boolean);
  fun expect() is ( (a) => a matches (0,0));
}