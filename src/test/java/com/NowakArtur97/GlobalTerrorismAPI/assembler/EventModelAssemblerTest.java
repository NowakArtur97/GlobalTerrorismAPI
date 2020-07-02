package com.NowakArtur97.GlobalTerrorismAPI.assembler;

import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.TargetModel;
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
import org.springframework.hateoas.Link;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("EventModelAssembler_Tests")
class EventModelAssemblerTest {

	private final String BASE_PATH = "http://localhost:8080/api/events";

	private EventModelAssembler modelAssembler;

	@Mock
	private TargetModelAssembler targetModelAssembler;

	@Mock
	private ObjectMapper objectMapper;

	private TargetBuilder targetBuilder;
	private EventBuilder eventBuilder;

	@BeforeEach
	private void setUp() {

		modelAssembler = new EventModelAssembler(targetModelAssembler, objectMapper);

		targetBuilder = new TargetBuilder();
		eventBuilder = new EventBuilder();
	}

	@Test
	void when_map_event_node_to_model_should_return_event_model() {

		Long targetId = 1L;
		TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
		EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);
		TargetModel targetModel = (TargetModel) targetBuilder.build(ObjectType.MODEL);
		EventModel eventModel = (EventModel) eventBuilder.withTarget(targetModel).build(ObjectType.MODEL);
		String pathToLink = BASE_PATH + targetId.intValue();
		Link link = new Link(pathToLink);
		targetModel.add(link);

		when(objectMapper.map(eventNode, EventModel.class)).thenReturn(eventModel);
		when(targetModelAssembler.toModel(eventNode.getTarget())).thenReturn(targetModel);

		EventModel model = modelAssembler.toModel(eventNode);

		assertAll(
				() -> assertNotNull(model.getId(),
						() -> "should return event model with new id, but was: " + model.getId()),
				() -> assertEquals(eventNode.getSummary(), model.getSummary(),
						() -> "should return event model with summary: " + eventNode.getSummary() + ", but was: "
								+ model.getSummary()),
				() -> assertEquals(eventNode.getMotive(), model.getMotive(),
						() -> "should return event model with motive: " + eventNode.getMotive() + ", but was: "
								+ model.getMotive()),
				() -> assertNotNull(model.getDate(),
						() -> "should return event model with date: " + eventNode.getDate() + ", but was null"),
				() -> assertEquals(eventNode.getIsPartOfMultipleIncidents(), model.getIsPartOfMultipleIncidents(),
						() -> "should return event model which was part of multiple incidents: "
								+ eventNode.getIsPartOfMultipleIncidents() + ", but was: "
								+ model.getIsPartOfMultipleIncidents()),
				() -> assertEquals(eventNode.getIsSuccessful(), model.getIsSuccessful(),
						() -> "should return event model which was successful: " + eventNode.getIsSuccessful()
								+ ", but was: " + model.getIsSuccessful()),
				() -> assertEquals(eventNode.getIsSuicidal(), model.getIsSuicidal(),
						() -> "should return event model which was suicidal: " + eventNode.getIsSuicidal() + ", but was: "
								+ model.getIsSuicidal()),
				() -> assertNotNull(eventNode.getTarget(),
						() -> "should return event model with not null target, but was: null"),
				() -> assertEquals(targetModel, model.getTarget(),
						() -> "should return event model with target model: " + targetModel + ", but was: "
								+ model.getTarget()),
				() -> assertNotNull(model.getLinks(), () -> "should return model with links, but was: " + model),
				() -> assertFalse(model.getLinks().isEmpty(),
						() -> "should return model with links, but was: " + model),
				() -> verify(objectMapper, times(1)).map(eventNode, EventModel.class),
				() -> verifyNoMoreInteractions(objectMapper),
				() -> verify(targetModelAssembler, times(1)).toModel(eventNode.getTarget()),
				() -> verifyNoMoreInteractions(targetModelAssembler));
	}

	@Test
	void when_map_event_node_to_model_without_target_should_return_event_model_without_target() {

		EventNode eventNode = (EventNode) eventBuilder.build(ObjectType.NODE);

		EventModel eventModel = (EventModel) eventBuilder.build(ObjectType.MODEL);

		when(objectMapper.map(eventNode, EventModel.class)).thenReturn(eventModel);

		EventModel model = modelAssembler.toModel(eventNode);

		assertAll(
				() -> assertNotNull(model.getId(),
						() -> "should return event model with new id, but was: " + model.getId()),
				() -> assertEquals(eventNode.getSummary(), model.getSummary(),
						() -> "should return event model with summary: " + eventNode.getSummary() + ", but was: "
								+ model.getSummary()),
				() -> assertEquals(eventNode.getMotive(), model.getMotive(),
						() -> "should return event model with motive: " + eventNode.getMotive() + ", but was: "
								+ model.getMotive()),
				() -> assertNotNull(model.getDate(),
						() -> "should return event model with date: " + eventNode.getDate() + ", but was null"),
				() -> assertEquals(eventNode.getIsPartOfMultipleIncidents(), model.getIsPartOfMultipleIncidents(),
						() -> "should return event model which was part of multiple incidents: "
								+ eventNode.getIsPartOfMultipleIncidents() + ", but was: "
								+ model.getIsPartOfMultipleIncidents()),
				() -> assertEquals(eventNode.getIsSuccessful(), model.getIsSuccessful(),
						() -> "should return event model which was successful: " + eventNode.getIsSuccessful()
								+ ", but was: " + model.getIsSuccessful()),
				() -> assertEquals(eventNode.getIsSuicidal(), model.getIsSuicidal(),
						() -> "should return event model which was suicidal: " + eventNode.getIsSuicidal() + ", but was: "
								+ model.getIsSuicidal()),
				() -> assertNull(eventNode.getTarget(),
						() -> "should return event model with null target, but wasn't: null"),
				() -> assertNotNull(model.getLinks(), () -> "should return model with links, but was: " + model),
				() -> assertFalse(model.getLinks().isEmpty(),
						() -> "should return model with links, but was: " + model),
				() -> verify(objectMapper, times(1)).map(eventNode, EventModel.class),
				() -> verifyNoMoreInteractions(objectMapper),
				() -> verifyNoInteractions(targetModelAssembler));
	}
}
