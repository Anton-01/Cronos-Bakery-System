package com.cronos.bakery.application.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RecentActivity {
    private String type;
    private String description;
    private LocalDateTime timestamp;
}
