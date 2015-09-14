eqgenerate is package {
    type Node is Node {
        name has type string;
        children has type list of Node;
    };
    prc main() do {
        def node is Node{name=""; children=list of []};
        assert node=node;
        def node2 is Node{name="2"; children=list of [node]};
        assert node!=node2;
        assert node2=node2;
        def node3 is Node{name="3";children=list of [node]};
        assert node2!=node3 and node3=node3;
    }
 }