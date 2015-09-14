tokenContract is package {
  contract token over %t is {
    tokenToString has type (%t) => string;
  };

  token has type () => ((%t) => string) where token over %t;
  fun token() is tokenToString;
}