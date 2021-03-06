package com.nowakArtur97.globalTerrorismAPI.feature.event;

import com.nowakArtur97.globalTerrorismAPI.advice.GenericRestControllerAdvice;
import com.nowakArtur97.globalTerrorismAPI.common.controller.GenericRestController;
import com.nowakArtur97.globalTerrorismAPI.common.util.PatchUtil;
import com.nowakArtur97.globalTerrorismAPI.common.util.ViolationUtil;
import com.nowakArtur97.globalTerrorismAPI.feature.city.CityModel;
import com.nowakArtur97.globalTerrorismAPI.feature.city.CityNode;
import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryModel;
import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryNode;
import com.nowakArtur97.globalTerrorismAPI.feature.province.ProvinceModel;
import com.nowakArtur97.globalTerrorismAPI.feature.province.ProvinceNode;
import com.nowakArtur97.globalTerrorismAPI.feature.region.RegionModel;
import com.nowakArtur97.globalTerrorismAPI.feature.region.RegionNode;
import com.nowakArtur97.globalTerrorismAPI.feature.target.TargetModel;
import com.nowakArtur97.globalTerrorismAPI.feature.target.TargetNode;
import com.nowakArtur97.globalTerrorismAPI.feature.victim.VictimModel;
import com.nowakArtur97.globalTerrorismAPI.feature.victim.VictimNode;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.*;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.nowakArtur97.globalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("EventController_Tests")
class EventControllerGetMethodTest {

    private static int counterForUtilMethodsModel = 0;
    private static int counterForUtilMethodsNode = 0;

    private final String REGION_BASE_PATH = "http://localhost:8080/api/v1/regions";
    private final String COUNTRY_BASE_PATH = "http://localhost:8080/api/v1/countries";
    private final String PROVINCE_BASE_PATH = "http://localhost:8080/api/v1/provinces";
    private final String CITY_BASE_PATH = "http://localhost:8080/api/v1/cities";
    private final String VICTIM_BASE_PATH = "http://localhost:8080/api/v1/victims";
    private final String TARGET_BASE_PATH = "http://localhost:8080/api/v1/targets";
    private final String EVENT_BASE_PATH = "http://localhost:8080/api/v1/events";

    private MockMvc mockMvc;

    @Mock
    private EventService eventService;

    @Mock
    private EventModelAssembler modelAssembler;

    @Mock
    private PagedResourcesAssembler<EventNode> pagedResourcesAssembler;

    @Mock
    private PatchUtil patchUtil;

    @Mock
    private ViolationUtil<EventNode, EventDTO> violationUtil;

    private static RegionBuilder regionBuilder;
    private static CountryBuilder countryBuilder;
    private static ProvinceBuilder provinceBuilder;
    private static CityBuilder cityBuilder;
    private static TargetBuilder targetBuilder;
    private static EventBuilder eventBuilder;
    private static VictimBuilder victimBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        regionBuilder = new RegionBuilder();
        countryBuilder = new CountryBuilder();
        targetBuilder = new TargetBuilder();
        provinceBuilder = new ProvinceBuilder();
        cityBuilder = new CityBuilder();
        victimBuilder = new VictimBuilder();
        eventBuilder = new EventBuilder();
    }

    @BeforeEach
    private void setUp() {

        GenericRestController<EventModel, EventDTO> eventController
                = new EventController(eventService, modelAssembler, pagedResourcesAssembler, patchUtil, violationUtil);

        mockMvc = MockMvcBuilders.standaloneSetup(eventController)
                .setControllerAdvice(new GenericRestControllerAdvice())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void when_find_all_events_with_default_parameters_in_link_and_events_exist_should_return_all_events() {

        EventNode eventNode1 = (EventNode) createEvent(ObjectType.NODE);
        EventNode eventNode2 = (EventNode) createEvent(ObjectType.NODE);
        EventNode eventNode3 = (EventNode) createEvent(ObjectType.NODE);
        EventNode eventNode4 = (EventNode) createEvent(ObjectType.NODE);

        EventModel eventModel1 = (EventModel) createEvent(ObjectType.MODEL);
        EventModel eventModel2 = (EventModel) createEvent(ObjectType.MODEL);
        EventModel eventModel3 = (EventModel) createEvent(ObjectType.MODEL);
        EventModel eventModel4 = (EventModel) createEvent(ObjectType.MODEL);

        List<EventNode> eventNodesListExpected = List.of(eventNode1, eventNode2, eventNode3, eventNode4);
        List<EventModel> eventModelsListExpected = List.of(eventModel1, eventModel2, eventModel3, eventModel4);
        Page<EventNode> eventsExpected = new PageImpl<>(eventNodesListExpected);

        int sizeExpected = 20;
        int totalElementsExpected = 4;
        int totalPagesExpected = 1;
        int numberExpected = 0;
        int pageExpected = 0;
        int lastPageExpected = 0;

        Pageable pageable = PageRequest.of(pageExpected, sizeExpected);

        String urlParameters1 = "?page=" + pageExpected + "&size=" + sizeExpected;
        String urlParameters2 = "?page=" + lastPageExpected + "&size=" + sizeExpected;
        String firstPageLink = EVENT_BASE_PATH + urlParameters1;
        String lastPageLink = EVENT_BASE_PATH + urlParameters2;

        Link pageLink1 = new Link(firstPageLink, "first");
        Link pageLink2 = new Link(firstPageLink, "self");
        Link pageLink3 = new Link(lastPageLink, "next");
        Link pageLink4 = new Link(lastPageLink, "last");

        PageMetadata metadata = new PagedModel.PageMetadata(sizeExpected, numberExpected, totalElementsExpected);
        PagedModel<EventModel> resources = new PagedModel<>(eventModelsListExpected, metadata, pageLink1, pageLink2,
                pageLink3, pageLink4);

        when(eventService.findAll(pageable)).thenReturn(eventsExpected);
        when(pagedResourcesAssembler.toModel(eventsExpected, modelAssembler)).thenReturn(resources);

        assertAll(
                () -> mockMvc.perform(get(firstPageLink).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[1].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[2].href", is(lastPageLink)))
                        .andExpect(jsonPath("links[3].href", is(lastPageLink)))
                        .andExpect(jsonPath("content[0].id", is(eventModel1.getId().intValue())))
                        .andExpect(jsonPath("content[0].summary", is(eventModel1.getSummary())))
                        .andExpect(jsonPath("content[0].motive", is(eventModel1.getMotive())))
                        .andExpect(jsonPath("content[0].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventModel1.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("content[0].isSuicidal", is(eventModel1.getIsSuicidal())))
                        .andExpect(jsonPath("content[0].isSuccessful", is(eventModel1.getIsSuccessful())))
                        .andExpect(jsonPath("content[0].isPartOfMultipleIncidents",
                                is(eventModel1.getIsPartOfMultipleIncidents())))
                        .andExpect(
                                jsonPath("content[0].links[0].href", is(eventModel1.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[0].links[1].href",
                                is(eventModel1.getLink("target").get().getHref())))
                        .andExpect(jsonPath("content[0].target.links[0].href",
                                is(eventModel1.getTarget().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[0].city.links[0].href",
                                is(eventModel1.getCity().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[0].victim.links[0].href",
                                is(eventModel1.getVictim().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[0].target.id", is(eventModel1.getTarget().getId().intValue())))
                        .andExpect(jsonPath("content[0].target.target", is(eventModel1.getTarget().getTarget())))
                        .andExpect(jsonPath("content[0].city.id", is(eventModel1.getCity().getId().intValue())))
                        .andExpect(jsonPath("content[0].city.name", is(eventModel1.getCity().getName())))
                        .andExpect(jsonPath("content[0].city.latitude", is(eventModel1.getCity().getLatitude())))
                        .andExpect(jsonPath("content[0].city.longitude", is(eventModel1.getCity().getLongitude())))
                        .andExpect(jsonPath("content[0].victim.id", is(eventModel1.getVictim().getId().intValue())))
                        .andExpect(jsonPath("content[0].victim.totalNumberOfFatalities",
                                is(eventModel1.getVictim().getTotalNumberOfFatalities().intValue())))
                        .andExpect(jsonPath("content[0].victim.numberOfPerpetratorsFatalities",
                                is(eventModel1.getVictim().getNumberOfPerpetratorsFatalities().intValue())))
                        .andExpect(jsonPath("content[0].victim.totalNumberOfInjured",
                                is(eventModel1.getVictim().getTotalNumberOfInjured().intValue())))
                        .andExpect(jsonPath("content[0].victim.numberOfPerpetratorsInjured",
                                is(eventModel1.getVictim().getNumberOfPerpetratorsInjured().intValue())))
                        .andExpect(jsonPath("content[0].victim.valueOfPropertyDamage",
                                is(eventModel1.getVictim().getValueOfPropertyDamage().intValue())))

                        .andExpect(jsonPath("content[1].id", is(eventModel2.getId().intValue())))
                        .andExpect(jsonPath("content[1].summary", is(eventModel2.getSummary())))
                        .andExpect(jsonPath("content[1].motive", is(eventModel2.getMotive())))
                        .andExpect(jsonPath("content[1].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventModel2.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("content[1].isSuicidal", is(eventModel2.getIsSuicidal())))
                        .andExpect(jsonPath("content[1].isSuccessful", is(eventModel2.getIsSuccessful())))
                        .andExpect(jsonPath("content[1].isPartOfMultipleIncidents",
                                is(eventModel2.getIsPartOfMultipleIncidents())))
                        .andExpect(
                                jsonPath("content[1].links[0].href", is(eventModel2.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[1].links[1].href",
                                is(eventModel2.getLink("target").get().getHref())))
                        .andExpect(jsonPath("content[1].target.links[0].href",
                                is(eventModel2.getTarget().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[1].city.links[0].href",
                                is(eventModel2.getCity().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[1].victim.links[0].href",
                                is(eventModel2.getVictim().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[1].target.id", is(eventModel2.getTarget().getId().intValue())))
                        .andExpect(jsonPath("content[1].target.target", is(eventModel2.getTarget().getTarget())))
                        .andExpect(jsonPath("content[1].city.id", is(eventModel2.getCity().getId().intValue())))
                        .andExpect(jsonPath("content[1].city.name", is(eventModel2.getCity().getName())))
                        .andExpect(jsonPath("content[1].city.latitude", is(eventModel2.getCity().getLatitude())))
                        .andExpect(jsonPath("content[1].city.longitude", is(eventModel2.getCity().getLongitude())))
                        .andExpect(jsonPath("content[1].victim.id", is(eventModel2.getVictim().getId().intValue())))
                        .andExpect(jsonPath("content[1].victim.totalNumberOfFatalities",
                                is(eventModel2.getVictim().getTotalNumberOfFatalities().intValue())))
                        .andExpect(jsonPath("content[1].victim.numberOfPerpetratorsFatalities",
                                is(eventModel2.getVictim().getNumberOfPerpetratorsFatalities().intValue())))
                        .andExpect(jsonPath("content[1].victim.totalNumberOfInjured",
                                is(eventModel2.getVictim().getTotalNumberOfInjured().intValue())))
                        .andExpect(jsonPath("content[1].victim.numberOfPerpetratorsInjured",
                                is(eventModel2.getVictim().getNumberOfPerpetratorsInjured().intValue())))
                        .andExpect(jsonPath("content[1].victim.valueOfPropertyDamage",
                                is(eventModel2.getVictim().getValueOfPropertyDamage().intValue())))

                        .andExpect(jsonPath("content[2].id", is(eventModel3.getId().intValue())))
                        .andExpect(jsonPath("content[2].summary", is(eventModel3.getSummary())))
                        .andExpect(jsonPath("content[2].motive", is(eventModel3.getMotive())))
                        .andExpect(jsonPath("content[2].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventModel2.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("content[2].isSuicidal", is(eventModel3.getIsSuicidal())))
                        .andExpect(jsonPath("content[2].isSuccessful", is(eventModel3.getIsSuccessful())))
                        .andExpect(jsonPath("content[2].isPartOfMultipleIncidents",
                                is(eventModel3.getIsPartOfMultipleIncidents())))
                        .andExpect(
                                jsonPath("content[2].links[0].href", is(eventModel3.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[2].links[1].href",
                                is(eventModel3.getLink("target").get().getHref())))
                        .andExpect(jsonPath("content[2].target.links[0].href",
                                is(eventModel3.getTarget().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[2].city.links[0].href",
                                is(eventModel3.getCity().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[2].victim.links[0].href",
                                is(eventModel3.getVictim().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[2].target.id", is(eventModel3.getTarget().getId().intValue())))
                        .andExpect(jsonPath("content[2].target.target", is(eventModel3.getTarget().getTarget())))
                        .andExpect(jsonPath("content[2].city.id", is(eventModel3.getCity().getId().intValue())))
                        .andExpect(jsonPath("content[2].city.name", is(eventModel3.getCity().getName())))
                        .andExpect(jsonPath("content[2].city.latitude", is(eventModel3.getCity().getLatitude())))
                        .andExpect(jsonPath("content[2].city.longitude", is(eventModel3.getCity().getLongitude())))
                        .andExpect(jsonPath("content[2].victim.id", is(eventModel3.getVictim().getId().intValue())))
                        .andExpect(jsonPath("content[2].victim.totalNumberOfFatalities",
                                is(eventModel3.getVictim().getTotalNumberOfFatalities().intValue())))
                        .andExpect(jsonPath("content[2].victim.numberOfPerpetratorsFatalities",
                                is(eventModel3.getVictim().getNumberOfPerpetratorsFatalities().intValue())))
                        .andExpect(jsonPath("content[2].victim.totalNumberOfInjured",
                                is(eventModel3.getVictim().getTotalNumberOfInjured().intValue())))
                        .andExpect(jsonPath("content[2].victim.numberOfPerpetratorsInjured",
                                is(eventModel3.getVictim().getNumberOfPerpetratorsInjured().intValue())))
                        .andExpect(jsonPath("content[2].victim.valueOfPropertyDamage",
                                is(eventModel3.getVictim().getValueOfPropertyDamage().intValue())))

                        .andExpect(jsonPath("content[3].id", is(eventModel4.getId().intValue())))
                        .andExpect(jsonPath("content[3].summary", is(eventModel4.getSummary())))
                        .andExpect(jsonPath("content[3].motive", is(eventModel4.getMotive())))
                        .andExpect(jsonPath("content[3].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventModel4.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("content[3].isSuicidal", is(eventModel4.getIsSuicidal())))
                        .andExpect(jsonPath("content[3].isSuccessful", is(eventModel4.getIsSuccessful())))
                        .andExpect(jsonPath("content[3].isPartOfMultipleIncidents",
                                is(eventModel4.getIsPartOfMultipleIncidents())))
                        .andExpect(
                                jsonPath("content[3].links[0].href", is(eventModel4.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[3].links[1].href",
                                is(eventModel4.getLink("target").get().getHref())))
                        .andExpect(jsonPath("content[3].target.links[0].href",
                                is(eventModel4.getTarget().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[3].city.links[0].href",
                                is(eventModel4.getCity().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[3].victim.links[0].href",
                                is(eventModel4.getVictim().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[3].target.id", is(eventModel4.getTarget().getId().intValue())))
                        .andExpect(jsonPath("content[3].target.target", is(eventModel4.getTarget().getTarget())))
                        .andExpect(jsonPath("content[3].city.id", is(eventModel4.getCity().getId().intValue())))
                        .andExpect(jsonPath("content[3].city.name", is(eventModel4.getCity().getName())))
                        .andExpect(jsonPath("content[3].city.latitude", is(eventModel4.getCity().getLatitude())))
                        .andExpect(jsonPath("content[3].city.longitude", is(eventModel4.getCity().getLongitude())))
                        .andExpect(jsonPath("content[3].victim.id", is(eventModel4.getVictim().getId().intValue())))
                        .andExpect(jsonPath("content[3].victim.totalNumberOfFatalities",
                                is(eventModel4.getVictim().getTotalNumberOfFatalities().intValue())))
                        .andExpect(jsonPath("content[3].victim.numberOfPerpetratorsFatalities",
                                is(eventModel4.getVictim().getNumberOfPerpetratorsFatalities().intValue())))
                        .andExpect(jsonPath("content[3].victim.totalNumberOfInjured",
                                is(eventModel4.getVictim().getTotalNumberOfInjured().intValue())))
                        .andExpect(jsonPath("content[3].victim.numberOfPerpetratorsInjured",
                                is(eventModel4.getVictim().getNumberOfPerpetratorsInjured().intValue())))
                        .andExpect(jsonPath("content[3].victim.valueOfPropertyDamage",
                                is(eventModel4.getVictim().getValueOfPropertyDamage().intValue())))

                        .andExpect(jsonPath("page.size", is(sizeExpected)))
                        .andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
                        .andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
                        .andExpect(jsonPath("page.number", is(numberExpected))),
                () -> verify(eventService, times(1)).findAll(pageable),
                () -> verifyNoMoreInteractions(eventService),
                () -> verify(pagedResourcesAssembler, times(1)).toModel(eventsExpected, modelAssembler),
                () -> verifyNoMoreInteractions(pagedResourcesAssembler),
                () -> verifyNoInteractions(patchUtil),
                () -> verifyNoInteractions(violationUtil));
    }

    @Test
    void when_find_all_events_with_changed_parameters_in_link_and_events_exist_should_return_all_events() {

        EventNode eventNode1 = (EventNode) createEvent(ObjectType.NODE);
        EventNode eventNode2 = (EventNode) createEvent(ObjectType.NODE);
        EventNode eventNode3 = (EventNode) createEvent(ObjectType.NODE);
        EventNode eventNode4 = (EventNode) createEvent(ObjectType.NODE);

        EventModel eventModel1 = (EventModel) createEvent(ObjectType.MODEL);
        EventModel eventModel2 = (EventModel) createEvent(ObjectType.MODEL);
        EventModel eventModel3 = (EventModel) createEvent(ObjectType.MODEL);

        List<EventModel> eventModelsListExpected = List.of(eventModel1, eventModel2, eventModel3);
        List<EventNode> eventNodesListExpected = List.of(eventNode1, eventNode2, eventNode3, eventNode4);

        int sizeExpected = 3;
        int totalElementsExpected = 4;
        int totalPagesExpected = 2;
        int numberExpected = 0;
        int pageExpected = 0;
        int lastPageExpected = 1;

        Pageable pageable = PageRequest.of(pageExpected, sizeExpected);
        PageImpl<EventNode> eventsExpected = new PageImpl<>(eventNodesListExpected, pageable, eventNodesListExpected.size());

        String urlParameters1 = "?page=" + pageExpected + "&size=" + sizeExpected;
        String urlParameters2 = "?page=" + lastPageExpected + "&size=" + sizeExpected;
        String firstPageLink = EVENT_BASE_PATH + urlParameters1;
        String lastPageLink = EVENT_BASE_PATH + urlParameters2;

        Link pageLink1 = new Link(firstPageLink, "first");
        Link pageLink2 = new Link(firstPageLink, "self");
        Link pageLink3 = new Link(lastPageLink, "next");
        Link pageLink4 = new Link(lastPageLink, "last");

        PageMetadata metadata = new PagedModel.PageMetadata(sizeExpected, numberExpected, totalElementsExpected);
        PagedModel<EventModel> resources = new PagedModel<>(eventModelsListExpected, metadata, pageLink1, pageLink2,
                pageLink3, pageLink4);

        when(eventService.findAll(pageable)).thenReturn(eventsExpected);
        when(pagedResourcesAssembler.toModel(eventsExpected, modelAssembler)).thenReturn(resources);

        assertAll(
                () -> mockMvc.perform(get(firstPageLink)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[1].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[2].href", is(lastPageLink)))
                        .andExpect(jsonPath("links[3].href", is(lastPageLink)))
                        .andExpect(jsonPath("content[0].id", is(eventModel1.getId().intValue())))
                        .andExpect(jsonPath("content[0].summary", is(eventModel1.getSummary())))
                        .andExpect(jsonPath("content[0].motive", is(eventModel1.getMotive())))
                        .andExpect(jsonPath("content[0].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventModel1.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("content[0].isSuicidal", is(eventModel1.getIsSuicidal())))
                        .andExpect(jsonPath("content[0].isSuccessful", is(eventModel1.getIsSuccessful())))
                        .andExpect(jsonPath("content[0].isPartOfMultipleIncidents",
                                is(eventModel1.getIsPartOfMultipleIncidents())))
                        .andExpect(
                                jsonPath("content[0].links[0].href", is(eventModel1.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[0].links[1].href",
                                is(eventModel1.getLink("target").get().getHref())))
                        .andExpect(jsonPath("content[0].target.links[0].href",
                                is(eventModel1.getTarget().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[0].city.links[0].href",
                                is(eventModel1.getCity().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[0].victim.links[0].href",
                                is(eventModel1.getVictim().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[0].target.id", is(eventModel1.getTarget().getId().intValue())))
                        .andExpect(jsonPath("content[0].target.target", is(eventModel1.getTarget().getTarget())))
                        .andExpect(jsonPath("content[0].city.id", is(eventModel1.getCity().getId().intValue())))
                        .andExpect(jsonPath("content[0].city.name", is(eventModel1.getCity().getName())))
                        .andExpect(jsonPath("content[0].city.latitude", is(eventModel1.getCity().getLatitude())))
                        .andExpect(jsonPath("content[0].city.longitude", is(eventModel1.getCity().getLongitude())))
                        .andExpect(jsonPath("content[0].victim.id", is(eventModel1.getVictim().getId().intValue())))
                        .andExpect(jsonPath("content[0].victim.totalNumberOfFatalities",
                                is(eventModel1.getVictim().getTotalNumberOfFatalities().intValue())))
                        .andExpect(jsonPath("content[0].victim.numberOfPerpetratorsFatalities",
                                is(eventModel1.getVictim().getNumberOfPerpetratorsFatalities().intValue())))
                        .andExpect(jsonPath("content[0].victim.totalNumberOfInjured",
                                is(eventModel1.getVictim().getTotalNumberOfInjured().intValue())))
                        .andExpect(jsonPath("content[0].victim.numberOfPerpetratorsInjured",
                                is(eventModel1.getVictim().getNumberOfPerpetratorsInjured().intValue())))
                        .andExpect(jsonPath("content[0].victim.valueOfPropertyDamage",
                                is(eventModel1.getVictim().getValueOfPropertyDamage().intValue())))

                        .andExpect(jsonPath("content[1].id", is(eventModel2.getId().intValue())))
                        .andExpect(jsonPath("content[1].summary", is(eventModel2.getSummary())))
                        .andExpect(jsonPath("content[1].motive", is(eventModel2.getMotive())))
                        .andExpect(jsonPath("content[1].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventModel2.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("content[1].isSuicidal", is(eventModel2.getIsSuicidal())))
                        .andExpect(jsonPath("content[1].isSuccessful", is(eventModel2.getIsSuccessful())))
                        .andExpect(jsonPath("content[1].isPartOfMultipleIncidents",
                                is(eventModel2.getIsPartOfMultipleIncidents())))
                        .andExpect(
                                jsonPath("content[1].links[0].href", is(eventModel2.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[1].links[1].href",
                                is(eventModel2.getLink("target").get().getHref())))
                        .andExpect(jsonPath("content[1].target.links[0].href",
                                is(eventModel2.getTarget().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[1].city.links[0].href",
                                is(eventModel2.getCity().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[1].victim.links[0].href",
                                is(eventModel2.getVictim().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[1].target.id", is(eventModel2.getTarget().getId().intValue())))
                        .andExpect(jsonPath("content[1].target.target", is(eventModel2.getTarget().getTarget())))
                        .andExpect(jsonPath("content[1].city.id", is(eventModel2.getCity().getId().intValue())))
                        .andExpect(jsonPath("content[1].city.name", is(eventModel2.getCity().getName())))
                        .andExpect(jsonPath("content[1].city.latitude", is(eventModel2.getCity().getLatitude())))
                        .andExpect(jsonPath("content[1].city.longitude", is(eventModel2.getCity().getLongitude())))
                        .andExpect(jsonPath("content[1].victim.id", is(eventModel2.getVictim().getId().intValue())))
                        .andExpect(jsonPath("content[1].victim.totalNumberOfFatalities",
                                is(eventModel2.getVictim().getTotalNumberOfFatalities().intValue())))
                        .andExpect(jsonPath("content[1].victim.numberOfPerpetratorsFatalities",
                                is(eventModel2.getVictim().getNumberOfPerpetratorsFatalities().intValue())))
                        .andExpect(jsonPath("content[1].victim.totalNumberOfInjured",
                                is(eventModel2.getVictim().getTotalNumberOfInjured().intValue())))
                        .andExpect(jsonPath("content[1].victim.numberOfPerpetratorsInjured",
                                is(eventModel2.getVictim().getNumberOfPerpetratorsInjured().intValue())))
                        .andExpect(jsonPath("content[1].victim.valueOfPropertyDamage",
                                is(eventModel2.getVictim().getValueOfPropertyDamage().intValue())))

                        .andExpect(jsonPath("content[2].id", is(eventModel3.getId().intValue())))
                        .andExpect(jsonPath("content[2].summary", is(eventModel3.getSummary())))
                        .andExpect(jsonPath("content[2].motive", is(eventModel3.getMotive())))
                        .andExpect(jsonPath("content[2].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventModel2.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("content[2].isSuicidal", is(eventModel3.getIsSuicidal())))
                        .andExpect(jsonPath("content[2].isSuccessful", is(eventModel3.getIsSuccessful())))
                        .andExpect(jsonPath("content[2].isPartOfMultipleIncidents",
                                is(eventModel3.getIsPartOfMultipleIncidents())))
                        .andExpect(
                                jsonPath("content[2].links[0].href", is(eventModel3.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[2].links[1].href",
                                is(eventModel3.getLink("target").get().getHref())))
                        .andExpect(jsonPath("content[2].target.links[0].href",
                                is(eventModel3.getTarget().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[2].city.links[0].href",
                                is(eventModel3.getCity().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[2].victim.links[0].href",
                                is(eventModel3.getVictim().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[2].target.id", is(eventModel3.getTarget().getId().intValue())))
                        .andExpect(jsonPath("content[2].target.target", is(eventModel3.getTarget().getTarget())))
                        .andExpect(jsonPath("content[2].city.id", is(eventModel3.getCity().getId().intValue())))
                        .andExpect(jsonPath("content[2].city.name", is(eventModel3.getCity().getName())))
                        .andExpect(jsonPath("content[2].city.latitude", is(eventModel3.getCity().getLatitude())))
                        .andExpect(jsonPath("content[2].city.longitude", is(eventModel3.getCity().getLongitude())))
                        .andExpect(jsonPath("content[2].victim.id", is(eventModel3.getVictim().getId().intValue())))
                        .andExpect(jsonPath("content[2].victim.totalNumberOfFatalities",
                                is(eventModel3.getVictim().getTotalNumberOfFatalities().intValue())))
                        .andExpect(jsonPath("content[2].victim.numberOfPerpetratorsFatalities",
                                is(eventModel3.getVictim().getNumberOfPerpetratorsFatalities().intValue())))
                        .andExpect(jsonPath("content[2].victim.totalNumberOfInjured",
                                is(eventModel3.getVictim().getTotalNumberOfInjured().intValue())))
                        .andExpect(jsonPath("content[2].victim.numberOfPerpetratorsInjured",
                                is(eventModel3.getVictim().getNumberOfPerpetratorsInjured().intValue())))
                        .andExpect(jsonPath("content[2].victim.valueOfPropertyDamage",
                                is(eventModel3.getVictim().getValueOfPropertyDamage().intValue())))

                        .andExpect(jsonPath("content[3]").doesNotExist())
                        .andExpect(jsonPath("page.size", is(sizeExpected)))
                        .andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
                        .andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
                        .andExpect(jsonPath("page.number", is(numberExpected))),
                () -> verify(eventService, times(1)).findAll(pageable),
                () -> verifyNoMoreInteractions(eventService),
                () -> verify(pagedResourcesAssembler, times(1)).toModel(eventsExpected, modelAssembler),
                () -> verifyNoMoreInteractions(pagedResourcesAssembler),
                () -> verifyNoInteractions(patchUtil),
                () -> verifyNoInteractions(violationUtil));
    }

    @Test
    void when_find_all_events_but_events_not_exist_should_return_empty_list() {

        List<EventNode> eventsListExpected = new ArrayList<>();

        List<EventModel> modelsListExpected = new ArrayList<>();

        Page<EventNode> eventsExpected = new PageImpl<>(eventsListExpected);

        int sizeExpected = 20;
        int totalElementsExpected = 0;
        int totalPagesExpected = 0;
        int numberExpected = 0;
        int pageExpected = 0;
        int lastPageExpected = 0;

        Pageable pageable = PageRequest.of(pageExpected, sizeExpected);

        String urlParameters1 = "?page=" + pageExpected + "&size=" + sizeExpected;
        String urlParameters2 = "?page=" + lastPageExpected + "&size=" + sizeExpected;
        String firstPageLink = EVENT_BASE_PATH + urlParameters1;
        String lastPageLink = EVENT_BASE_PATH + urlParameters2;

        Link pageLink1 = new Link(firstPageLink, "first");
        Link pageLink2 = new Link(firstPageLink, "self");
        Link pageLink3 = new Link(lastPageLink, "next");
        Link pageLink4 = new Link(lastPageLink, "last");

        PageMetadata metadata = new PagedModel.PageMetadata(sizeExpected, numberExpected, totalElementsExpected);
        PagedModel<EventModel> resources = new PagedModel<>(modelsListExpected, metadata, pageLink1, pageLink2,
                pageLink3, pageLink4);

        when(eventService.findAll(pageable)).thenReturn(eventsExpected);
        when(pagedResourcesAssembler.toModel(eventsExpected, modelAssembler)).thenReturn(resources);

        assertAll(
                () -> mockMvc.perform(get(firstPageLink)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[1].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[2].href", is(lastPageLink)))
                        .andExpect(jsonPath("links[3].href", is(lastPageLink)))
                        .andExpect(jsonPath("content").isEmpty())
                        .andExpect(jsonPath("page.size", is(sizeExpected)))
                        .andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
                        .andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
                        .andExpect(jsonPath("page.number", is(numberExpected))),
                () -> verify(eventService, times(1)).findAll(pageable),
                () -> verifyNoMoreInteractions(eventService),
                () -> verify(pagedResourcesAssembler, times(1)).toModel(eventsExpected, modelAssembler),
                () -> verifyNoMoreInteractions(pagedResourcesAssembler),
                () -> verifyNoInteractions(patchUtil),
                () -> verifyNoInteractions(violationUtil));
    }

    @Test
    void when_find_all_events_with_depth_and_events_exist_should_return_all_events_with_nested_objects() {

        int depth = 5;

        RegionNode regionNode1 = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNode1 = (CountryNode) countryBuilder.withRegion(regionNode1)
                .build(ObjectType.NODE);
        TargetNode targetNode1 = (TargetNode) targetBuilder.withCountry(countryNode1)
                .build(ObjectType.NODE);
        ProvinceNode provinceNode1 = (ProvinceNode) provinceBuilder.withCountry(countryNode1)
                .build(ObjectType.NODE);
        CityNode cityNode1 = (CityNode) cityBuilder.withProvince(provinceNode1).build(ObjectType.NODE);
        VictimNode victimNode1 = (VictimNode) victimBuilder.build(ObjectType.NODE);
        EventNode eventNode1 = (EventNode) eventBuilder.withTarget(targetNode1).withCity(cityNode1)
                .withVictim(victimNode1).build(ObjectType.NODE);

        String pathToRegionLink1 = REGION_BASE_PATH + "/" + regionNode1.getId().intValue();
        String pathToCountryLink1 = COUNTRY_BASE_PATH + "/" + countryNode1.getId().intValue();
        String pathToProvinceLink1 = PROVINCE_BASE_PATH + "/" + provinceNode1.getId().intValue();
        String pathToTargetLink1 = TARGET_BASE_PATH + "/" + targetNode1.getId().intValue();
        String pathToCityLink1 = CITY_BASE_PATH + "/" + cityNode1.getId().intValue();
        String pathToVictimLink1 = VICTIM_BASE_PATH + "/" + victimNode1.getId().intValue();
        String pathToEventLink1 = EVENT_BASE_PATH + "/" + eventNode1.getId().intValue();
        String pathToTargetEventLink1 = EVENT_BASE_PATH + "/" + eventNode1.getId().intValue() + "/targets";

        RegionModel regionModel1 = (RegionModel) regionBuilder.build(ObjectType.MODEL);
        regionModel1.add(new Link(pathToRegionLink1));
        CountryModel countryModel1 = (CountryModel) countryBuilder.withRegion(regionModel1)
                .build(ObjectType.MODEL);
        countryModel1.add(new Link(pathToCountryLink1));
        TargetModel targetModel1 = (TargetModel) targetBuilder.withCountry(countryModel1)
                .build(ObjectType.MODEL);
        targetModel1.add(new Link(pathToTargetLink1));
        ProvinceModel provinceModel1 = (ProvinceModel) provinceBuilder.withCountry(countryModel1)
                .build(ObjectType.MODEL);
        provinceModel1.add(new Link(pathToProvinceLink1));
        CityModel cityModel1 = (CityModel) cityBuilder.withProvince(provinceModel1).build(ObjectType.MODEL);
        cityModel1.add(new Link(pathToCityLink1));
        VictimModel victimModel1 = (VictimModel) victimBuilder.build(ObjectType.MODEL);
        victimModel1.add(new Link(pathToVictimLink1));
        EventModel eventModel1 = (EventModel) eventBuilder.withTarget(targetModel1).withCity(cityModel1)
                .withVictim(victimModel1).build(ObjectType.MODEL);
        eventModel1.add(new Link(pathToEventLink1));
        eventModel1.add(new Link(pathToTargetEventLink1));

        RegionNode regionNode2 = (RegionNode) regionBuilder.withName("region 2").build(ObjectType.NODE);
        CountryNode countryNode2 = (CountryNode) countryBuilder.withName("country 2").withRegion(regionNode2)
                .build(ObjectType.NODE);
        TargetNode targetNode2 = (TargetNode) targetBuilder.withTarget("target 2").withCountry(countryNode2)
                .build(ObjectType.NODE);
        ProvinceNode provinceNode2 = (ProvinceNode) provinceBuilder.withName("province 2").withCountry(countryNode2)
                .build(ObjectType.NODE);
        CityNode cityNode2 = (CityNode) cityBuilder.withName("city 2").withProvince(provinceNode2).build(ObjectType.NODE);
        VictimNode victimNode2 = (VictimNode) victimBuilder.build(ObjectType.NODE);
        EventNode eventNode2 = (EventNode) eventBuilder.withSummary("summary 2").withMotive("motive 2")
                .withTarget(targetNode2).withCity(cityNode2)
                .withVictim(victimNode2).build(ObjectType.NODE);

        String pathToRegionLink2 = REGION_BASE_PATH + "/" + regionNode2.getId().intValue();
        String pathToCountryLink2 = COUNTRY_BASE_PATH + "/" + countryNode2.getId().intValue();
        String pathToProvinceLink2 = PROVINCE_BASE_PATH + "/" + provinceNode2.getId().intValue();
        String pathToTargetLink2 = TARGET_BASE_PATH + "/" + targetNode2.getId().intValue();
        String pathToCityLink2 = CITY_BASE_PATH + "/" + cityNode2.getId().intValue();
        String pathToVictimLink2 = VICTIM_BASE_PATH + "/" + victimNode2.getId().intValue();
        String pathToEventLink2 = EVENT_BASE_PATH + "/" + eventNode2.getId().intValue();
        String pathToTargetEventLink2 = EVENT_BASE_PATH + "/" + eventNode2.getId().intValue() + "/targets";

        RegionModel regionModel2 = (RegionModel) regionBuilder.withName("region 2").build(ObjectType.MODEL);
        regionModel2.add(new Link(pathToRegionLink2));
        CountryModel countryModel2 = (CountryModel) countryBuilder.withName("country 2").withRegion(regionModel2)
                .build(ObjectType.MODEL);
        countryModel2.add(new Link(pathToCountryLink2));
        TargetModel targetModel2 = (TargetModel) targetBuilder.withTarget("target 2").withCountry(countryModel2)
                .build(ObjectType.MODEL);
        targetModel2.add(new Link(pathToTargetLink2));
        ProvinceModel provinceModel2 = (ProvinceModel) provinceBuilder.withName("province 2").withCountry(countryModel2)
                .build(ObjectType.MODEL);
        provinceModel2.add(new Link(pathToProvinceLink2));
        CityModel cityModel2 = (CityModel) cityBuilder.withName("city 2").withProvince(provinceModel2).build(ObjectType.MODEL);
        cityModel2.add(new Link(pathToCityLink2));
        VictimModel victimModel2 = (VictimModel) victimBuilder.build(ObjectType.MODEL);
        victimModel2.add(new Link(pathToVictimLink2));
        EventModel eventModel2 = (EventModel) eventBuilder.withSummary("summary 2").withMotive("motive 2")
                .withTarget(targetModel2).withCity(cityModel2)
                .withVictim(victimModel2).build(ObjectType.MODEL);
        eventModel2.add(new Link(pathToEventLink2));
        eventModel2.add(new Link(pathToTargetEventLink2));

        List<EventNode> eventNodesListExpected = List.of(eventNode1, eventNode2);
        List<EventModel> eventModelsListExpected = List.of(eventModel1, eventModel2);
        Page<EventNode> eventsExpected = new PageImpl<>(eventNodesListExpected);

        int sizeExpected = 20;
        int totalElementsExpected = 2;
        int totalPagesExpected = 1;
        int numberExpected = 0;
        int pageExpected = 0;
        int lastPageExpected = 0;

        Pageable pageable = PageRequest.of(pageExpected, sizeExpected);

        String urlParameters1 = "?page=" + pageExpected + "&size=" + sizeExpected;
        String urlParameters2 = "?page=" + lastPageExpected + "&size=" + sizeExpected;
        String firstPageLink = EVENT_BASE_PATH + urlParameters1;
        String lastPageLink = EVENT_BASE_PATH + urlParameters2;

        Link pageLink1 = new Link(firstPageLink, "first");
        Link pageLink2 = new Link(firstPageLink, "self");
        Link pageLink3 = new Link(lastPageLink, "next");
        Link pageLink4 = new Link(lastPageLink, "last");

        PageMetadata metadata = new PagedModel.PageMetadata(sizeExpected, numberExpected, totalElementsExpected);
        PagedModel<EventModel> resources = new PagedModel<>(eventModelsListExpected, metadata, pageLink1, pageLink2,
                pageLink3, pageLink4);

        String linkWithParameter = EVENT_BASE_PATH + "/depth/" + "{depth}";

        when(eventService.findAll(pageable, depth)).thenReturn(eventsExpected);
        when(pagedResourcesAssembler.toModel(eventsExpected, modelAssembler)).thenReturn(resources);

        assertAll(
                () -> mockMvc.perform(get(linkWithParameter, depth).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[1].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[2].href", is(lastPageLink)))
                        .andExpect(jsonPath("links[3].href", is(lastPageLink)))
                        .andExpect(jsonPath("content[0].links[0].href", is(pathToEventLink1)))
                        .andExpect(jsonPath("content[0].links[1].href", is(pathToTargetEventLink1)))
                        .andExpect(jsonPath("content[0].id", is(eventModel1.getId().intValue())))
                        .andExpect(jsonPath("content[0].summary", is(eventModel1.getSummary())))
                        .andExpect(jsonPath("content[0].motive", is(eventModel1.getMotive())))
                        .andExpect(jsonPath("content[0].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventModel1.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("content[0].isSuicidal", is(eventModel1.getIsSuicidal())))
                        .andExpect(jsonPath("content[0].isSuccessful", is(eventModel1.getIsSuccessful())))
                        .andExpect(jsonPath("content[0].isPartOfMultipleIncidents",
                                is(eventModel1.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("content[0].target.links[0].href", is(pathToTargetLink1)))
                        .andExpect(jsonPath("content[0].target.links[1].href").doesNotExist())
                        .andExpect(jsonPath("content[0].target.id", is(targetModel1.getId().intValue())))
                        .andExpect(jsonPath("content[0].target.target", is(targetModel1.getTarget())))
                        .andExpect(jsonPath("content[0].target.countryOfOrigin.links[0].href", is(pathToCountryLink1)))
                        .andExpect(jsonPath("content[0].target.countryOfOrigin.links[1].href").doesNotExist())
                        .andExpect(jsonPath("content[0].target.countryOfOrigin.id",
                                is(countryModel1.getId().intValue())))
                        .andExpect(jsonPath("content[0].target.countryOfOrigin.name", is(countryModel1.getName())))
                        .andExpect(jsonPath("content[0].target.countryOfOrigin.region.links[0].href",
                                is(pathToRegionLink1)))
                        .andExpect(jsonPath("content[0].target.countryOfOrigin.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("content[0].target.countryOfOrigin.region.id",
                                is(regionModel1.getId().intValue())))
                        .andExpect(jsonPath("content[0].target.countryOfOrigin.region.name", is(regionModel1.getName())))
                        .andExpect(jsonPath("content[0].city.links[0].href", is(pathToCityLink1)))
                        .andExpect(jsonPath("content[0].city.links[1].href").doesNotExist())
                        .andExpect(jsonPath("content[0].city.id", is(cityModel1.getId().intValue())))
                        .andExpect(jsonPath("content[0].city.name", is(cityModel1.getName())))
                        .andExpect(jsonPath("content[0].city.latitude", is(cityModel1.getLatitude())))
                        .andExpect(jsonPath("content[0].city.longitude", is(cityModel1.getLongitude())))
                        .andExpect(jsonPath("content[0].city.province.links[0].href", is(pathToProvinceLink1)))
                        .andExpect(jsonPath("content[0].city.province.links[1].href").doesNotExist())
                        .andExpect(jsonPath("content[0].city.province.id", is(provinceModel1.getId().intValue())))
                        .andExpect(jsonPath("content[0].city.province.name", is(provinceModel1.getName())))
                        .andExpect(jsonPath("content[0].city.province.country.links[0].href", is(pathToCountryLink1)))
                        .andExpect(jsonPath("content[0].city.province.country.links[1].href").doesNotExist())
                        .andExpect(jsonPath("content[0].city.province.country.id", is(countryModel1.getId().intValue())))
                        .andExpect(jsonPath("content[0].city.province.country.name", is(countryModel1.getName())))
                        .andExpect(jsonPath("content[0].city.province.country.region.links[0].href",
                                is(pathToRegionLink1)))
                        .andExpect(jsonPath("content[0].city.province.country.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("content[0].city.province.country.region.id",
                                is(regionModel1.getId().intValue())))
                        .andExpect(jsonPath("content[0].city.province.country.region.name", is(regionModel1.getName())))
                        .andExpect(jsonPath("content[0].victim.links[0].href", is(pathToVictimLink1)))
                        .andExpect(jsonPath("content[0].victim.links[1].href").doesNotExist())
                        .andExpect(jsonPath("content[0].victim.id", is(victimModel1.getId().intValue())))
                        .andExpect(jsonPath("content[0].victim.totalNumberOfFatalities",
                                is(victimModel1.getTotalNumberOfFatalities().intValue())))
                        .andExpect(jsonPath("content[0].victim.numberOfPerpetratorsFatalities",
                                is(victimModel1.getNumberOfPerpetratorsFatalities().intValue())))
                        .andExpect(jsonPath("content[0].victim.totalNumberOfInjured",
                                is(victimModel1.getTotalNumberOfInjured().intValue())))
                        .andExpect(jsonPath("content[0].victim.numberOfPerpetratorsInjured",
                                is(victimModel1.getNumberOfPerpetratorsInjured().intValue())))
                        .andExpect(jsonPath("content[0].victim.valueOfPropertyDamage",
                                is(victimModel1.getValueOfPropertyDamage().intValue())))

                        .andExpect(jsonPath("content[1].links[0].href", is(pathToEventLink2)))
                        .andExpect(jsonPath("content[1].links[1].href", is(pathToTargetEventLink2)))
                        .andExpect(jsonPath("content[1].id", is(eventModel2.getId().intValue())))
                        .andExpect(jsonPath("content[1].summary", is(eventModel2.getSummary())))
                        .andExpect(jsonPath("content[1].motive", is(eventModel2.getMotive())))
                        .andExpect(jsonPath("content[1].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventModel2.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("content[1].isSuicidal", is(eventModel2.getIsSuicidal())))
                        .andExpect(jsonPath("content[1].isSuccessful", is(eventModel2.getIsSuccessful())))
                        .andExpect(jsonPath("content[1].isPartOfMultipleIncidents",
                                is(eventModel2.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("content[1].target.links[0].href", is(pathToTargetLink2)))
                        .andExpect(jsonPath("content[1].target.links[1].href").doesNotExist())
                        .andExpect(jsonPath("content[1].target.id", is(targetModel2.getId().intValue())))
                        .andExpect(jsonPath("content[1].target.target", is(targetModel2.getTarget())))
                        .andExpect(jsonPath("content[1].target.countryOfOrigin.links[0].href", is(pathToCountryLink2)))
                        .andExpect(jsonPath("content[1].target.countryOfOrigin.links[1].href").doesNotExist())
                        .andExpect(jsonPath("content[1].target.countryOfOrigin.id",
                                is(countryModel2.getId().intValue())))
                        .andExpect(jsonPath("content[1].target.countryOfOrigin.name", is(countryModel2.getName())))
                        .andExpect(jsonPath("content[1].target.countryOfOrigin.region.links[0].href",
                                is(pathToRegionLink2)))
                        .andExpect(jsonPath("content[1].target.countryOfOrigin.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("content[1].target.countryOfOrigin.region.id",
                                is(regionModel2.getId().intValue())))
                        .andExpect(jsonPath("content[1].target.countryOfOrigin.region.name", is(regionModel2.getName())))
                        .andExpect(jsonPath("content[1].city.links[0].href", is(pathToCityLink2)))
                        .andExpect(jsonPath("content[1].city.links[1].href").doesNotExist())
                        .andExpect(jsonPath("content[1].city.id", is(cityModel2.getId().intValue())))
                        .andExpect(jsonPath("content[1].city.name", is(cityModel2.getName())))
                        .andExpect(jsonPath("content[1].city.latitude", is(cityModel2.getLatitude())))
                        .andExpect(jsonPath("content[1].city.longitude", is(cityModel2.getLongitude())))
                        .andExpect(jsonPath("content[1].city.province.links[0].href", is(pathToProvinceLink2)))
                        .andExpect(jsonPath("content[1].city.province.links[1].href").doesNotExist())
                        .andExpect(jsonPath("content[1].city.province.id", is(provinceModel2.getId().intValue())))
                        .andExpect(jsonPath("content[1].city.province.name", is(provinceModel2.getName())))
                        .andExpect(jsonPath("content[1].city.province.country.links[0].href", is(pathToCountryLink2)))
                        .andExpect(jsonPath("content[1].city.province.country.links[1].href").doesNotExist())
                        .andExpect(jsonPath("content[1].city.province.country.id", is(countryModel2.getId().intValue())))
                        .andExpect(jsonPath("content[1].city.province.country.name", is(countryModel2.getName())))
                        .andExpect(jsonPath("content[1].city.province.country.region.links[0].href",
                                is(pathToRegionLink2)))
                        .andExpect(jsonPath("content[1].city.province.country.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("content[1].city.province.country.region.id",
                                is(regionModel2.getId().intValue())))
                        .andExpect(jsonPath("content[1].city.province.country.region.name",
                                is(regionModel2.getName())))
                        .andExpect(jsonPath("content[1].victim.links[0].href", is(pathToVictimLink2)))
                        .andExpect(jsonPath("content[1].victim.links[1].href").doesNotExist())
                        .andExpect(jsonPath("content[1].victim.id", is(victimModel2.getId().intValue())))
                        .andExpect(jsonPath("content[1].victim.totalNumberOfFatalities",
                                is(victimModel2.getTotalNumberOfFatalities().intValue())))
                        .andExpect(jsonPath("content[1].victim.numberOfPerpetratorsFatalities",
                                is(victimModel2.getNumberOfPerpetratorsFatalities().intValue())))
                        .andExpect(jsonPath("content[1].victim.totalNumberOfInjured",
                                is(victimModel2.getTotalNumberOfInjured().intValue())))
                        .andExpect(jsonPath("content[1].victim.numberOfPerpetratorsInjured",
                                is(victimModel2.getNumberOfPerpetratorsInjured().intValue())))
                        .andExpect(jsonPath("content[1].victim.valueOfPropertyDamage",
                                is(victimModel2.getValueOfPropertyDamage().intValue())))

                        .andExpect(jsonPath("page.size", is(sizeExpected)))
                        .andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
                        .andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
                        .andExpect(jsonPath("page.number", is(numberExpected))),
                () -> verify(eventService, times(1)).findAll(pageable, depth),
                () -> verifyNoMoreInteractions(eventService),
                () -> verify(pagedResourcesAssembler, times(1)).toModel(eventsExpected, modelAssembler),
                () -> verifyNoMoreInteractions(pagedResourcesAssembler),
                () -> verifyNoInteractions(patchUtil),
                () -> verifyNoInteractions(violationUtil));
    }

    @Test
    void when_find_all_events_with_negative_depth_should_return_events_without_nested_objects() {

        int depth = -5;
        int depthWhenProvidedNegativeDepth = 0;

        EventNode eventNode1 = (EventNode) eventBuilder.build(ObjectType.NODE);

        String pathToEventLink1 = EVENT_BASE_PATH + "/" + eventNode1.getId().intValue();
        String pathToTargetEventLink1 = EVENT_BASE_PATH + "/" + eventNode1.getId().intValue() + "/targets";

        EventModel eventModel1 = (EventModel) eventBuilder.build(ObjectType.MODEL);
        eventModel1.add(new Link(pathToEventLink1));
        eventModel1.add(new Link(pathToTargetEventLink1));

        EventNode eventNode2 = (EventNode) eventBuilder.withSummary("summary 2").withMotive("motive 2").build(ObjectType.NODE);

        String pathToEventLink2 = EVENT_BASE_PATH + "/" + eventNode2.getId().intValue();
        String pathToTargetEventLink2 = EVENT_BASE_PATH + "/" + eventNode2.getId().intValue() + "/targets";

        EventModel eventModel2 = (EventModel) eventBuilder.withSummary("summary 2").withMotive("motive 2").build(ObjectType.MODEL);
        eventModel2.add(new Link(pathToEventLink2));
        eventModel2.add(new Link(pathToTargetEventLink2));

        List<EventNode> eventNodesListExpected = List.of(eventNode1, eventNode2);
        List<EventModel> eventModelsListExpected = List.of(eventModel1, eventModel2);
        Page<EventNode> eventsExpected = new PageImpl<>(eventNodesListExpected);

        int sizeExpected = 20;
        int totalElementsExpected = 2;
        int totalPagesExpected = 1;
        int numberExpected = 0;
        int pageExpected = 0;
        int lastPageExpected = 0;

        Pageable pageable = PageRequest.of(pageExpected, sizeExpected);

        String urlParameters1 = "?page=" + pageExpected + "&size=" + sizeExpected;
        String urlParameters2 = "?page=" + lastPageExpected + "&size=" + sizeExpected;
        String firstPageLink = EVENT_BASE_PATH + urlParameters1;
        String lastPageLink = EVENT_BASE_PATH + urlParameters2;

        Link pageLink1 = new Link(firstPageLink, "first");
        Link pageLink2 = new Link(firstPageLink, "self");
        Link pageLink3 = new Link(lastPageLink, "next");
        Link pageLink4 = new Link(lastPageLink, "last");

        PageMetadata metadata = new PagedModel.PageMetadata(sizeExpected, numberExpected, totalElementsExpected);
        PagedModel<EventModel> resources = new PagedModel<>(eventModelsListExpected, metadata, pageLink1, pageLink2,
                pageLink3, pageLink4);

        String linkWithParameter = EVENT_BASE_PATH + "/depth/" + "{depth}";

        when(eventService.findAll(pageable, depthWhenProvidedNegativeDepth)).thenReturn(eventsExpected);
        when(pagedResourcesAssembler.toModel(eventsExpected, modelAssembler)).thenReturn(resources);

        assertAll(
                () -> mockMvc.perform(get(linkWithParameter, depth).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[1].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[2].href", is(lastPageLink)))
                        .andExpect(jsonPath("links[3].href", is(lastPageLink)))
                        .andExpect(jsonPath("content[0].links[0].href", is(pathToEventLink1)))
                        .andExpect(jsonPath("content[0].links[1].href", is(pathToTargetEventLink1)))
                        .andExpect(jsonPath("content[0].id", is(eventModel1.getId().intValue())))
                        .andExpect(jsonPath("content[0].summary", is(eventModel1.getSummary())))
                        .andExpect(jsonPath("content[0].motive", is(eventModel1.getMotive())))
                        .andExpect(jsonPath("content[0].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventModel1.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("content[0].isSuicidal", is(eventModel1.getIsSuicidal())))
                        .andExpect(jsonPath("content[0].isSuccessful", is(eventModel1.getIsSuccessful())))
                        .andExpect(jsonPath("content[0].isPartOfMultipleIncidents",
                                is(eventModel1.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("content[0].target").doesNotExist())
                        .andExpect(jsonPath("content[0].city").doesNotExist())
                        .andExpect(jsonPath("content[0].victim").doesNotExist())

                        .andExpect(jsonPath("content[1].links[0].href", is(pathToEventLink2)))
                        .andExpect(jsonPath("content[1].links[1].href", is(pathToTargetEventLink2)))
                        .andExpect(jsonPath("content[1].id", is(eventModel2.getId().intValue())))
                        .andExpect(jsonPath("content[1].summary", is(eventModel2.getSummary())))
                        .andExpect(jsonPath("content[1].motive", is(eventModel2.getMotive())))
                        .andExpect(jsonPath("content[1].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventModel2.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("content[1].isSuicidal", is(eventModel2.getIsSuicidal())))
                        .andExpect(jsonPath("content[1].isSuccessful", is(eventModel2.getIsSuccessful())))
                        .andExpect(jsonPath("content[1].isPartOfMultipleIncidents",
                                is(eventModel2.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("content[0].target").doesNotExist())
                        .andExpect(jsonPath("content[0].city").doesNotExist())
                        .andExpect(jsonPath("content[0].victim").doesNotExist())

                        .andExpect(jsonPath("page.size", is(sizeExpected)))
                        .andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
                        .andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
                        .andExpect(jsonPath("page.number", is(numberExpected))),
                () -> verify(eventService, times(1)).findAll(pageable, depthWhenProvidedNegativeDepth),
                () -> verifyNoMoreInteractions(eventService),
                () -> verify(pagedResourcesAssembler, times(1)).toModel(eventsExpected, modelAssembler),
                () -> verifyNoMoreInteractions(pagedResourcesAssembler),
                () -> verifyNoInteractions(patchUtil),
                () -> verifyNoInteractions(violationUtil));
    }

    @Test
    void when_find_all_events_with_large_depth_should_return_events_with_all_nested_objects() {

        int depth = 15;
        int depthWhenProvidedToBigDepth = 5;

        RegionNode regionNode1 = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNode1 = (CountryNode) countryBuilder.withRegion(regionNode1)
                .build(ObjectType.NODE);
        TargetNode targetNode1 = (TargetNode) targetBuilder.withCountry(countryNode1)
                .build(ObjectType.NODE);
        ProvinceNode provinceNode1 = (ProvinceNode) provinceBuilder.withCountry(countryNode1)
                .build(ObjectType.NODE);
        CityNode cityNode1 = (CityNode) cityBuilder.withProvince(provinceNode1).build(ObjectType.NODE);
        VictimNode victimNode1 = (VictimNode) victimBuilder.build(ObjectType.NODE);
        EventNode eventNode1 = (EventNode) eventBuilder.withTarget(targetNode1).withCity(cityNode1)
                .withVictim(victimNode1).build(ObjectType.NODE);

        String pathToRegionLink1 = REGION_BASE_PATH + "/" + regionNode1.getId().intValue();
        String pathToCountryLink1 = COUNTRY_BASE_PATH + "/" + countryNode1.getId().intValue();
        String pathToProvinceLink1 = PROVINCE_BASE_PATH + "/" + provinceNode1.getId().intValue();
        String pathToTargetLink1 = TARGET_BASE_PATH + "/" + targetNode1.getId().intValue();
        String pathToCityLink1 = CITY_BASE_PATH + "/" + cityNode1.getId().intValue();
        String pathToVictimLink1 = VICTIM_BASE_PATH + "/" + victimNode1.getId().intValue();
        String pathToEventLink1 = EVENT_BASE_PATH + "/" + eventNode1.getId().intValue();
        String pathToTargetEventLink1 = EVENT_BASE_PATH + "/" + eventNode1.getId().intValue() + "/targets";

        RegionModel regionModel1 = (RegionModel) regionBuilder.build(ObjectType.MODEL);
        regionModel1.add(new Link(pathToRegionLink1));
        CountryModel countryModel1 = (CountryModel) countryBuilder.withRegion(regionModel1)
                .build(ObjectType.MODEL);
        countryModel1.add(new Link(pathToCountryLink1));
        TargetModel targetModel1 = (TargetModel) targetBuilder.withCountry(countryModel1)
                .build(ObjectType.MODEL);
        targetModel1.add(new Link(pathToTargetLink1));
        ProvinceModel provinceModel1 = (ProvinceModel) provinceBuilder.withCountry(countryModel1)
                .build(ObjectType.MODEL);
        provinceModel1.add(new Link(pathToProvinceLink1));
        CityModel cityModel1 = (CityModel) cityBuilder.withProvince(provinceModel1).build(ObjectType.MODEL);
        cityModel1.add(new Link(pathToCityLink1));
        VictimModel victimModel1 = (VictimModel) victimBuilder.build(ObjectType.MODEL);
        victimModel1.add(new Link(pathToVictimLink1));
        EventModel eventModel1 = (EventModel) eventBuilder.withTarget(targetModel1).withCity(cityModel1)
                .withVictim(victimModel1).build(ObjectType.MODEL);
        eventModel1.add(new Link(pathToEventLink1));
        eventModel1.add(new Link(pathToTargetEventLink1));

        RegionNode regionNode2 = (RegionNode) regionBuilder.withName("region 2").build(ObjectType.NODE);
        CountryNode countryNode2 = (CountryNode) countryBuilder.withName("country 2").withRegion(regionNode2)
                .build(ObjectType.NODE);
        TargetNode targetNode2 = (TargetNode) targetBuilder.withTarget("target 2").withCountry(countryNode2)
                .build(ObjectType.NODE);
        ProvinceNode provinceNode2 = (ProvinceNode) provinceBuilder.withName("province 2").withCountry(countryNode2)
                .build(ObjectType.NODE);
        CityNode cityNode2 = (CityNode) cityBuilder.withName("city 2").withProvince(provinceNode2).build(ObjectType.NODE);
        VictimNode victimNode2 = (VictimNode) victimBuilder.build(ObjectType.NODE);
        EventNode eventNode2 = (EventNode) eventBuilder.withSummary("summary 2").withMotive("motive 2")
                .withTarget(targetNode2).withCity(cityNode2)
                .withVictim(victimNode2).build(ObjectType.NODE);

        String pathToRegionLink2 = REGION_BASE_PATH + "/" + regionNode2.getId().intValue();
        String pathToCountryLink2 = COUNTRY_BASE_PATH + "/" + countryNode2.getId().intValue();
        String pathToProvinceLink2 = PROVINCE_BASE_PATH + "/" + provinceNode2.getId().intValue();
        String pathToTargetLink2 = TARGET_BASE_PATH + "/" + targetNode2.getId().intValue();
        String pathToCityLink2 = CITY_BASE_PATH + "/" + cityNode2.getId().intValue();
        String pathToVictimLink2 = VICTIM_BASE_PATH + "/" + victimNode2.getId().intValue();
        String pathToEventLink2 = EVENT_BASE_PATH + "/" + eventNode2.getId().intValue();
        String pathToTargetEventLink2 = EVENT_BASE_PATH + "/" + eventNode2.getId().intValue() + "/targets";

        RegionModel regionModel2 = (RegionModel) regionBuilder.withName("region 2").build(ObjectType.MODEL);
        regionModel2.add(new Link(pathToRegionLink2));
        CountryModel countryModel2 = (CountryModel) countryBuilder.withName("country 2").withRegion(regionModel2)
                .build(ObjectType.MODEL);
        countryModel2.add(new Link(pathToCountryLink2));
        TargetModel targetModel2 = (TargetModel) targetBuilder.withTarget("target 2").withCountry(countryModel2)
                .build(ObjectType.MODEL);
        targetModel2.add(new Link(pathToTargetLink2));
        ProvinceModel provinceModel2 = (ProvinceModel) provinceBuilder.withName("province 2").withCountry(countryModel2)
                .build(ObjectType.MODEL);
        provinceModel2.add(new Link(pathToProvinceLink2));
        CityModel cityModel2 = (CityModel) cityBuilder.withName("city 2").withProvince(provinceModel2).build(ObjectType.MODEL);
        cityModel2.add(new Link(pathToCityLink2));
        VictimModel victimModel2 = (VictimModel) victimBuilder.build(ObjectType.MODEL);
        victimModel2.add(new Link(pathToVictimLink2));
        EventModel eventModel2 = (EventModel) eventBuilder.withSummary("summary 2").withMotive("motive 2")
                .withTarget(targetModel2).withCity(cityModel2)
                .withVictim(victimModel2).build(ObjectType.MODEL);
        eventModel2.add(new Link(pathToEventLink2));
        eventModel2.add(new Link(pathToTargetEventLink2));

        List<EventNode> eventNodesListExpected = List.of(eventNode1, eventNode2);
        List<EventModel> eventModelsListExpected = List.of(eventModel1, eventModel2);
        Page<EventNode> eventsExpected = new PageImpl<>(eventNodesListExpected);

        int sizeExpected = 20;
        int totalElementsExpected = 2;
        int totalPagesExpected = 1;
        int numberExpected = 0;
        int pageExpected = 0;
        int lastPageExpected = 0;

        Pageable pageable = PageRequest.of(pageExpected, sizeExpected);

        String urlParameters1 = "?page=" + pageExpected + "&size=" + sizeExpected;
        String urlParameters2 = "?page=" + lastPageExpected + "&size=" + sizeExpected;
        String firstPageLink = EVENT_BASE_PATH + urlParameters1;
        String lastPageLink = EVENT_BASE_PATH + urlParameters2;

        Link pageLink1 = new Link(firstPageLink, "first");
        Link pageLink2 = new Link(firstPageLink, "self");
        Link pageLink3 = new Link(lastPageLink, "next");
        Link pageLink4 = new Link(lastPageLink, "last");

        PageMetadata metadata = new PagedModel.PageMetadata(sizeExpected, numberExpected, totalElementsExpected);
        PagedModel<EventModel> resources = new PagedModel<>(eventModelsListExpected, metadata, pageLink1, pageLink2,
                pageLink3, pageLink4);

        String linkWithParameter = EVENT_BASE_PATH + "/depth/" + "{depth}";

        when(eventService.findAll(pageable, depthWhenProvidedToBigDepth)).thenReturn(eventsExpected);
        when(pagedResourcesAssembler.toModel(eventsExpected, modelAssembler)).thenReturn(resources);

        assertAll(
                () -> mockMvc.perform(get(linkWithParameter, depth).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[1].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[2].href", is(lastPageLink)))
                        .andExpect(jsonPath("links[3].href", is(lastPageLink)))
                        .andExpect(jsonPath("content[0].links[0].href", is(pathToEventLink1)))
                        .andExpect(jsonPath("content[0].links[1].href", is(pathToTargetEventLink1)))
                        .andExpect(jsonPath("content[0].id", is(eventModel1.getId().intValue())))
                        .andExpect(jsonPath("content[0].summary", is(eventModel1.getSummary())))
                        .andExpect(jsonPath("content[0].motive", is(eventModel1.getMotive())))
                        .andExpect(jsonPath("content[0].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventModel1.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("content[0].isSuicidal", is(eventModel1.getIsSuicidal())))
                        .andExpect(jsonPath("content[0].isSuccessful", is(eventModel1.getIsSuccessful())))
                        .andExpect(jsonPath("content[0].isPartOfMultipleIncidents", is(eventModel1.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("content[0].target.links[0].href", is(pathToTargetLink1)))
                        .andExpect(jsonPath("content[0].target.links[1].href").doesNotExist())
                        .andExpect(jsonPath("content[0].target.id", is(targetModel1.getId().intValue())))
                        .andExpect(jsonPath("content[0].target.target", is(targetModel1.getTarget())))
                        .andExpect(jsonPath("content[0].target.countryOfOrigin.links[0].href", is(pathToCountryLink1)))
                        .andExpect(jsonPath("content[0].target.countryOfOrigin.links[1].href").doesNotExist())
                        .andExpect(jsonPath("content[0].target.countryOfOrigin.id", is(countryModel1.getId().intValue())))
                        .andExpect(jsonPath("content[0].target.countryOfOrigin.name", is(countryModel1.getName())))
                        .andExpect(jsonPath("content[0].target.countryOfOrigin.region.links[0].href", is(pathToRegionLink1)))
                        .andExpect(jsonPath("content[0].target.countryOfOrigin.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("content[0].target.countryOfOrigin.region.id", is(regionModel1.getId().intValue())))
                        .andExpect(jsonPath("content[0].target.countryOfOrigin.region.name", is(regionModel1.getName())))
                        .andExpect(jsonPath("content[0].city.links[0].href", is(pathToCityLink1)))
                        .andExpect(jsonPath("content[0].city.links[1].href").doesNotExist())
                        .andExpect(jsonPath("content[0].city.id", is(cityModel1.getId().intValue())))
                        .andExpect(jsonPath("content[0].city.name", is(cityModel1.getName())))
                        .andExpect(jsonPath("content[0].city.latitude", is(cityModel1.getLatitude())))
                        .andExpect(jsonPath("content[0].city.longitude", is(cityModel1.getLongitude())))
                        .andExpect(jsonPath("content[0].city.province.links[0].href", is(pathToProvinceLink1)))
                        .andExpect(jsonPath("content[0].city.province.links[1].href").doesNotExist())
                        .andExpect(jsonPath("content[0].city.province.id", is(provinceModel1.getId().intValue())))
                        .andExpect(jsonPath("content[0].city.province.name", is(provinceModel1.getName())))
                        .andExpect(jsonPath("content[0].city.province.country.links[0].href", is(pathToCountryLink1)))
                        .andExpect(jsonPath("content[0].city.province.country.links[1].href").doesNotExist())
                        .andExpect(jsonPath("content[0].city.province.country.id", is(countryModel1.getId().intValue())))
                        .andExpect(jsonPath("content[0].city.province.country.name", is(countryModel1.getName())))
                        .andExpect(jsonPath("content[0].city.province.country.region.links[0].href", is(pathToRegionLink1)))
                        .andExpect(jsonPath("content[0].city.province.country.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("content[0].city.province.country.region.id", is(regionModel1.getId().intValue())))
                        .andExpect(jsonPath("content[0].city.province.country.region.name", is(regionModel1.getName())))
                        .andExpect(jsonPath("content[0].victim.links[0].href", is(pathToVictimLink1)))
                        .andExpect(jsonPath("content[0].victim.links[1].href").doesNotExist())
                        .andExpect(jsonPath("content[0].victim.id", is(victimModel1.getId().intValue())))
                        .andExpect(jsonPath("content[0].victim.totalNumberOfFatalities",
                                is(victimModel1.getTotalNumberOfFatalities().intValue())))
                        .andExpect(jsonPath("content[0].victim.numberOfPerpetratorsFatalities",
                                is(victimModel1.getNumberOfPerpetratorsFatalities().intValue())))
                        .andExpect(jsonPath("content[0].victim.totalNumberOfInjured",
                                is(victimModel1.getTotalNumberOfInjured().intValue())))
                        .andExpect(jsonPath("content[0].victim.numberOfPerpetratorsInjured",
                                is(victimModel1.getNumberOfPerpetratorsInjured().intValue())))
                        .andExpect(jsonPath("content[0].victim.valueOfPropertyDamage",
                                is(victimModel1.getValueOfPropertyDamage().intValue())))

                        .andExpect(jsonPath("content[1].links[0].href", is(pathToEventLink2)))
                        .andExpect(jsonPath("content[1].links[1].href", is(pathToTargetEventLink2)))
                        .andExpect(jsonPath("content[1].id", is(eventModel2.getId().intValue())))
                        .andExpect(jsonPath("content[1].summary", is(eventModel2.getSummary())))
                        .andExpect(jsonPath("content[1].motive", is(eventModel2.getMotive())))
                        .andExpect(jsonPath("content[1].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventModel2.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("content[1].isSuicidal", is(eventModel2.getIsSuicidal())))
                        .andExpect(jsonPath("content[1].isSuccessful", is(eventModel2.getIsSuccessful())))
                        .andExpect(jsonPath("content[1].isPartOfMultipleIncidents", is(eventModel2.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("content[1].target.links[0].href", is(pathToTargetLink2)))
                        .andExpect(jsonPath("content[1].target.links[1].href").doesNotExist())
                        .andExpect(jsonPath("content[1].target.id", is(targetModel2.getId().intValue())))
                        .andExpect(jsonPath("content[1].target.target", is(targetModel2.getTarget())))
                        .andExpect(jsonPath("content[1].target.countryOfOrigin.links[0].href", is(pathToCountryLink2)))
                        .andExpect(jsonPath("content[1].target.countryOfOrigin.links[1].href").doesNotExist())
                        .andExpect(jsonPath("content[1].target.countryOfOrigin.id", is(countryModel2.getId().intValue())))
                        .andExpect(jsonPath("content[1].target.countryOfOrigin.name", is(countryModel2.getName())))
                        .andExpect(jsonPath("content[1].target.countryOfOrigin.region.links[0].href", is(pathToRegionLink2)))
                        .andExpect(jsonPath("content[1].target.countryOfOrigin.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("content[1].target.countryOfOrigin.region.id", is(regionModel2.getId().intValue())))
                        .andExpect(jsonPath("content[1].target.countryOfOrigin.region.name", is(regionModel2.getName())))
                        .andExpect(jsonPath("content[1].city.links[0].href", is(pathToCityLink2)))
                        .andExpect(jsonPath("content[1].city.links[1].href").doesNotExist())
                        .andExpect(jsonPath("content[1].city.id", is(cityModel2.getId().intValue())))
                        .andExpect(jsonPath("content[1].city.name", is(cityModel2.getName())))
                        .andExpect(jsonPath("content[1].city.latitude", is(cityModel2.getLatitude())))
                        .andExpect(jsonPath("content[1].city.longitude", is(cityModel2.getLongitude())))
                        .andExpect(jsonPath("content[1].city.province.links[0].href", is(pathToProvinceLink2)))
                        .andExpect(jsonPath("content[1].city.province.links[1].href").doesNotExist())
                        .andExpect(jsonPath("content[1].city.province.id", is(provinceModel2.getId().intValue())))
                        .andExpect(jsonPath("content[1].city.province.name", is(provinceModel2.getName())))
                        .andExpect(jsonPath("content[1].city.province.country.links[0].href", is(pathToCountryLink2)))
                        .andExpect(jsonPath("content[1].city.province.country.links[1].href").doesNotExist())
                        .andExpect(jsonPath("content[1].city.province.country.id", is(countryModel2.getId().intValue())))
                        .andExpect(jsonPath("content[1].city.province.country.name", is(countryModel2.getName())))
                        .andExpect(jsonPath("content[1].city.province.country.region.links[0].href", is(pathToRegionLink2)))
                        .andExpect(jsonPath("content[1].city.province.country.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("content[1].city.province.country.region.id", is(regionModel2.getId().intValue())))
                        .andExpect(jsonPath("content[1].city.province.country.region.name", is(regionModel2.getName())))
                        .andExpect(jsonPath("content[1].victim.links[0].href", is(pathToVictimLink2)))
                        .andExpect(jsonPath("content[1].victim.links[1].href").doesNotExist())
                        .andExpect(jsonPath("content[1].victim.id", is(victimModel2.getId().intValue())))
                        .andExpect(jsonPath("content[1].victim.totalNumberOfFatalities",
                                is(victimModel2.getTotalNumberOfFatalities().intValue())))
                        .andExpect(jsonPath("content[1].victim.numberOfPerpetratorsFatalities",
                                is(victimModel2.getNumberOfPerpetratorsFatalities().intValue())))
                        .andExpect(jsonPath("content[1].victim.totalNumberOfInjured",
                                is(victimModel2.getTotalNumberOfInjured().intValue())))
                        .andExpect(jsonPath("content[1].victim.numberOfPerpetratorsInjured",
                                is(victimModel2.getNumberOfPerpetratorsInjured().intValue())))
                        .andExpect(jsonPath("content[1].victim.valueOfPropertyDamage",
                                is(victimModel2.getValueOfPropertyDamage().intValue())))

                        .andExpect(jsonPath("page.size", is(sizeExpected)))
                        .andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
                        .andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
                        .andExpect(jsonPath("page.number", is(numberExpected))),
                () -> verify(eventService, times(1)).findAll(pageable, depthWhenProvidedToBigDepth),
                () -> verifyNoMoreInteractions(eventService),
                () -> verify(pagedResourcesAssembler, times(1)).toModel(eventsExpected, modelAssembler),
                () -> verifyNoMoreInteractions(pagedResourcesAssembler),
                () -> verifyNoInteractions(patchUtil),
                () -> verifyNoInteractions(violationUtil));
    }

    @Test
    void when_find_all_events_with_depth_but_events_not_exist_should_return_empty_list() {

        int depth = 5;

        List<EventNode> eventsListExpected = new ArrayList<>();

        List<EventModel> modelsListExpected = new ArrayList<>();

        Page<EventNode> eventsExpected = new PageImpl<>(eventsListExpected);

        int sizeExpected = 20;
        int totalElementsExpected = 0;
        int totalPagesExpected = 0;
        int numberExpected = 0;
        int pageExpected = 0;
        int lastPageExpected = 0;

        Pageable pageable = PageRequest.of(pageExpected, sizeExpected);

        String urlParameters1 = "?page=" + pageExpected + "&size=" + sizeExpected;
        String urlParameters2 = "?page=" + lastPageExpected + "&size=" + sizeExpected;
        String firstPageLink = EVENT_BASE_PATH + urlParameters1;
        String lastPageLink = EVENT_BASE_PATH + urlParameters2;

        Link pageLink1 = new Link(firstPageLink, "first");
        Link pageLink2 = new Link(firstPageLink, "self");
        Link pageLink3 = new Link(lastPageLink, "next");
        Link pageLink4 = new Link(lastPageLink, "last");

        PageMetadata metadata = new PagedModel.PageMetadata(sizeExpected, numberExpected, totalElementsExpected);
        PagedModel<EventModel> resources = new PagedModel<>(modelsListExpected, metadata, pageLink1, pageLink2,
                pageLink3, pageLink4);

        String linkWithParameter = EVENT_BASE_PATH + "/depth/" + "{depth}";

        when(eventService.findAll(pageable, depth)).thenReturn(eventsExpected);
        when(pagedResourcesAssembler.toModel(eventsExpected, modelAssembler)).thenReturn(resources);

        assertAll(
                () -> mockMvc.perform(get(linkWithParameter, depth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[1].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[2].href", is(lastPageLink)))
                        .andExpect(jsonPath("links[3].href", is(lastPageLink)))
                        .andExpect(jsonPath("content").isEmpty())
                        .andExpect(jsonPath("page.size", is(sizeExpected)))
                        .andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
                        .andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
                        .andExpect(jsonPath("page.number", is(numberExpected))),
                () -> verify(eventService, times(1)).findAll(pageable, depth),
                () -> verifyNoMoreInteractions(eventService),
                () -> verify(pagedResourcesAssembler, times(1)).toModel(eventsExpected, modelAssembler),
                () -> verifyNoMoreInteractions(pagedResourcesAssembler),
                () -> verifyNoInteractions(patchUtil),
                () -> verifyNoInteractions(violationUtil));
    }

    @Test
    void when_find_existing_event_should_return_event() {

        Long eventId = 1L;

        CityNode cityNode = (CityNode) cityBuilder.build(ObjectType.NODE);
        TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
        EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).withCity(cityNode).build(ObjectType.NODE);
        CityModel cityModel = (CityModel) cityBuilder.build(ObjectType.MODEL);
        String pathToCityLink = CITY_BASE_PATH + "/" + counterForUtilMethodsModel;
        cityModel.add(new Link(pathToCityLink));
        TargetModel targetModel = (TargetModel) targetBuilder.build(ObjectType.MODEL);
        String pathToTargetLink = TARGET_BASE_PATH + "/" + targetModel.getId();
        targetModel.add(new Link(pathToTargetLink));
        VictimModel victimModel = (VictimModel) victimBuilder.build(ObjectType.MODEL);
        String pathToVictimLink = VICTIM_BASE_PATH + "/" + targetModel.getId();
        victimModel.add(new Link(pathToVictimLink));

        EventModel eventModel = (EventModel) eventBuilder.withTarget(targetModel).withCity(cityModel).withVictim(victimModel)
                .build(ObjectType.MODEL);
        String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();
        eventModel.add(new Link(pathToEventLink));
        String pathToTargetEventLink = EVENT_BASE_PATH + "/" + eventModel.getId().intValue() + "/targets";
        eventModel.add(new Link(pathToTargetEventLink, "target"));

        String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

        when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
        when(modelAssembler.toModel(eventNode)).thenReturn(eventModel);

        assertAll(
                () -> mockMvc.perform(get(linkWithParameter, eventId)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToEventLink)))
                        .andExpect(jsonPath("links[1].href", is(pathToTargetEventLink)))
                        .andExpect(jsonPath("id", is(eventId.intValue())))
                        .andExpect(jsonPath("summary", is(eventModel.getSummary())))
                        .andExpect(jsonPath("motive", is(eventModel.getMotive())))
                        .andExpect(jsonPath("date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventModel.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("isSuicidal", is(eventModel.getIsSuicidal())))
                        .andExpect(jsonPath("isSuccessful", is(eventModel.getIsSuccessful())))
                        .andExpect(jsonPath("isPartOfMultipleIncidents", is(eventModel.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("target.id", is(targetModel.getId().intValue())))
                        .andExpect(jsonPath("target.target", is(targetModel.getTarget())))
                        .andExpect(jsonPath("target.links[0].href", is(pathToTargetLink)))
                        .andExpect(jsonPath("city.id", is(cityModel.getId().intValue())))
                        .andExpect(jsonPath("city.name", is(cityModel.getName())))
                        .andExpect(jsonPath("city.latitude", is(cityModel.getLatitude())))
                        .andExpect(jsonPath("city.longitude", is(cityModel.getLongitude())))
                        .andExpect(jsonPath("city.links[0].href", is(pathToCityLink)))
                        .andExpect(jsonPath("victim.id", is(victimModel.getId().intValue())))
                        .andExpect(jsonPath("victim.totalNumberOfFatalities",
                                is(victimModel.getTotalNumberOfFatalities().intValue())))
                        .andExpect(jsonPath("victim.numberOfPerpetratorsFatalities",
                                is(victimModel.getNumberOfPerpetratorsFatalities().intValue())))
                        .andExpect(jsonPath("victim.totalNumberOfInjured",
                                is(victimModel.getTotalNumberOfInjured().intValue())))
                        .andExpect(jsonPath("victim.numberOfPerpetratorsInjured",
                                is(victimModel.getNumberOfPerpetratorsInjured().intValue())))
                        .andExpect(jsonPath("victim.valueOfPropertyDamage",
                                is(victimModel.getValueOfPropertyDamage().intValue())))
                        .andExpect(jsonPath("victim.links[0].href", is(pathToVictimLink))),
                () -> verify(eventService, times(1)).findById(eventId),
                () -> verifyNoMoreInteractions(eventService),
                () -> verify(modelAssembler, times(1)).toModel(eventNode),
                () -> verifyNoMoreInteractions(modelAssembler),
                () -> verifyNoInteractions(patchUtil),
                () -> verifyNoInteractions(violationUtil),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    @Test
    void when_find_event_but_event_does_not_exist_should_return_error_response() {

        Long eventId = 1L;

        String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

        when(eventService.findById(eventId)).thenReturn(Optional.empty());

        assertAll(
                () -> mockMvc.perform(get(linkWithParameter, eventId)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(content().json("{'status': 404}"))
                        .andExpect(jsonPath("errors[0]", is("Could not find EventModel with id: " + eventId + ".")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(eventService, times(1)).findById(eventId),
                () -> verifyNoMoreInteractions(eventService),
                () -> verifyNoInteractions(modelAssembler),
                () -> verifyNoInteractions(patchUtil),
                () -> verifyNoInteractions(violationUtil),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    @Test
    void when_find_existing_event_with_negative_depth_should_return_only_event_without_nested_objects() {

        int depth = -5;
        Long eventId = 1L;
        int depthWhenProvidedNegativeDepth = 0;

        EventNode eventNode = (EventNode) eventBuilder.build(ObjectType.NODE);
        EventModel eventModel = (EventModel) eventBuilder.build(ObjectType.MODEL);
        String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();
        eventModel.add(new Link(pathToEventLink));

        String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}" + "/depth/" + "{depth}";

        when(eventService.findById(eventId, depthWhenProvidedNegativeDepth)).thenReturn(Optional.of(eventNode));
        when(modelAssembler.toModel(eventNode)).thenReturn(eventModel);

        assertAll(
                () -> mockMvc.perform(get(linkWithParameter, eventId, depth)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))

                        .andDo(MockMvcResultHandlers.print())

                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToEventLink)))
                        .andExpect(jsonPath("links[1].href").doesNotExist())
                        .andExpect(jsonPath("id", is(eventId.intValue())))
                        .andExpect(jsonPath("summary", is(eventModel.getSummary())))
                        .andExpect(jsonPath("motive", is(eventModel.getMotive())))
                        .andExpect(jsonPath("date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventModel.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("isSuicidal", is(eventModel.getIsSuicidal())))
                        .andExpect(jsonPath("isSuccessful", is(eventModel.getIsSuccessful())))
                        .andExpect(jsonPath("isPartOfMultipleIncidents", is(eventModel.getIsPartOfMultipleIncidents()))),
                () -> verify(eventService, times(1)).findById(eventId, depthWhenProvidedNegativeDepth),
                () -> verifyNoMoreInteractions(eventService),
                () -> verify(modelAssembler, times(1)).toModel(eventNode),
                () -> verifyNoMoreInteractions(modelAssembler),
                () -> verifyNoInteractions(patchUtil),
                () -> verifyNoInteractions(violationUtil),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    @Test
    void when_find_existing_event_with_large_depth_should_return_event_with_all_nested_objects() {

        Long eventId = 1L;
        int depth = 15;
        int depthWhenProvidedToBigDepth = 5;

        RegionNode regionNode = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNode = (CountryNode) countryBuilder.withRegion(regionNode).build(ObjectType.NODE);
        TargetNode targetNode = (TargetNode) targetBuilder.withCountry(countryNode).build(ObjectType.NODE);
        ProvinceNode provinceNode = (ProvinceNode) provinceBuilder.withCountry(countryNode).build(ObjectType.NODE);
        CityNode cityNode = (CityNode) cityBuilder.withProvince(provinceNode).build(ObjectType.NODE);
        VictimNode victimNode = (VictimNode) victimBuilder.build(ObjectType.NODE);
        EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).withCity(cityNode).withVictim(victimNode)
                .build(ObjectType.NODE);

        RegionModel regionModel = (RegionModel) regionBuilder.build(ObjectType.MODEL);
        String pathToRegionLink = REGION_BASE_PATH + "/" + regionModel.getId().intValue();
        regionModel.add(new Link(pathToRegionLink));
        CountryModel countryModel = (CountryModel) countryBuilder.withRegion(regionModel).build(ObjectType.MODEL);
        String pathToCountryLink = COUNTRY_BASE_PATH + "/" + countryNode.getId().intValue();
        countryModel.add(new Link(pathToCountryLink));
        ProvinceModel provinceModel = (ProvinceModel) provinceBuilder.withCountry(countryModel).build(ObjectType.MODEL);
        String pathToProvinceLink = PROVINCE_BASE_PATH + "/" + provinceModel.getId().intValue();
        provinceModel.add(new Link(pathToProvinceLink));
        CityModel cityModel = (CityModel) cityBuilder.withProvince(provinceModel).build(ObjectType.MODEL);
        String pathToCityLink = CITY_BASE_PATH + "/" + counterForUtilMethodsModel;
        cityModel.add(new Link(pathToCityLink));
        TargetModel targetModel = (TargetModel) targetBuilder.withCountry(countryModel).build(ObjectType.MODEL);
        String pathToTargetLink = TARGET_BASE_PATH + "/" + targetModel.getId();
        targetModel.add(new Link(pathToTargetLink));
        VictimModel victimModel = (VictimModel) victimBuilder.build(ObjectType.MODEL);
        String pathToVictimLink = VICTIM_BASE_PATH + "/" + targetModel.getId();
        victimModel.add(new Link(pathToVictimLink));

        EventModel eventModel = (EventModel) eventBuilder.withTarget(targetModel).withCity(cityModel).withVictim(victimModel)
                .build(ObjectType.MODEL);
        String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();
        eventModel.add(new Link(pathToEventLink));
        String pathToTargetEventLink = EVENT_BASE_PATH + "/" + eventModel.getId().intValue() + "/targets";
        eventModel.add(new Link(pathToTargetEventLink, "target"));

        String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}" + "/depth/" + "{depth}";

        when(eventService.findById(eventId, depthWhenProvidedToBigDepth)).thenReturn(Optional.of(eventNode));
        when(modelAssembler.toModel(eventNode)).thenReturn(eventModel);

        assertAll(
                () -> mockMvc.perform(get(linkWithParameter, eventId, depth)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToEventLink)))
                        .andExpect(jsonPath("links[1].href", is(pathToTargetEventLink)))
                        .andExpect(jsonPath("id", is(eventModel.getId().intValue())))
                        .andExpect(jsonPath("summary", is(eventModel.getSummary())))
                        .andExpect(jsonPath("motive", is(eventModel.getMotive())))
                        .andExpect(jsonPath("date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventModel.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("isSuicidal", is(eventModel.getIsSuicidal())))
                        .andExpect(jsonPath("isSuccessful", is(eventModel.getIsSuccessful())))
                        .andExpect(jsonPath("isPartOfMultipleIncidents", is(eventModel.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("target.links[0].href", is(pathToTargetLink)))
                        .andExpect(jsonPath("target.links[1].href").doesNotExist())
                        .andExpect(jsonPath("target.id", is(targetModel.getId().intValue())))
                        .andExpect(jsonPath("target.target", is(targetModel.getTarget())))
                        .andExpect(jsonPath("target.countryOfOrigin.links[0].href", is(pathToCountryLink)))
                        .andExpect(jsonPath("target.countryOfOrigin.links[1].href").doesNotExist())
                        .andExpect(jsonPath("target.countryOfOrigin.id", is(countryModel.getId().intValue())))
                        .andExpect(jsonPath("target.countryOfOrigin.name", is(countryModel.getName())))
                        .andExpect(jsonPath("target.countryOfOrigin.region.links[0].href", is(pathToRegionLink)))
                        .andExpect(jsonPath("target.countryOfOrigin.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("target.countryOfOrigin.region.id", is(regionModel.getId().intValue())))
                        .andExpect(jsonPath("target.countryOfOrigin.region.name", is(regionModel.getName())))
                        .andExpect(jsonPath("city.id", is(cityModel.getId().intValue())))
                        .andExpect(jsonPath("city.name", is(cityModel.getName())))
                        .andExpect(jsonPath("city.latitude", is(cityModel.getLatitude())))
                        .andExpect(jsonPath("city.longitude", is(cityModel.getLongitude())))
                        .andExpect(jsonPath("city.links[0].href", is(pathToCityLink)))
                        .andExpect(jsonPath("city.links[1].href").doesNotExist())
                        .andExpect(jsonPath("city.province.links[0].href", is(pathToProvinceLink)))
                        .andExpect(jsonPath("city.province.links[1].href").doesNotExist())
                        .andExpect(jsonPath("city.province.id", is(provinceModel.getId().intValue())))
                        .andExpect(jsonPath("city.province.name", is(provinceModel.getName())))
                        .andExpect(jsonPath("city.province.country.links[0].href", is(pathToCountryLink)))
                        .andExpect(jsonPath("city.province.country.links[1].href").doesNotExist())
                        .andExpect(jsonPath("city.province.country.id", is(provinceModel.getId().intValue())))
                        .andExpect(jsonPath("city.province.country.name", is(countryModel.getName())))
                        .andExpect(jsonPath("city.province.country.region.links[0].href", is(pathToRegionLink)))
                        .andExpect(jsonPath("city.province.country.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("city.province.country.region.id", is(regionModel.getId().intValue())))
                        .andExpect(jsonPath("city.province.country.region.name", is(regionModel.getName())))
                        .andExpect(jsonPath("victim.links[0].href", is(pathToVictimLink)))
                        .andExpect(jsonPath("victim.links[1].href").doesNotExist())
                        .andExpect(jsonPath("victim.id", is(victimModel.getId().intValue())))
                        .andExpect(jsonPath("victim.totalNumberOfFatalities",
                                is(victimModel.getTotalNumberOfFatalities().intValue())))
                        .andExpect(jsonPath("victim.numberOfPerpetratorsFatalities",
                                is(victimModel.getNumberOfPerpetratorsFatalities().intValue())))
                        .andExpect(jsonPath("victim.totalNumberOfInjured",
                                is(victimModel.getTotalNumberOfInjured().intValue())))
                        .andExpect(jsonPath("victim.numberOfPerpetratorsInjured",
                                is(victimModel.getNumberOfPerpetratorsInjured().intValue())))
                        .andExpect(jsonPath("victim.valueOfPropertyDamage",
                                is(victimModel.getValueOfPropertyDamage().intValue()))),
                () -> verify(eventService, times(1)).findById(eventId, depthWhenProvidedToBigDepth),
                () -> verifyNoMoreInteractions(eventService),
                () -> verify(modelAssembler, times(1)).toModel(eventNode),
                () -> verifyNoMoreInteractions(modelAssembler),
                () -> verifyNoInteractions(patchUtil),
                () -> verifyNoInteractions(violationUtil),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    @Test
    void when_find_event_with_depth_but_event_does_not_exist_should_return_error_response() {

        Long eventId = 1L;
        int depth = 5;

        String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}" + "/depth/" + "{depth}";

        when(eventService.findById(eventId, depth)).thenReturn(Optional.empty());

        assertAll(
                () -> mockMvc.perform(get(linkWithParameter, eventId, depth)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(content().json("{'status': 404}"))
                        .andExpect(jsonPath("errors[0]", is("Could not find EventModel with id: " + eventId + ".")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(eventService, times(1)).findById(eventId, depth),
                () -> verifyNoMoreInteractions(eventService),
                () -> verifyNoInteractions(modelAssembler),
                () -> verifyNoInteractions(patchUtil),
                () -> verifyNoInteractions(violationUtil),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    private Event createEvent(ObjectType type) {

        String summary = "summary";
        String motive = "motive";
        boolean isPartOfMultipleIncidents = true;
        boolean isSuccessful = true;
        boolean isSuicidal = true;

        switch (type) {

            case NODE:

                counterForUtilMethodsNode++;

                CityNode cityNode = (CityNode) cityBuilder
                        .withId((long) counterForUtilMethodsNode)
                        .withName("city" + counterForUtilMethodsNode)
                        .withLatitude((double) (20 + counterForUtilMethodsNode))
                        .withLongitude((double) (40 + counterForUtilMethodsNode))
                        .build(ObjectType.NODE);

                TargetNode targetNode = (TargetNode) targetBuilder.withId((long) counterForUtilMethodsNode)
                        .withTarget("target" + counterForUtilMethodsNode).build(ObjectType.NODE);

                VictimNode victimNode = (VictimNode) victimBuilder.withId((long) counterForUtilMethodsNode)
                        .withTotalNumberOfFatalities(20L + counterForUtilMethodsNode)
                        .withNumberOfPerpetratorsFatalities(11L + counterForUtilMethodsNode)
                        .withTotalNumberOfInjured(21L + counterForUtilMethodsNode)
                        .withNumberOfPerpetratorsInjured(10L + counterForUtilMethodsNode)
                        .withValueOfPropertyDamage(1000L + counterForUtilMethodsNode)
                        .build(ObjectType.NODE);

                return eventBuilder.withId((long) counterForUtilMethodsNode).withSummary(summary + counterForUtilMethodsNode)
                        .withMotive(motive + counterForUtilMethodsNode).withIsPartOfMultipleIncidents(isPartOfMultipleIncidents)
                        .withIsSuccessful(isSuccessful).withIsSuicidal(isSuicidal).withTarget(targetNode).withCity(cityNode)
                        .withVictim(victimNode).build(ObjectType.NODE);

            case MODEL:

                counterForUtilMethodsModel++;

                CityModel cityModel = (CityModel) cityBuilder
                        .withId((long) counterForUtilMethodsModel)
                        .withName("city" + counterForUtilMethodsModel)
                        .withLatitude((double) (20 + counterForUtilMethodsModel))
                        .withLongitude((double) (40 + counterForUtilMethodsModel))
                        .build(ObjectType.MODEL);
                String pathToCityLink = CITY_BASE_PATH + "/" + counterForUtilMethodsModel;
                cityModel.add(new Link(pathToCityLink));

                TargetModel targetModel = (TargetModel) targetBuilder.withId((long) counterForUtilMethodsModel)
                        .withTarget("target" + counterForUtilMethodsModel)
                        .build(ObjectType.MODEL);

                String pathToTargetLink = TARGET_BASE_PATH + "/" + counterForUtilMethodsModel;
                targetModel.add(new Link(pathToTargetLink));

                VictimModel victimModel = (VictimModel) victimBuilder.withId((long) counterForUtilMethodsModel)
                        .withTotalNumberOfFatalities(20L + counterForUtilMethodsModel)
                        .withNumberOfPerpetratorsFatalities(11L + counterForUtilMethodsModel)
                        .withTotalNumberOfInjured(21L + counterForUtilMethodsModel)
                        .withNumberOfPerpetratorsInjured(10L + counterForUtilMethodsModel)
                        .withValueOfPropertyDamage(1000L + counterForUtilMethodsModel)
                        .build(ObjectType.MODEL);

                String pathToVictimLink = VICTIM_BASE_PATH + "/" + counterForUtilMethodsModel;
                victimModel.add(new Link(pathToVictimLink));

                EventModel eventModel = (EventModel) eventBuilder.withId((long) counterForUtilMethodsModel)
                        .withSummary(summary + counterForUtilMethodsModel)
                        .withMotive(motive + counterForUtilMethodsModel)
                        .withIsPartOfMultipleIncidents(isPartOfMultipleIncidents)
                        .withIsSuccessful(isSuccessful)
                        .withIsSuicidal(isSuicidal)
                        .withTarget(targetModel)
                        .withCity(cityModel)
                        .withVictim(victimModel)
                        .build(ObjectType.MODEL);
                String pathToEventLink = EVENT_BASE_PATH + "/" + counterForUtilMethodsModel;
                eventModel.add(new Link(pathToEventLink, "self"));
                String pathToTargetEventLink = EVENT_BASE_PATH + "/" + eventModel.getId().intValue() + "/targets";
                eventModel.add(new Link(pathToTargetEventLink, "target"));

                return eventModel;

            default:
                throw new RuntimeException("Invalid type");
        }
    }
}
