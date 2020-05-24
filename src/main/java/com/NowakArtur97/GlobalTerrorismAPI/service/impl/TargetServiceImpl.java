package com.NowakArtur97.GlobalTerrorismAPI.service.impl;

import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.BaseRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.TargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TargetServiceImpl extends GenericServiceImpl<TargetNode, TargetDTO> implements TargetService {

    @Autowired
    public TargetServiceImpl(BaseRepository<TargetNode> repository, ObjectMapper dtoMapper) {
        super(repository, dtoMapper);
    }

    @Override
    public boolean isDatabaseEmpty() {

        return repository.count() == 0;
    }
}
