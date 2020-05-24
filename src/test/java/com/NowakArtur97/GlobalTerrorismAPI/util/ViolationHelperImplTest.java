package com.NowakArtur97.GlobalTerrorismAPI.util;

import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.EventBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.TargetBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("ViolationHelperImpl_Tests")
class ViolationHelperImplTest {

	private ViolationHelper violationHelper;

	@Mock
	private Validator validator;

	@Mock
	private ObjectMapper objectMapper;

	@Mock
	@SuppressWarnings("rawtypes")
	private ConstraintViolation constraintViolation;

	private TargetBuilder targetBuilder;
	private EventBuilder eventBuilder;

	@BeforeEach
	private void setUp() {

		violationHelper = new ViolationHelperImpl(validator, objectMapper);

		targetBuilder = new TargetBuilder();
		eventBuilder = new EventBuilder();
	}

	@Test
	void when_violate_valid_target_should_not_have_violations() {

		Long targetId = 1L;
		String invalidTargetName = "some invalid target name";
		TargetDTO targetDTO = new TargetDTO(invalidTargetName);
		TargetNode targetNode = new TargetNode(targetId, invalidTargetName);

		Set<ConstraintViolation<TargetDTO>> violationsExpected = new HashSet<>();

		when(objectMapper.map(targetNode, TargetDTO.class)).thenReturn(targetDTO);
		when(validator.validate(targetDTO)).thenReturn(violationsExpected);

		assertAll(
				() -> assertDoesNotThrow(() -> violationHelper.violate(targetNode, TargetDTO.class),
						() -> "should not throw Constraint Violation Exception, but was thrown"),
				() -> verify(objectMapper, times(1)).map(targetNode, TargetDTO.class),
				() -> verifyNoMoreInteractions(objectMapper), () -> verify(validator, times(1)).validate(targetDTO),
				() -> verifyNoMoreInteractions(validator));
	}

	@Test
	@SuppressWarnings("unchecked")
	void when_violate_invalid_target_should_have_violations() {

		Long targetId = 1L;
		String invalidTargetName = "some invalid target name";
		TargetDTO targetDTO = new TargetDTO(invalidTargetName);
		TargetNode targetNode = new TargetNode(targetId, invalidTargetName);

		Set<ConstraintViolation<TargetDTO>> violationsExpected = new HashSet<>();

		violationsExpected.add(constraintViolation);

		Class<ConstraintViolationException> expectedException = ConstraintViolationException.class;

		when(objectMapper.map(targetNode, TargetDTO.class)).thenReturn(targetDTO);
		when(validator.validate(targetDTO)).thenReturn(violationsExpected);

		assertAll(
				() -> assertThrows(expectedException, () -> violationHelper.violate(targetNode, TargetDTO.class),
						() -> "should throw Constraint Violation Exception, but nothing was thrown"),
				() -> verify(objectMapper, times(1)).map(targetNode, TargetDTO.class),
				() -> verifyNoMoreInteractions(objectMapper), () -> verify(validator, times(1)).validate(targetDTO),
				() -> verifyNoMoreInteractions(validator));
	}

	@Test
	void when_violate_valid_event_should_not_have_violations() {

		TargetDTO targetDTO = (TargetDTO) targetBuilder.build(ObjectType.DTO);
		EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).build(ObjectType.DTO);
		TargetNode targetNode = (TargetNode) targetBuilder.withId(null).build(ObjectType.NODE);
		EventNode eventNode = (EventNode) eventBuilder.withId(null).withTarget(targetNode).build(ObjectType.NODE);

		Set<ConstraintViolation<EventDTO>> violationsExpected = new HashSet<>();

		when(objectMapper.map(eventNode, EventDTO.class)).thenReturn(eventDTO);
		when(validator.validate(eventDTO)).thenReturn(violationsExpected);

		assertAll(
				() -> assertDoesNotThrow(() -> violationHelper.violate(eventNode, EventDTO.class),
						() -> "should not throw Constraint Violation Exception, but was thrown"),
				() -> verify(objectMapper, times(1)).map(eventNode, EventDTO.class),
				() -> verifyNoMoreInteractions(objectMapper), () -> verify(validator, times(1)).validate(eventDTO),
				() -> verifyNoMoreInteractions(validator));
	}

	@Test
	@SuppressWarnings("unchecked")
	void when_violate_invalid_event_should_have_violations() {

		TargetDTO targetDTO = (TargetDTO) targetBuilder.build(ObjectType.DTO);
		EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).build(ObjectType.DTO);
		TargetNode targetNode = (TargetNode) targetBuilder.withId(null).build(ObjectType.NODE);
		EventNode eventNode = (EventNode) eventBuilder.withId(null).withTarget(targetNode).build(ObjectType.NODE);

		Set<ConstraintViolation<EventDTO>> violationsExpected = new HashSet<>();

		violationsExpected.add(constraintViolation);

		Class<ConstraintViolationException> expectedException = ConstraintViolationException.class;

		when(objectMapper.map(eventNode, EventDTO.class)).thenReturn(eventDTO);
		when(validator.validate(eventDTO)).thenReturn(violationsExpected);

		assertAll(
				() -> assertThrows(expectedException, () -> violationHelper.violate(eventNode, EventDTO.class),
						() -> "should throw Constraint Violation Exception, but nothing was thrown"),
				() -> verify(objectMapper, times(1)).map(eventNode, EventDTO.class),
				() -> verifyNoMoreInteractions(objectMapper), () -> verify(validator, times(1)).validate(eventDTO),
				() -> verifyNoMoreInteractions(validator));
	}
}
