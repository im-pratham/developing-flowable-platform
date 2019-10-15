package com.flowable.training.dp.model;//


import java.util.ArrayList;
import java.util.List;

public class StarwarsDataResponse<T> {
    List<T> results;
    String sort;
    long size;
    String previous;
    String next;
    public String getSort() {
        return sort;
    }
    public void setSort(String sort) {
        this.sort = sort;
    }
    public long getSize() {
        return size;
    }
    public void setSize(long size) {
        this.size = size;
    }
    public String getPrevious() {
        return previous;
    }
    public void setPrevious(String previous) {
        this.previous = previous;
    }
    public String getNext() {
        return next;
    }
    public void setNext(String next) {
        this.next = next;
    }


    public StarwarsDataResponse() {
    }

    public List<T> getResults() {
        if(results == null) {
            return new ArrayList<>();
        }
        return this.results;
    }

    public StarwarsDataResponse<T> setResults(List<T> data) {
        this.results = data;
        return this;
    }

}
