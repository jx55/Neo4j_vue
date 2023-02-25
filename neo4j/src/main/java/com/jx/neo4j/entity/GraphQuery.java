package com.jx.neo4j.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GraphQuery {
    private int labelId;
    private String label;
    private String name;
    private String[] relation;
}
