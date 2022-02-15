package com.epam.esm.gcs.util.impl;

import com.epam.esm.gcs.util.Limiter;
import lombok.Data;

@Data
public class QueryLimiter implements Limiter {

    private static final int DEFAULT_LIMIT = 20;
    private static final int DEFAULT_OFFSET = 0;
    private static final int MIN_LIMIT = 1;
    private static final int MAX_LIMIT = 100;

    private Integer limit;
    private Integer offset;

    public QueryLimiter(Integer limit, Integer offset) {
        this.limit = limit == null || limit < MIN_LIMIT || limit > MAX_LIMIT
                ? DEFAULT_LIMIT : limit;
        this.offset = offset == null || offset < DEFAULT_OFFSET?
                DEFAULT_OFFSET : offset;
    }

}
