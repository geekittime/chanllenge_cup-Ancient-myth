package com.example.myth.entity.dto;

public class AiResultChoices {
    private AiResultDelta delta;
    private Integer index;

    public AiResultDelta getDelta() {
        return delta;
    }

    public void setDelta(AiResultDelta delta) {
        this.delta = delta;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
