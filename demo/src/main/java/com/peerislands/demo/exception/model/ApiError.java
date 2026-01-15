package com.peerislands.demo.exception.model;

import java.time.Instant;

public record ApiError(
    Instant timestamp,
    int status,
    String error,
    String message
) {}

