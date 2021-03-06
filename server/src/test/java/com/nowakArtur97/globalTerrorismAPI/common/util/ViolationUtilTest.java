package com.nowakArtur97.globalTerrorismAPI.common.util;

import com.nowakArtur97.globalTerrorismAPI.feature.event.EventDTO;
import com.nowakArtur97.globalTerrorismAPI.feature.event.EventNode;
import com.nowakArtur97.globalTerrorismAPI.feature.group.GroupDTO;
import com.nowakArtur97.globalTerrorismAPI.feature.group.GroupNode;
import com.nowakArtur97.globalTerrorismAPI.feature.target.TargetDTO;
import com.nowakArtur97.globalTerrorismAPI.feature.target.TargetNode;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.EventBuilder;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.GroupBuilder;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.TargetBuilder;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.nowakArtur97.globalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("ViolationUtil_Tests")
class ViolationUtilTest {

    private ViolationUtil violationUtil;

    @Mock
    private Validator validator;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    @SuppressWarnings("rawtypes")
    private ConstraintViolation constraintViolation;

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

        violationUtil = new ViolationUtil(validator, modelMapper);
    }

    @Test
    void when_violate_valid_target_should_not_have_violations() {

        TargetDTO targetDTO = (TargetDTO) targetBuilder.build(ObjectType.DTO);
        TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);

        Set<ConstraintViolation<TargetDTO>> violationsExpected = new HashSet<>();

        when(modelMapper.map(targetNode, TargetDTO.class)).thenReturn(targetDTO);
        when(validator.validate(targetDTO)).thenReturn(violationsExpected);

        assertAll(
                () -> assertDoesNotThrow(() -> violationUtil.violate(targetNode, TargetDTO.class),
                        () -> "should not throw Constraint Violation Exception, but was thrown"),
                () -> verify(modelMapper, times(1)).map(targetNode, TargetDTO.class),
                () -> verifyNoMoreInteractions(modelMapper),
                () -> verify(validator, times(1)).validate(targetDTO),
                () -> verifyNoMoreInteractions(validator));
    }

    @Test
    @SuppressWarnings("unchecked")
    void when_violate_invalid_target_should_have_violations() {

        TargetDTO targetDTO = (TargetDTO) targetBuilder.build(ObjectType.DTO);
        TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);

        Set<ConstraintViolation<TargetDTO>> violationsExpected = new HashSet<>();

        violationsExpected.add(constraintViolation);

        Class<ConstraintViolationException> expectedException = ConstraintViolationException.class;

        when(modelMapper.map(targetNode, TargetDTO.class)).thenReturn(targetDTO);
        when(validator.validate(targetDTO)).thenReturn(violationsExpected);

        assertAll(
                () -> assertThrows(expectedException, () -> violationUtil.violate(targetNode, TargetDTO.class),
                        () -> "should throw Constraint Violation Exception, but nothing was thrown"),
                () -> verify(modelMapper, times(1)).map(targetNode, TargetDTO.class),
                () -> verifyNoMoreInteractions(modelMapper),
                () -> verify(validator, times(1)).validate(targetDTO),
                () -> verifyNoMoreInteractions(validator));
    }

    @Test
    void when_violate_valid_event_should_not_have_violations() {

        TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
        EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).build(ObjectType.DTO);

        Set<ConstraintViolation<EventDTO>> violationsExpected = new HashSet<>();

        when(modelMapper.map(eventNode, EventDTO.class)).thenReturn(eventDTO);
        when(validator.validate(eventDTO)).thenReturn(violationsExpected);

        assertAll(
                () -> assertDoesNotThrow(() -> violationUtil.violate(eventNode, EventDTO.class),
                        () -> "should not throw Constraint Violation Exception, but was thrown"),
                () -> verify(modelMapper, times(1)).map(eventNode, EventDTO.class),
                () -> verifyNoMoreInteractions(modelMapper),
                () -> verify(validator, times(1)).validate(eventDTO),
                () -> verifyNoMoreInteractions(validator));
    }

    @Test
    @SuppressWarnings("unchecked")
    void when_violate_invalid_event_should_have_violations() {

        TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
        EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).build(ObjectType.DTO);

        Set<ConstraintViolation<EventDTO>> violationsExpected = new HashSet<>();

        violationsExpected.add(constraintViolation);

        Class<ConstraintViolationException> expectedException = ConstraintViolationException.class;

        when(modelMapper.map(eventNode, EventDTO.class)).thenReturn(eventDTO);
        when(validator.validate(eventDTO)).thenReturn(violationsExpected);

        assertAll(
                () -> assertThrows(expectedException, () -> violationUtil.violate(eventNode, EventDTO.class),
                        () -> "should throw Constraint Violation Exception, but nothing was thrown"),
                () -> verify(modelMapper, times(1)).map(eventNode, EventDTO.class),
                () -> verifyNoMoreInteractions(modelMapper),
                () -> verify(validator, times(1)).validate(eventDTO),
                () -> verifyNoMoreInteractions(validator));
    }

    @Test
    void when_violate_valid_group_should_not_have_violations() {

        TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
        EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);
        GroupNode groupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);

        TargetDTO targetDTO = (TargetDTO) targetBuilder.build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        Set<ConstraintViolation<GroupDTO>> violationsExpected = new HashSet<>();

        when(modelMapper.map(groupNode, GroupDTO.class)).thenReturn(groupDTO);
        when(validator.validate(groupDTO)).thenReturn(violationsExpected);

        assertAll(
                () -> assertDoesNotThrow(() -> violationUtil.violate(groupNode, GroupDTO.class),
                        () -> "should not throw Constraint Violation Exception, but was thrown"),
                () -> verify(modelMapper, times(1)).map(groupNode, GroupDTO.class),
                () -> verifyNoMoreInteractions(modelMapper),
                () -> verify(validator, times(1)).validate(groupDTO),
                () -> verifyNoMoreInteractions(validator));
    }

    @Test
    @SuppressWarnings("unchecked")
    void when_violate_invalid_group_should_have_violations() {

        TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
        EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);
        GroupNode groupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);

        TargetDTO targetDTO = (TargetDTO) targetBuilder.build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        Set<ConstraintViolation<GroupDTO>> violationsExpected = new HashSet<>();

        violationsExpected.add(constraintViolation);

        Class<ConstraintViolationException> expectedException = ConstraintViolationException.class;

        when(modelMapper.map(groupNode, GroupDTO.class)).thenReturn(groupDTO);
        when(validator.validate(groupDTO)).thenReturn(violationsExpected);

        assertAll(
                () -> assertThrows(expectedException, () -> violationUtil.violate(groupNode, GroupDTO.class),
                        () -> "should throw Constraint Violation Exception, but nothing was thrown"),
                () -> verify(modelMapper, times(1)).map(groupNode, GroupDTO.class),
                () -> verifyNoMoreInteractions(modelMapper),
                () -> verify(validator, times(1)).validate(groupDTO),
                () -> verifyNoMoreInteractions(validator));
    }
}
