package com.NowakArtur97.GlobalTerrorismAPI.service.api;

import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.GroupDTO;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.GroupNode;

import java.util.List;

public interface GroupService extends GenericService<GroupNode, GroupDTO> {

    List<EventNode> findAllEventsCausedByGroup(Long id);

    GroupNode addEventToGroup(Long id, EventDTO dto);
}
