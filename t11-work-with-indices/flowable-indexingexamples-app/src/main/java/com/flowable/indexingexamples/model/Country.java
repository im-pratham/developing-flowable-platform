package com.flowable.indexingexamples.model;

import lombok.Value;

@Value
public class Country {
    private String value;

    @Override
    public String toString() {
        return value;
    }
}