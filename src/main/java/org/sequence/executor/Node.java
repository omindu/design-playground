package org.sequence.executor;

public interface Node {

    Node next();
    NodeResponse execute();
}
