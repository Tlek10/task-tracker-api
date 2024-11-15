package com.example.task_tracker_api.api.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AckDto {
    private Boolean answer;
    public static AckDto makeDefault(Boolean answer){
        return builder()
                .answer(answer)
                .build();
    }
}
