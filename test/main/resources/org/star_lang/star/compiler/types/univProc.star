univProc is package {
    type t is t {
        p has type ref action();
    }
    prc main() do {
        def r is t{p:=(() do nothing)};
    }
}