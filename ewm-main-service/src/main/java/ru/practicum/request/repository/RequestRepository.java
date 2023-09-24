package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.request.dto.RequestStatus;
import ru.practicum.request.model.ParticipationRequest;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    Long countByEvent_IdAndStatus(Long eventId, RequestStatus status);

    Optional<ParticipationRequest> findByRequester_IdAndId(Long requesterId, Long request);

    List<ParticipationRequest> findByRequester_Id(Long requesterId);

    List<ParticipationRequest> findByEvent_Id(Long eventId);

    List<ParticipationRequest> findByEvent_IdAndStatus(Long eventId, RequestStatus status);

    Optional<ParticipationRequest> findByRequester_IdAndEvent_Id(Long requesterId, Long eventId);
}