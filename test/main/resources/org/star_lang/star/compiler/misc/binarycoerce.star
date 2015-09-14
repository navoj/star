binarycoerce is package {
  prc main() do {
    def s3 is ("my string" as binary) as string;
    assert s3="my string";
  }
}
