package com.NowakArtur97.GlobalTerrorismAPI.controller.group;

import com.NowakArtur97.GlobalTerrorismAPI.model.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GroupService;
import com.NowakArtur97.GlobalTerrorismAPI.tag.GroupTag;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@Api(tags = {GroupTag.RESOURCE})
@ApiResponses(value = {@ApiResponse(code = 401, message = "Permission to the resource is prohibited"),
        @ApiResponse(code = 403, message = "Access to the resource is prohibited")})
@RequiredArgsConstructor
@Slf4j
public class GroupEventsController {

    protected final GroupService service;

    private final RepresentationModelAssemblerSupport<EventNode, EventModel> eventsModelAssembler;

    protected final PagedResourcesAssembler<EventNode> eventsPagedResourcesAssembler;

    @GetMapping(path = "/{id}/events")
    public ResponseEntity<PagedModel<?>> findGroupEvents(@PathVariable("id") Long id, Pageable pageable) {

        List<EventNode> eventsCausedByGroup = service.findAllEventsCausedByGroup(id);

        if (pageable.getOffset() >= eventsCausedByGroup.size()) {
            return new ResponseEntity<>(PagedModel.NO_PAGE, HttpStatus.OK);
        }

        int startIndex = (int) pageable.getOffset();
        int endIndex = (int) ((pageable.getOffset() + pageable.getPageSize()) > eventsCausedByGroup.size() ?
                eventsCausedByGroup.size() :
                pageable.getOffset() + pageable.getPageSize());

        List<EventNode> subList = eventsCausedByGroup.subList(startIndex, endIndex);

        PageImpl<EventNode> pages = new PageImpl<>(subList, pageable, eventsCausedByGroup.size());

        PagedModel<EventModel> pagedModel = eventsPagedResourcesAssembler.toModel(pages, eventsModelAssembler);

        return new ResponseEntity<>(pagedModel, HttpStatus.OK);
    }
}