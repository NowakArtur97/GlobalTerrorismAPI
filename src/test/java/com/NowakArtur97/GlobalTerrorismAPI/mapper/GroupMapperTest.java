package com.NowakArtur97.GlobalTerrorismAPI.mapper;

import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.GroupDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.GroupModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.GroupNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.EventBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.GroupBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.TargetBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("ObjectMapper_Tests")
class GroupMapperTest {

    private ObjectMapper objectMapper;

    @Mock
    private ModelMapper modelMapper;

    private static TargetBuilder targetBuilder;
    private static EventBuilder eventBuilder;
    private static GroupBuilder groupBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        targetBuilder = new TargetBuilder();
        eventBuilder = new EventBuilder();
        groupBuilder = new GroupBuilder();
    }

    @BeforeEach
    private void setUp() {

        objectMapper = new ObjectMapperImpl(modelMapper);
    }

    @Test
    void when_map_group_node_to_model_should_return_model() {

        Long targetId = 1L;
        Long eventId = 1L;
        Long groupId = 1L;

        String group = "group";

        String summary = "summary";
        String motive = "motive";
        boolean isPartOfMultipleIncidents = true;
        boolean isSuccessful = true;
        boolean isSuicidal = true;

        TargetNode targetNode1 = (TargetNode) targetBuilder.withId(targetId).withTarget("target" + targetId)
                .build(ObjectType.NODE);
        TargetModel targetModel1 = (TargetModel) targetBuilder.withId(targetId).withTarget("target" + targetId)
                .build(ObjectType.MODEL);

        EventNode eventNode1 = (EventNode) eventBuilder.withId(eventId).withSummary(summary + eventId)
                .withMotive(motive + eventId).withIsPartOfMultipleIncidents(isPartOfMultipleIncidents)
                .withIsSuccessful(isSuccessful).withIsSuicidal(isSuicidal).withTarget(targetNode1)
                .build(ObjectType.NODE);

        EventModel eventModel1 = (EventModel) eventBuilder.withId(eventId).withSummary(summary + eventId)
                .withMotive(motive + eventId).withIsPartOfMultipleIncidents(isPartOfMultipleIncidents)
                .withIsSuccessful(isSuccessful).withIsSuicidal(isSuicidal).withTarget(targetModel1)
                .build(ObjectType.MODEL);

        targetId++;
        eventId++;
        isPartOfMultipleIncidents = false;
        isSuccessful = false;
        isSuicidal = false;

        TargetNode targetNode2 = (TargetNode) targetBuilder.withId(targetId).withTarget("target" + targetId)
                .build(ObjectType.NODE);
        TargetModel targetModel2 = (TargetModel) targetBuilder.withId(targetId).withTarget("target" + targetId)
                .build(ObjectType.MODEL);

        EventNode eventNode2 = (EventNode) eventBuilder.withId(eventId).withSummary(summary + eventId)
                .withMotive(motive + eventId).withIsPartOfMultipleIncidents(isPartOfMultipleIncidents)
                .withIsSuccessful(isSuccessful).withIsSuicidal(isSuicidal).withTarget(targetNode2)
                .build(ObjectType.NODE);

        EventModel eventModel2 = (EventModel) eventBuilder.withId(eventId).withSummary(summary + eventId)
                .withMotive(motive + eventId).withIsPartOfMultipleIncidents(isPartOfMultipleIncidents)
                .withIsSuccessful(isSuccessful).withIsSuicidal(isSuicidal).withTarget(targetModel2)
                .build(ObjectType.MODEL);

        GroupNode groupNode = (GroupNode) groupBuilder.withId(groupId).withName(group).withEventsCaused(List.of(eventNode1, eventNode2)).build(ObjectType.NODE);

        GroupModel groupModelExpected = (GroupModel) groupBuilder.withEventsCaused(List.of(eventModel1, eventModel2)).build(ObjectType.MODEL);

        when(modelMapper.map(groupNode, GroupModel.class)).thenReturn(groupModelExpected);

        GroupModel groupModelActual = objectMapper.map(groupNode, GroupModel.class);

        assertAll(
                () -> assertEquals(groupModelExpected.getId(), groupModelActual.getId(),
                        () -> "should return group model with id: " + groupModelExpected.getId() + ", but was: "
                                + groupModelActual.getId()),
                () -> assertEquals(groupModelExpected.getName(), groupModelActual.getName(),
                        () -> "should return group model with name: " + groupModelExpected.getName() + ", but was: "
                                + groupModelActual.getName()),

                () -> assertEquals(groupModelExpected.getEventsCaused().get(0).getSummary(), groupModelActual.getEventsCaused().get(0).getSummary(),
                        () -> "should return group's event node with summary: " + groupModelExpected.getEventsCaused().get(0).getSummary() + ", but was: "
                                + groupModelActual.getEventsCaused().get(0).getSummary()),
                () -> assertEquals(groupModelExpected.getEventsCaused().get(0).getMotive(), groupModelActual.getEventsCaused().get(0).getMotive(),
                        () -> "should return group's event node with motive: " + groupModelExpected.getEventsCaused().get(0).getMotive() + ", but was: "
                                + groupModelActual.getEventsCaused().get(0).getMotive()),
                () -> assertEquals(groupModelExpected.getEventsCaused().get(0).getDate(), groupModelActual.getEventsCaused().get(0).getDate(),
                        () -> "should return group's event node with date: " + groupModelExpected.getEventsCaused().get(0).getDate() + ", but was: "
                                + groupModelActual.getEventsCaused().get(0).getDate()),
                () -> assertEquals(groupModelExpected.getEventsCaused().get(0).getIsPartOfMultipleIncidents(),
                        groupModelActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents(),
                        () -> "should return group's event node which was part of multiple incidents: "
                                + groupModelExpected.getEventsCaused().get(0).getIsPartOfMultipleIncidents() + ", but was: "
                                + groupModelActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents()),
                () -> assertEquals(groupModelExpected.getEventsCaused().get(0).getIsSuccessful(), groupModelActual.getEventsCaused().get(0).getIsSuccessful(),
                        () -> "should return group's event node which was successful: " + groupModelExpected.getEventsCaused().get(0).getIsSuccessful()
                                + ", but was: " + groupModelActual.getEventsCaused().get(0).getIsSuccessful()),
                () -> assertEquals(groupModelExpected.getEventsCaused().get(0).getIsSuicidal(), groupModelActual.getEventsCaused().get(0).getIsSuicidal(),
                        () -> "should return group's event node which was suicidal: " + groupModelExpected.getEventsCaused().get(0).getIsSuicidal()
                                + ", but was: " + groupModelActual.getEventsCaused().get(0).getIsSuicidal()),
                () -> assertNotNull(groupModelActual.getEventsCaused().get(0).getTarget(),
                        () -> "should return group's event node with not null target, but was: null"),
                () -> assertEquals(groupModelExpected.getEventsCaused().get(0).getTarget().getId(), groupModelActual.getEventsCaused().get(0).getTarget().getId(),
                        () -> "should return group's event node target with id: " + groupModelExpected.getEventsCaused().get(0).getTarget().getId() + ", but was: "
                                + groupModelActual.getEventsCaused().get(0).getTarget().getId()),
                () -> assertEquals(groupModelExpected.getEventsCaused().get(0).getTarget().getTarget(), groupModelActual.getEventsCaused().get(0).getTarget().getTarget(),
                        () -> "should return group's event node with target: " + groupModelExpected.getEventsCaused().get(0).getTarget().getTarget()
                                + ", but was: " + groupModelActual.getEventsCaused().get(0).getTarget().getTarget()),

                () -> assertEquals(groupModelExpected.getEventsCaused().get(1).getSummary(), groupModelActual.getEventsCaused().get(1).getSummary(),
                        () -> "should return group's event node with summary: " + groupModelExpected.getEventsCaused().get(1).getSummary() + ", but was: "
                                + groupModelActual.getEventsCaused().get(1).getSummary()),
                () -> assertEquals(groupModelExpected.getEventsCaused().get(1).getMotive(), groupModelActual.getEventsCaused().get(1).getMotive(),
                        () -> "should return group's event node with motive: " + groupModelExpected.getEventsCaused().get(1).getMotive() + ", but was: "
                                + groupModelActual.getEventsCaused().get(1).getMotive()),
                () -> assertEquals(groupModelExpected.getEventsCaused().get(1).getDate(), groupModelActual.getEventsCaused().get(1).getDate(),
                        () -> "should return group's event node with date: " + groupModelExpected.getEventsCaused().get(1).getDate() + ", but was: "
                                + groupModelActual.getEventsCaused().get(1).getDate()),
                () -> assertEquals(groupModelExpected.getEventsCaused().get(1).getIsPartOfMultipleIncidents(),
                        groupModelActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents(),
                        () -> "should return group's event node which was part of multiple incidents: "
                                + groupModelExpected.getEventsCaused().get(1).getIsPartOfMultipleIncidents() + ", but was: "
                                + groupModelActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents()),
                () -> assertEquals(groupModelExpected.getEventsCaused().get(1).getIsSuccessful(), groupModelActual.getEventsCaused().get(1).getIsSuccessful(),
                        () -> "should return group's event node which was successful: " + groupModelExpected.getEventsCaused().get(1).getIsSuccessful()
                                + ", but was: " + groupModelActual.getEventsCaused().get(1).getIsSuccessful()),
                () -> assertEquals(groupModelExpected.getEventsCaused().get(1).getIsSuicidal(), groupModelActual.getEventsCaused().get(1).getIsSuicidal(),
                        () -> "should return group's event node which was suicidal: " + groupModelExpected.getEventsCaused().get(1).getIsSuicidal()
                                + ", but was: " + groupModelActual.getEventsCaused().get(1).getIsSuicidal()),
                () -> assertNotNull(groupModelActual.getEventsCaused().get(1).getTarget(),
                        () -> "should return group's event node with not null target, but was: null"),
                () -> assertEquals(groupModelExpected.getEventsCaused().get(1).getTarget().getId(), groupModelActual.getEventsCaused().get(1).getTarget().getId(),
                        () -> "should return group's event node target with id: " + groupModelExpected.getEventsCaused().get(1).getTarget().getId() + ", but was: "
                                + groupModelActual.getEventsCaused().get(1).getTarget().getId()),
                () -> assertEquals(groupModelExpected.getEventsCaused().get(1).getTarget().getTarget(), groupModelActual.getEventsCaused().get(1).getTarget().getTarget(),
                        () -> "should return group's event node with target: " + groupModelExpected.getEventsCaused().get(1).getTarget().getTarget()
                                + ", but was: " + groupModelActual.getEventsCaused().get(1).getTarget().getTarget()),
                () -> verify(modelMapper, times(1)).map(groupNode, GroupModel.class),
                () -> verifyNoMoreInteractions(modelMapper));
    }

    @Test
    void when_map_group_dto_to_node_should_return_node() {

        long targetId = 1L;
        long eventId = 1L;

        String group = "group";

        String summary = "summary";
        String motive = "motive";
        boolean isPartOfMultipleIncidents = true;
        boolean isSuccessful = true;
        boolean isSuicidal = true;

        TargetDTO targetDTO1 = (TargetDTO) targetBuilder.withTarget("target" + targetId).build(ObjectType.DTO);
        TargetNode targetNode1 = (TargetNode) targetBuilder.withId(null).withTarget("target" + targetId)
                .build(ObjectType.NODE);

        EventDTO eventDTO1 = (EventDTO) eventBuilder.withSummary(summary + eventId)
                .withMotive(motive + eventId).withIsPartOfMultipleIncidents(isPartOfMultipleIncidents)
                .withIsSuccessful(isSuccessful).withIsSuicidal(isSuicidal).withTarget(targetDTO1)
                .build(ObjectType.DTO);

        EventNode eventNode1 = (EventNode) eventBuilder.withId(null).withSummary(summary + eventId)
                .withMotive(motive + eventId).withIsPartOfMultipleIncidents(isPartOfMultipleIncidents)
                .withIsSuccessful(isSuccessful).withIsSuicidal(isSuicidal).withTarget(targetNode1)
                .build(ObjectType.NODE);

        targetId++;
        eventId++;
        isPartOfMultipleIncidents = false;
        isSuccessful = false;
        isSuicidal = false;

        TargetDTO targetDTO2 = (TargetDTO) targetBuilder.withTarget("target" + targetId).build(ObjectType.DTO);
        TargetNode targetNode2 = (TargetNode) targetBuilder.withId(null).withTarget("target" + targetId)
                .build(ObjectType.NODE);

        EventDTO eventDTO2 = (EventDTO) eventBuilder.withSummary(summary + eventId)
                .withMotive(motive + eventId).withIsPartOfMultipleIncidents(isPartOfMultipleIncidents)
                .withIsSuccessful(isSuccessful).withIsSuicidal(isSuicidal).withTarget(targetDTO2)
                .build(ObjectType.DTO);

        EventNode eventNode2 = (EventNode) eventBuilder.withId(null).withSummary(summary + eventId)
                .withMotive(motive + eventId).withIsPartOfMultipleIncidents(isPartOfMultipleIncidents)
                .withIsSuccessful(isSuccessful).withIsSuicidal(isSuicidal).withTarget(targetNode2)
                .build(ObjectType.NODE);

        GroupDTO groupDTO = (GroupDTO) groupBuilder.withName(group).withEventsCaused(List.of(eventDTO1, eventDTO2)).build(ObjectType.DTO);

        GroupNode groupNodeExpected = (GroupNode) groupBuilder.withId(null)
                .withEventsCaused(List.of(eventNode1, eventNode2)).build(ObjectType.NODE);

        when(modelMapper.map(groupDTO, GroupNode.class)).thenReturn(groupNodeExpected);

        GroupNode groupNodeActual = objectMapper.map(groupDTO, GroupNode.class);

        assertAll(
                () -> assertNull(groupNodeActual.getId(),
                        () -> "should return group node with id: as null, but was: " + groupNodeActual.getId()),
                () -> assertEquals(groupNodeExpected.getName(), groupNodeActual.getName(),
                        () -> "should return group node with name: " + groupNodeExpected.getName() + ", but was: "
                                + groupNodeActual.getName()),

                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getSummary(), groupNodeActual.getEventsCaused().get(0).getSummary(),
                        () -> "should return group's event node with summary: " + groupNodeExpected.getEventsCaused().get(0).getSummary() + ", but was: "
                                + groupNodeActual.getEventsCaused().get(0).getSummary()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getMotive(), groupNodeActual.getEventsCaused().get(0).getMotive(),
                        () -> "should return group's event node with motive: " + groupNodeExpected.getEventsCaused().get(0).getMotive() + ", but was: "
                                + groupNodeActual.getEventsCaused().get(0).getMotive()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getDate(), groupNodeActual.getEventsCaused().get(0).getDate(),
                        () -> "should return group's event node with date: " + groupNodeExpected.getEventsCaused().get(0).getDate() + ", but was: "
                                + groupNodeActual.getEventsCaused().get(0).getDate()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsPartOfMultipleIncidents(),
                        groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents(),
                        () -> "should return group's event node which was part of multiple incidents: "
                                + groupNodeExpected.getEventsCaused().get(0).getIsPartOfMultipleIncidents() + ", but was: "
                                + groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsSuccessful(), groupNodeActual.getEventsCaused().get(0).getIsSuccessful(),
                        () -> "should return group's event node which was successful: " + groupNodeExpected.getEventsCaused().get(0).getIsSuccessful()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuccessful()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsSuicidal(), groupNodeActual.getEventsCaused().get(0).getIsSuicidal(),
                        () -> "should return group's event node which was suicidal: " + groupNodeExpected.getEventsCaused().get(0).getIsSuicidal()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuicidal()),
                () -> assertNotNull(groupNodeActual.getEventsCaused().get(0).getTarget(),
                        () -> "should return group's event node with not null target, but was: null"),
                () -> assertNull(groupNodeActual.getEventsCaused().get(0).getTarget().getId(),
                        () -> "should return group's event node target with id as null, but was: "
                                + groupNodeActual.getEventsCaused().get(0).getTarget().getId()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getTarget().getTarget(), groupNodeActual.getEventsCaused().get(0).getTarget().getTarget(),
                        () -> "should return group's event node with target: " + groupNodeExpected.getEventsCaused().get(0).getTarget().getTarget()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(0).getTarget().getTarget()),

                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getSummary(), groupNodeActual.getEventsCaused().get(1).getSummary(),
                        () -> "should return group's event node with summary: " + groupNodeExpected.getEventsCaused().get(1).getSummary() + ", but was: "
                                + groupNodeActual.getEventsCaused().get(1).getSummary()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getMotive(), groupNodeActual.getEventsCaused().get(1).getMotive(),
                        () -> "should return group's event node with motive: " + groupNodeExpected.getEventsCaused().get(1).getMotive() + ", but was: "
                                + groupNodeActual.getEventsCaused().get(1).getMotive()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getDate(), groupNodeActual.getEventsCaused().get(1).getDate(),
                        () -> "should return group's event node with date: " + groupNodeExpected.getEventsCaused().get(1).getDate() + ", but was: "
                                + groupNodeActual.getEventsCaused().get(1).getDate()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getIsPartOfMultipleIncidents(),
                        groupNodeActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents(),
                        () -> "should return group's event node which was part of multiple incidents: "
                                + groupNodeExpected.getEventsCaused().get(1).getIsPartOfMultipleIncidents() + ", but was: "
                                + groupNodeActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getIsSuccessful(), groupNodeActual.getEventsCaused().get(1).getIsSuccessful(),
                        () -> "should return group's event node which was successful: " + groupNodeExpected.getEventsCaused().get(1).getIsSuccessful()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(1).getIsSuccessful()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getIsSuicidal(), groupNodeActual.getEventsCaused().get(1).getIsSuicidal(),
                        () -> "should return group's event node which was suicidal: " + groupNodeExpected.getEventsCaused().get(1).getIsSuicidal()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(1).getIsSuicidal()),
                () -> assertNotNull(groupNodeActual.getEventsCaused().get(1).getTarget(),
                        () -> "should return group's event node with not null target, but was: null"),
                () -> assertNull(groupNodeActual.getEventsCaused().get(1).getTarget().getId(),
                        () -> "should return group's event node target with idas null, but was: "
                                + groupNodeActual.getEventsCaused().get(1).getTarget().getId()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getTarget().getTarget(), groupNodeActual.getEventsCaused().get(1).getTarget().getTarget(),
                        () -> "should return group's event node with target: " + groupNodeExpected.getEventsCaused().get(1).getTarget().getTarget()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(1).getTarget().getTarget()),
                () -> verify(modelMapper, times(1)).map(groupDTO, GroupNode.class),
                () -> verifyNoMoreInteractions(modelMapper));
    }

    @Test
    void when_map_group_node_to_dto_should_return_node() {

        long targetId = 1L;
        long eventId = 1L;

        String group = "group";

        String summary = "summary";
        String motive = "motive";
        boolean isPartOfMultipleIncidents = true;
        boolean isSuccessful = true;
        boolean isSuicidal = true;

        TargetNode targetNode1 = (TargetNode) targetBuilder.withTarget("target" + targetId).build(ObjectType.NODE);
        TargetDTO targetDTO1 = (TargetDTO) targetBuilder.withId(null).withTarget("target" + targetId)
                .build(ObjectType.DTO);

        EventNode eventNode1 = (EventNode) eventBuilder.withSummary(summary + eventId)
                .withMotive(motive + eventId).withIsPartOfMultipleIncidents(isPartOfMultipleIncidents)
                .withIsSuccessful(isSuccessful).withIsSuicidal(isSuicidal).withTarget(targetNode1)
                .build(ObjectType.NODE);

        EventDTO eventDTO1 = (EventDTO) eventBuilder.withId(null).withSummary(summary + eventId)
                .withMotive(motive + eventId).withIsPartOfMultipleIncidents(isPartOfMultipleIncidents)
                .withIsSuccessful(isSuccessful).withIsSuicidal(isSuicidal).withTarget(targetDTO1)
                .build(ObjectType.DTO);

        targetId++;
        eventId++;
        isPartOfMultipleIncidents = false;
        isSuccessful = false;
        isSuicidal = false;

        TargetNode targetNode2 = (TargetNode) targetBuilder.withTarget("target" + targetId).build(ObjectType.NODE);
        TargetDTO targetDTO2 = (TargetDTO) targetBuilder.withId(null).withTarget("target" + targetId)
                .build(ObjectType.DTO);

        EventNode eventNode2 = (EventNode) eventBuilder.withSummary(summary + eventId)
                .withMotive(motive + eventId).withIsPartOfMultipleIncidents(isPartOfMultipleIncidents)
                .withIsSuccessful(isSuccessful).withIsSuicidal(isSuicidal).withTarget(targetNode2)
                .build(ObjectType.NODE);

        EventDTO eventDTO2 = (EventDTO) eventBuilder.withId(null).withSummary(summary + eventId)
                .withMotive(motive + eventId).withIsPartOfMultipleIncidents(isPartOfMultipleIncidents)
                .withIsSuccessful(isSuccessful).withIsSuicidal(isSuicidal).withTarget(targetDTO2)
                .build(ObjectType.DTO);

        GroupNode groupNode = (GroupNode) groupBuilder.withName(group).withEventsCaused(List.of(eventNode1, eventNode2)).build(ObjectType.NODE);

        GroupDTO groupDTOExpected = (GroupDTO) groupBuilder.withId(null)
                .withEventsCaused(List.of(eventDTO1, eventDTO2)).build(ObjectType.DTO);

        when(modelMapper.map(groupNode, GroupDTO.class)).thenReturn(groupDTOExpected);

        GroupDTO groupDTOActual = objectMapper.map(groupNode, GroupDTO.class);

        assertAll(
                () -> assertEquals(groupDTOExpected.getName(), groupDTOActual.getName(),
                        () -> "should return group dto with name: " + groupDTOExpected.getName() + ", but was: "
                                + groupDTOActual.getName()),

                () -> assertEquals(groupDTOExpected.getEventsCaused().get(0).getSummary(), groupDTOActual.getEventsCaused().get(0).getSummary(),
                        () -> "should return group's event dto with summary: " + groupDTOExpected.getEventsCaused().get(0).getSummary() + ", but was: "
                                + groupDTOActual.getEventsCaused().get(0).getSummary()),
                () -> assertEquals(groupDTOExpected.getEventsCaused().get(0).getMotive(), groupDTOActual.getEventsCaused().get(0).getMotive(),
                        () -> "should return group's event dto with motive: " + groupDTOExpected.getEventsCaused().get(0).getMotive() + ", but was: "
                                + groupDTOActual.getEventsCaused().get(0).getMotive()),
                () -> assertEquals(groupDTOExpected.getEventsCaused().get(0).getDate(), groupDTOActual.getEventsCaused().get(0).getDate(),
                        () -> "should return group's event dto with date: " + groupDTOExpected.getEventsCaused().get(0).getDate() + ", but was: "
                                + groupDTOActual.getEventsCaused().get(0).getDate()),
                () -> assertEquals(groupDTOExpected.getEventsCaused().get(0).getIsPartOfMultipleIncidents(),
                        groupDTOActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents(),
                        () -> "should return group's event dto which was part of multiple incidents: "
                                + groupDTOExpected.getEventsCaused().get(0).getIsPartOfMultipleIncidents() + ", but was: "
                                + groupDTOActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents()),
                () -> assertEquals(groupDTOExpected.getEventsCaused().get(0).getIsSuccessful(), groupDTOActual.getEventsCaused().get(0).getIsSuccessful(),
                        () -> "should return group's event dto which was successful: " + groupDTOExpected.getEventsCaused().get(0).getIsSuccessful()
                                + ", but was: " + groupDTOActual.getEventsCaused().get(0).getIsSuccessful()),
                () -> assertEquals(groupDTOExpected.getEventsCaused().get(0).getIsSuicidal(), groupDTOActual.getEventsCaused().get(0).getIsSuicidal(),
                        () -> "should return group's event dto which was suicidal: " + groupDTOExpected.getEventsCaused().get(0).getIsSuicidal()
                                + ", but was: " + groupDTOActual.getEventsCaused().get(0).getIsSuicidal()),
                () -> assertNotNull(groupDTOActual.getEventsCaused().get(0).getTarget(),
                        () -> "should return group's event dto with not null target, but was: null"),
                () -> assertEquals(groupDTOExpected.getEventsCaused().get(0).getTarget().getTarget(), groupDTOActual.getEventsCaused().get(0).getTarget().getTarget(),
                        () -> "should return group's event dto with target: " + groupDTOExpected.getEventsCaused().get(0).getTarget().getTarget()
                                + ", but was: " + groupDTOActual.getEventsCaused().get(0).getTarget().getTarget()),

                () -> assertEquals(groupDTOExpected.getEventsCaused().get(1).getSummary(), groupDTOActual.getEventsCaused().get(1).getSummary(),
                        () -> "should return group's event dto with summary: " + groupDTOExpected.getEventsCaused().get(1).getSummary() + ", but was: "
                                + groupDTOActual.getEventsCaused().get(1).getSummary()),
                () -> assertEquals(groupDTOExpected.getEventsCaused().get(1).getMotive(), groupDTOActual.getEventsCaused().get(1).getMotive(),
                        () -> "should return group's event dto with motive: " + groupDTOExpected.getEventsCaused().get(1).getMotive() + ", but was: "
                                + groupDTOActual.getEventsCaused().get(1).getMotive()),
                () -> assertEquals(groupDTOExpected.getEventsCaused().get(1).getDate(), groupDTOActual.getEventsCaused().get(1).getDate(),
                        () -> "should return group's event dto with date: " + groupDTOExpected.getEventsCaused().get(1).getDate() + ", but was: "
                                + groupDTOActual.getEventsCaused().get(1).getDate()),
                () -> assertEquals(groupDTOExpected.getEventsCaused().get(1).getIsPartOfMultipleIncidents(),
                        groupDTOActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents(),
                        () -> "should return group's event dto which was part of multiple incidents: "
                                + groupDTOExpected.getEventsCaused().get(1).getIsPartOfMultipleIncidents() + ", but was: "
                                + groupDTOActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents()),
                () -> assertEquals(groupDTOExpected.getEventsCaused().get(1).getIsSuccessful(), groupDTOActual.getEventsCaused().get(1).getIsSuccessful(),
                        () -> "should return group's event dto which was successful: " + groupDTOExpected.getEventsCaused().get(1).getIsSuccessful()
                                + ", but was: " + groupDTOActual.getEventsCaused().get(1).getIsSuccessful()),
                () -> assertEquals(groupDTOExpected.getEventsCaused().get(1).getIsSuicidal(), groupDTOActual.getEventsCaused().get(1).getIsSuicidal(),
                        () -> "should return group's event dto which was suicidal: " + groupDTOExpected.getEventsCaused().get(1).getIsSuicidal()
                                + ", but was: " + groupDTOActual.getEventsCaused().get(1).getIsSuicidal()),
                () -> assertNotNull(groupDTOActual.getEventsCaused().get(1).getTarget(),
                        () -> "should return group's event dto with not null target, but was: null"),
                () -> assertEquals(groupDTOExpected.getEventsCaused().get(1).getTarget().getTarget(), groupDTOActual.getEventsCaused().get(1).getTarget().getTarget(),
                        () -> "should return group's event dto with target: " + groupDTOExpected.getEventsCaused().get(1).getTarget().getTarget()
                                + ", but was: " + groupDTOActual.getEventsCaused().get(1).getTarget().getTarget()),
                () -> verify(modelMapper, times(1)).map(groupNode, GroupDTO.class),
                () -> verifyNoMoreInteractions(modelMapper));
    }
}