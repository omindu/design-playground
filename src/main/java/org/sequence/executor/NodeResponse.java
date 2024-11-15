package org.sequence.executor;

public class NodeResponse {

    private final Status status;

    public enum Status {
        COMPLETE, INPUT_REQUIRED, DECISION_REQUIRED
    }

    public NodeResponse(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }
}
