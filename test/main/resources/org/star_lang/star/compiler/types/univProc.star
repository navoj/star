univProc is package {
    type t is t {
        p has type ref action();
    }
    main() do {
        def r is t{p:=(procedure() do nothing)};
    }
}