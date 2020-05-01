package com.NowakArtur97.GlobalTerrorismAPI.controller.event;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import javax.json.JsonPatch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.NowakArtur97.GlobalTerrorismAPI.advice.RestResponseGlobalEntityExceptionHandler;
import com.NowakArtur97.GlobalTerrorismAPI.assembler.EventModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.controller.EventController;
import com.NowakArtur97.GlobalTerrorismAPI.httpMessageConverter.JsonMergePatchHttpMessageConverter;
import com.NowakArtur97.GlobalTerrorismAPI.httpMessageConverter.JsonPatchHttpMessageConverter;
import com.NowakArtur97.GlobalTerrorismAPI.model.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.EventService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.mediaType.PatchMediaType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.NowakArtur97.GlobalTerrorismAPI.util.PatchHelper;
import com.NowakArtur97.GlobalTerrorismAPI.util.ViolationHelper;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("EventController_Tests")
@DisabledOnOs(OS.LINUX)
class EventControllerPatchMethodTest {

	private final String EVENT_BASE_PATH = "http://localhost:8080/api/events";
	private final String TARGET_BASE_PATH = "http://localhost:8080/api/targets";

	private MockMvc mockMvc;

	private EventController eventController;

	private RestResponseGlobalEntityExceptionHandler restResponseGlobalEntityExceptionHandler;

	@Mock
	private EventService eventService;

	@Mock
	private EventModelAssembler eventModelAssembler;

	@Mock
	private PagedResourcesAssembler<EventNode> pagedResourcesAssembler;

	@Mock
	private PatchHelper patchHelper;

	@Autowired
	private ViolationHelper violationHelper;

	@BeforeEach
	private void setUp() {

		eventController = new EventController(eventService, eventModelAssembler, pagedResourcesAssembler, patchHelper,
				violationHelper);

		restResponseGlobalEntityExceptionHandler = new RestResponseGlobalEntityExceptionHandler();

		mockMvc = MockMvcBuilders.standaloneSetup(eventController, restResponseGlobalEntityExceptionHandler)
				.setMessageConverters(new JsonMergePatchHttpMessageConverter(), new JsonPatchHttpMessageConverter(),
						new MappingJackson2HttpMessageConverter())
				.build();
	}

	@Test
	void when_partial_update_valid_event_using_json_patch_should_return_partially_updated_node() throws ParseException {

		Long eventId = 1L;

		String eventSummary = "summary";
		String eventMotive = "motive";
		String eventDateString = "2000-08-05";
		Date eventDate = new SimpleDateFormat("yyyy-MM-dd").parse(eventDateString);
		boolean isEventPartOfMultipleIncidents = true;
		boolean isEventSuccessful = true;
		boolean isEventSuicide = true;

		String updatedEventSummary = "summary updated";
		String updatedEventMotive = "motive updated";
		String updatedEventDateString = "2001-08-05";
		Date updatedEventDate = new SimpleDateFormat("yyyy-MM-dd").parse(updatedEventDateString);
		boolean updatedIsEventPartOfMultipleIncidents = false;
		boolean updatedIsEventSuccessful = false;
		boolean updatedIsEventSuicide = false;

		Long targetId = 1L;
		String target = "target";
		TargetNode targetNode = new TargetNode(targetId, target);
		TargetModel targetModel = new TargetModel(targetId, target);

		String pathToTargetLink = TARGET_BASE_PATH + "/" + targetId.intValue();
		Link targetLink = new Link(pathToTargetLink);
		targetModel.add(targetLink);

		EventNode eventNode = EventNode.builder().id(eventId).date(eventDate).summary(eventSummary)
				.isPartOfMultipleIncidents(isEventPartOfMultipleIncidents).isSuccessful(isEventSuccessful)
				.isSuicide(isEventSuicide).motive(eventMotive).target(targetNode).build();

		EventNode updatedEventNode = EventNode.builder().id(eventId).date(updatedEventDate).summary(updatedEventSummary)
				.isPartOfMultipleIncidents(updatedIsEventPartOfMultipleIncidents).isSuccessful(updatedIsEventSuccessful)
				.isSuicide(updatedIsEventSuicide).motive(updatedEventMotive).target(targetNode).build();

		EventModel eventModel = EventModel.builder().id(eventId).date(updatedEventDate).summary(updatedEventSummary)
				.isPartOfMultipleIncidents(updatedIsEventPartOfMultipleIncidents).isSuccessful(updatedIsEventSuccessful)
				.isSuicide(updatedIsEventSuicide).motive(updatedEventMotive).target(targetModel).build();

		String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();
		Link eventLink = new Link(pathToEventLink);
		eventModel.add(eventLink);

		String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

		when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
		when(patchHelper.patch(any(JsonPatch.class), ArgumentMatchers.any(EventNode.class),
				ArgumentMatchers.<Class<EventNode>>any())).thenReturn(updatedEventNode);
		when(eventService.save(ArgumentMatchers.any(EventNode.class))).thenReturn(updatedEventNode);
		when(eventModelAssembler.toModel(ArgumentMatchers.any(EventNode.class))).thenReturn(eventModel);

		String jsonPatch = "[" + "{ \"op\": \"replace\", \"path\": \"/summary\", \"value\": \"" + updatedEventSummary
				+ "\" }," + "{ \"op\": \"replace\", \"path\": \"/motive\", \"value\": \"" + updatedEventMotive + "\" },"
				+ "{ \"op\": \"replace\", \"path\": \"/date\", \"value\": \"" + updatedEventDateString + "\" },"
				+ "{ \"op\": \"replace\", \"path\": \"/partOfMultipleIncidents\", \"value\": \""
				+ updatedIsEventPartOfMultipleIncidents + "\" },"
				+ "{ \"op\": \"replace\", \"path\": \"/successful\", \"value\": \"" + updatedIsEventSuccessful + "\" },"
				+ "{ \"op\": \"replace\", \"path\": \"/suicide\", \"value\": \"" + updatedIsEventSuicide + "\" }" + "]";

		assertAll(
				() -> mockMvc
						.perform(patch(linkWithParameter, eventId).content(jsonPatch)
								.contentType(PatchMediaType.APPLICATION_JSON_PATCH))
						.andExpect(status().isOk())
						.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("links[0].href", is(pathToEventLink)))
						.andExpect(jsonPath("id", is(eventId.intValue())))
						.andExpect(jsonPath("summary", is(updatedEventSummary)))
						.andExpect(jsonPath("motive", is(updatedEventMotive)))
						.andExpect(jsonPath("date", is(notNullValue())))
						.andExpect(jsonPath("suicide", is(updatedIsEventSuicide)))
						.andExpect(jsonPath("successful", is(updatedIsEventSuccessful)))
						.andExpect(jsonPath("partOfMultipleIncidents", is(updatedIsEventPartOfMultipleIncidents)))
						.andExpect(jsonPath("target.links[0].href", is(pathToTargetLink)))
						.andExpect(jsonPath("target.id", is(targetId.intValue())))
						.andExpect(jsonPath("target.target", is(target))),
				() -> verify(eventService, times(1)).findById(eventId),
				() -> verify(patchHelper, times(1)).patch(any(JsonPatch.class), ArgumentMatchers.any(EventNode.class),
						ArgumentMatchers.<Class<EventNode>>any()),
				() -> verifyNoMoreInteractions(patchHelper),
				() -> verify(eventService, times(1)).save(ArgumentMatchers.any(EventNode.class)),
				() -> verifyNoMoreInteractions(eventService),
				() -> verify(eventModelAssembler, times(1)).toModel(ArgumentMatchers.any(EventNode.class)),
				() -> verifyNoMoreInteractions(eventModelAssembler),
				() -> verifyNoInteractions(pagedResourcesAssembler));
	}

	@ParameterizedTest(name = "{index}: For Event Target: {0} should have violation")
	@NullAndEmptySource
	@ValueSource(strings = { " " })
	void when_partial_update_invalid_events_target_using_json_patch_should_have_errors(String invalidTarget)
			throws ParseException {

		Long eventId = 1L;

		String eventSummary = "summary";
		String eventMotive = "motive";
		String eventDateString = "2000-08-05";
		Date eventDate = new SimpleDateFormat("yyyy-MM-dd").parse(eventDateString);
		boolean isEventPartOfMultipleIncidents = true;
		boolean isEventSuccessful = true;
		boolean isEventSuicide = true;

		Long targetId = 1L;
		String target = "target";
		TargetNode targetNode = new TargetNode(targetId, target);
		TargetNode updatedTargetNode = new TargetNode(targetId, invalidTarget);

		EventNode eventNode = EventNode.builder().id(eventId).date(eventDate).summary(eventSummary)
				.isPartOfMultipleIncidents(isEventPartOfMultipleIncidents).isSuccessful(isEventSuccessful)
				.isSuicide(isEventSuicide).motive(eventMotive).target(targetNode).build();

		EventNode updatedEventNode = EventNode.builder().id(eventId).date(eventDate).summary(eventSummary)
				.isPartOfMultipleIncidents(isEventPartOfMultipleIncidents).isSuccessful(isEventSuccessful)
				.isSuicide(isEventSuicide).motive(eventMotive).target(updatedTargetNode).build();

		String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

		when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
		when(patchHelper.patch(any(JsonPatch.class), ArgumentMatchers.any(EventNode.class),
				ArgumentMatchers.<Class<EventNode>>any())).thenReturn(updatedEventNode);

		String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/target/target\", \"value\": \"" + invalidTarget
				+ "\" }]";

		assertAll(
				() -> mockMvc
						.perform(patch(linkWithParameter, eventId).content(jsonPatch)
								.contentType(PatchMediaType.APPLICATION_JSON_PATCH).accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isBadRequest()).andExpect(jsonPath("timestamp", is(notNullValue())))
						.andExpect(jsonPath("status", is(400)))
						.andExpect(jsonPath("errors[0]", is("Target name cannot be empty"))),
				() -> verify(eventService, times(1)).findById(eventId), () -> verifyNoMoreInteractions(eventService),
				() -> verify(patchHelper, times(1)).patch(any(JsonPatch.class), ArgumentMatchers.any(EventNode.class),
						ArgumentMatchers.<Class<EventNode>>any()),
				() -> verifyNoMoreInteractions(patchHelper), () -> verifyNoInteractions(eventModelAssembler),
				() -> verifyNoInteractions(pagedResourcesAssembler));
	}

	@Test
	void when_partial_update_valid_events_target_using_json_patch_should_return_partially_updated_node()
			throws ParseException {

		Long eventId = 1L;

		String eventSummary = "summary";
		String eventMotive = "motive";
		String eventDateString = "2000-08-05";
		Date eventDate = new SimpleDateFormat("yyyy-MM-dd").parse(eventDateString);
		boolean isEventPartOfMultipleIncidents = true;
		boolean isEventSuccessful = true;
		boolean isEventSuicide = true;

		Long targetId = 1L;
		String target = "target";
		String updatedTarget = "updated target";
		TargetNode targetNode = new TargetNode(targetId, target);
		TargetNode updatedTargetNode = new TargetNode(targetId, updatedTarget);
		TargetModel targetModel = new TargetModel(targetId, updatedTarget);

		String pathToTargetLink = TARGET_BASE_PATH + "/" + targetId.intValue();
		Link targetLink = new Link(pathToTargetLink);
		targetModel.add(targetLink);

		EventNode eventNode = EventNode.builder().id(eventId).date(eventDate).summary(eventSummary)
				.isPartOfMultipleIncidents(isEventPartOfMultipleIncidents).isSuccessful(isEventSuccessful)
				.isSuicide(isEventSuicide).motive(eventMotive).target(targetNode).build();

		EventNode updatedEventNode = EventNode.builder().id(eventId).date(eventDate).summary(eventSummary)
				.isPartOfMultipleIncidents(isEventPartOfMultipleIncidents).isSuccessful(isEventSuccessful)
				.isSuicide(isEventSuicide).motive(eventMotive).target(updatedTargetNode).build();

		EventModel eventModel = EventModel.builder().id(eventId).date(eventDate).summary(eventSummary)
				.isPartOfMultipleIncidents(isEventPartOfMultipleIncidents).isSuccessful(isEventSuccessful)
				.isSuicide(isEventSuicide).motive(eventMotive).target(targetModel).build();

		String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();
		Link eventLink = new Link(pathToEventLink);
		eventModel.add(eventLink);

		String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

		when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
		when(patchHelper.patch(any(JsonPatch.class), ArgumentMatchers.any(EventNode.class),
				ArgumentMatchers.<Class<EventNode>>any())).thenReturn(updatedEventNode);
		when(eventService.save(ArgumentMatchers.any(EventNode.class))).thenReturn(updatedEventNode);
		when(eventModelAssembler.toModel(ArgumentMatchers.any(EventNode.class))).thenReturn(eventModel);

		String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/target/target\", \"value\": \"" + updatedTarget
				+ "\" }]";

		assertAll(
				() -> mockMvc
						.perform(patch(linkWithParameter, eventId).content(jsonPatch)
								.contentType(PatchMediaType.APPLICATION_JSON_PATCH))
						.andExpect(status().isOk())
						.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("links[0].href", is(pathToEventLink)))
						.andExpect(jsonPath("id", is(eventId.intValue())))
						.andExpect(jsonPath("summary", is(eventSummary))).andExpect(jsonPath("motive", is(eventMotive)))
						.andExpect(jsonPath("date", is(notNullValue())))
						.andExpect(jsonPath("suicide", is(isEventSuicide)))
						.andExpect(jsonPath("successful", is(isEventSuccessful)))
						.andExpect(jsonPath("partOfMultipleIncidents", is(isEventPartOfMultipleIncidents)))
						.andExpect(jsonPath("target.links[0].href", is(pathToTargetLink)))
						.andExpect(jsonPath("target.id", is(targetId.intValue())))
						.andExpect(jsonPath("target.target", is(updatedTarget))),
				() -> verify(eventService, times(1)).findById(eventId),
				() -> verify(patchHelper, times(1)).patch(any(JsonPatch.class), ArgumentMatchers.any(EventNode.class),
						ArgumentMatchers.<Class<EventNode>>any()),
				() -> verifyNoMoreInteractions(patchHelper),
				() -> verify(eventService, times(1)).save(ArgumentMatchers.any(EventNode.class)),
				() -> verifyNoMoreInteractions(eventService),
				() -> verify(eventModelAssembler, times(1)).toModel(ArgumentMatchers.any(EventNode.class)),
				() -> verifyNoMoreInteractions(eventModelAssembler),
				() -> verifyNoInteractions(pagedResourcesAssembler));
	}
}