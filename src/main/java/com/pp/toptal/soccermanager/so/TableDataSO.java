package com.pp.toptal.soccermanager.so;

import java.util.Collections;
import java.util.List;

public class TableDataSO<R> {

    private Long countTotal;
    private Long countFiltered;

    private List<R> data = Collections.emptyList();

    public Long getCountTotal() {
        return countTotal;
    }
    public void setCountTotal(Long countTotal) {
        this.countTotal = countTotal;
    }

    public Long getCountFiltered() {
        return countFiltered;
    }
    public void setCountFiltered(Long countFiltered) {
        this.countFiltered = countFiltered;
    }

    public List<R> getData() {
        return data;
    }
    public void setData(List<R> data) {
        this.data = data;
    }
    
}