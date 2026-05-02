package com.Zxyy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class LoveReport {
    private String title;
    private List<String> suggestions;
}
