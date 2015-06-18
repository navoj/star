univRef is package {
    type t is t {
        parentAction has type for all %u such that (ref action(array of %u, array of %u));
    }
    
    prc ph(_,_) do nothing;
    
    prc main() do {
        def r is t{parentAction := ph};
    }
}