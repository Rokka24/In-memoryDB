package ru.aton.db;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Record {

    private long account;
    private String name;
    private double value;
}
