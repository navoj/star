queryee is package{
  queryee has type actor of{
    nameQ has type relation of {
      name has type string;
      age has type integer;
    }
  };
  
  queryee is actor{
    nameQ has type relation of {name has type string; age has type integer};
    nameQ is indexed{
      {name="john";age=23};
      {name="fred";age=64};
    }
  }
}