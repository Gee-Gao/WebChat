package com.gee.utils;

public interface WorkerIdStrategy {
    void initialize();

    long availableWorkerId();

    void release();
}
