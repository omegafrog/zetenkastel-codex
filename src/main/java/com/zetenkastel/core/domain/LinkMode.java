package com.zetenkastel.core.domain;

public enum LinkMode {
    RECOMMEND,
    AUTO_CONNECT;

    public static LinkMode fromNullable(String value) {
        if (value == null || value.isBlank()) {
            return RECOMMEND;
        }
        return LinkMode.valueOf(value.toUpperCase());
    }
}
