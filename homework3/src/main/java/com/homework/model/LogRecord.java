package com.homework.model;

import java.time.ZonedDateTime;

public record LogRecord(
    String remoteAddr,
    String remoteUser,
    ZonedDateTime timeLocal,
    String method,
    String resource,
    String protocol,
    int status,
    long bodyBytesSent,
    String httpReferer,
    String httpUserAgent
) {}
