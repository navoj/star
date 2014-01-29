univRef is package {
    type t is t {
        parentAction has type for all %u such that (ref action(array of %u, array of %u));
    }
    ph(_,_) do nothing;
    main() do {
        var r is t{parentAction := ph};
    }
}