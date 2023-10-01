package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.request.dto.RequestStatus;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
public class EventRequestStatusUpdateRequest {
    @NotNull
    private final List<Long> requestIds;
    @NotNull
    private final RequestStatus status;
}