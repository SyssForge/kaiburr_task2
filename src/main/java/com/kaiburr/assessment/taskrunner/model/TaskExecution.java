package com.kaiburr.assessment.taskrunner.model;

import java.time.Instant;

import lombok.Data;

@Data
public class TaskExecution {
    private Instant startTime;
    private Instant endTime;
    private String output;
}
