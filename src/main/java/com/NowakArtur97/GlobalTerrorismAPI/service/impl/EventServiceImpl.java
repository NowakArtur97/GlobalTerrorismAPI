package com.NowakArtur97.GlobalTerrorismAPI.service.impl;

import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.BaseRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
class EventServiceImpl extends GenericServiceImpl<EventNode, EventDTO> {

    private final GenericService<TargetNode, TargetDTO> targetService;

    EventServiceImpl(BaseRepository<EventNode> repository, ObjectMapper dtoMapper, GenericService<TargetNode, TargetDTO> targetService) {
        super(repository, dtoMapper);
        this.targetService = targetService;
    }

    @Override
    public EventNode update(EventNode eventNode, EventDTO eventDTO) {

        Long id = eventNode.getId();

        targetService.update(eventNode.getTarget(), eventDTO.getTarget());

        eventNode = dtoMapper.map(eventDTO, EventNode.class);

        eventNode.setId(id);

        return repository.save(eventNode);
    }

    @Override
    public Optional<EventNode> delete(Long id) {

        Optional<EventNode> eventNodeOptional = findById(id);

        if (eventNodeOptional.isPresent()) {

            EventNode eventNode = eventNodeOptional.get();

            targetService.delete(eventNode.getTarget().getId());

            repository.delete(eventNode);
        }

        return eventNodeOptional;
    }
}
