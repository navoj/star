testable is package {
    contract Testable of %t is {
        toTest has type (%t) => boolean;
    };

    implementation Testable over string is {
        toTest = __test_string;    
    } using {
        __test_string has type (string) => boolean;
        __test_string(s) is size(s) > 5;
    };

    implementation Testable over (list of %t where Testable over %t) is {
        toTest = __test_list;    
    } using {
        -- __test_list has type (list of %t) => boolean;
        __test_list(list of []) is true;
        __test_list(list of [H,..T]) is toTest(H) and __test_list(T);
    };    
}