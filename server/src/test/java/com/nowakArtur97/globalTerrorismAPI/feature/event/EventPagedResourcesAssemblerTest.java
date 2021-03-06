package com.nowakArtur97.globalTerrorismAPI.feature.event;

import com.nowakArtur97.globalTerrorismAPI.feature.city.CityModelAssembler;
import com.nowakArtur97.globalTerrorismAPI.feature.target.TargetModelAssembler;
import com.nowakArtur97.globalTerrorismAPI.feature.victim.VictimModelAssembler;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.EventBuilder;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.nowakArtur97.globalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("PagedResourcesAssembler_Tests")
class EventPagedResourcesAssemblerTest {

    private final String BASE_URL = "http://localhost";

    private PagedResourcesAssembler<EventNode> pagedResourcesAssembler;

    private HateoasPageableHandlerMethodArgumentResolver resolver;

    private EventModelAssembler eventModelAssembler;

    @Mock
    private TargetModelAssembler targetModelAssembler;

    @Mock
    private CityModelAssembler cityModelAssembler;

    @Mock
    private VictimModelAssembler victimModelAssembler;

    @Mock
    private ModelMapper modelMapper;

    @BeforeAll
    private static void init() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @BeforeEach
    private void setUp() {

        resolver = new HateoasPageableHandlerMethodArgumentResolver();

        eventModelAssembler = new EventModelAssembler(targetModelAssembler, cityModelAssembler, victimModelAssembler, modelMapper);

        pagedResourcesAssembler = new PagedResourcesAssembler<>(resolver, null);
    }

    @Test
    void when_map_event_node_page_to_paged_model_without_events_should_return_empty_page() {

        int page = 0;
        int size = 1;
        int listSize = 0;

        List<EventNode> eventsListExpected = createEventNodesList(listSize);

        Page<EventNode> eventsPage = createPageOfEventNodes(eventsListExpected, page, size);

        PagedModel<EventModel> eventsPagedModel = pagedResourcesAssembler.toModel(eventsPage, eventModelAssembler);

        String selfLink = getLink(page, size);

        assertAll(
                () -> assertTrue(eventsPagedModel.hasLinks(),
                        () -> "should have links, but haven`t: " + eventsPagedModel),
                () -> assertTrue(eventsPagedModel.getLink("first").isEmpty(),
                        () -> "should not have first link, but had: "
                                + eventsPagedModel.getLink("first").get().getHref()),
                () -> assertTrue(eventsPagedModel.getLink("self").isPresent(),
                        () -> "should have self link, but haven`t: " + eventsPagedModel),
                () -> assertEquals(eventsPagedModel.getLink("self").get().getHref(), selfLink, () -> "should have self link with url: " + selfLink + ", but had: "
                        + eventsPagedModel.getLink("self").get().getHref()),
                () -> assertTrue(eventsPagedModel.getNextLink().isEmpty(),
                        () -> "should not have next link, but had: " + eventsPagedModel.getNextLink().get().getHref()),
                () -> assertTrue(eventsPagedModel.getPreviousLink().isEmpty(),
                        () -> "should not have previous link, but had: "
                                + eventsPagedModel.getPreviousLink().get().getHref()),
                () -> assertTrue(eventsPagedModel.getLink("last").isEmpty(),
                        () -> "should not have last link, but had: "
                                + eventsPagedModel.getLink("last").get().getHref()));
    }

    @Test
    void when_map_event_node_page_to_paged_model_on_first_page_should_return_paged_model_with_links() {

        int page = 0;
        int size = 1;
        int listSize = 3;

        List<EventNode> eventsListExpected = createEventNodesList(listSize);

        Page<EventNode> eventsPage = createPageOfEventNodes(eventsListExpected, page, size);

        PagedModel<EventModel> eventsPagedModel = pagedResourcesAssembler.toModel(eventsPage, eventModelAssembler);

        String firstLink = getLink(0, size);
        String selfLink = getLink(page, size);
        String nextLink = getLink(page + 1, size);
        String lastLink = getLink(listSize / size - 1, size);

        assertAll(
                () -> assertTrue(eventsPagedModel.hasLinks(),
                        () -> "should have links, but haven`t: " + eventsPagedModel),
                () -> assertTrue(eventsPagedModel.getLink("first").isPresent(),
                        () -> "should have first link, but haven`t: " + eventsPagedModel),
                () -> assertEquals(eventsPagedModel.getLink("first").get().getHref(), firstLink, () -> "should have first link with url: " + firstLink + ", but had: "
                        + eventsPagedModel.getLink("first").get().getHref()),
                () -> assertTrue(eventsPagedModel.getLink("self").isPresent(),
                        () -> "should have self link, but haven`t: " + eventsPagedModel),
                () -> assertEquals(eventsPagedModel.getLink("self").get().getHref(), selfLink, () -> "should have self link with url: " + selfLink + ", but had: "
                        + eventsPagedModel.getLink("self").get().getHref()),
                () -> assertTrue(eventsPagedModel.getNextLink().isPresent(),
                        () -> "should have next link, but haven`t: " + eventsPagedModel),
                () -> assertEquals(eventsPagedModel.getNextLink().get().getHref(), nextLink, () -> "should have next link with url:" + nextLink + ", but had: "
                        + eventsPagedModel.getNextLink().get().getHref()),
                () -> assertTrue(eventsPagedModel.getPreviousLink().isEmpty(),
                        () -> "should not have previous link, but had: "
                                + eventsPagedModel.getPreviousLink().get().getHref()),
                () -> assertTrue(eventsPagedModel.getLink("last").isPresent(),
                        () -> "should have last link, but haven`t: " + eventsPagedModel),
                () -> assertEquals(eventsPagedModel.getLink("last").get().getHref(), lastLink, () -> "should have last link with url: " + lastLink + ", but had: "
                        + eventsPagedModel.getLink("last").get().getHref()));
    }

    @Test
    void when_map_event_node_page_to_paged_model_on_last_page_should_return_paged_model_with_links() {

        int page = 2;
        int size = 1;
        int listSize = 3;

        List<EventNode> eventsListExpected = createEventNodesList(listSize);

        Page<EventNode> eventsPage = createPageOfEventNodes(eventsListExpected, page, size);

        PagedModel<EventModel> eventsPagedModel = pagedResourcesAssembler.toModel(eventsPage, eventModelAssembler);

        String firstLink = getLink(0, size);
        String selfLink = getLink(page, size);
        String previousLink = getLink(page - 1, size);
        String lastLink = getLink(listSize / size - 1, size);

        assertAll(
                () -> assertTrue(eventsPagedModel.hasLinks(),
                        () -> "should have links, but haven`t: " + eventsPagedModel),
                () -> assertTrue(eventsPagedModel.getLink("first").isPresent(),
                        () -> "should have first link, but haven`t: " + eventsPagedModel),
                () -> assertEquals(eventsPagedModel.getLink("first").get().getHref(), firstLink, () -> "should have first link with url: " + firstLink + ", but had: "
                        + eventsPagedModel.getLink("first").get().getHref()),
                () -> assertTrue(eventsPagedModel.getLink("self").isPresent(),
                        () -> "should have self link, but haven`t: " + eventsPagedModel),
                () -> assertEquals(eventsPagedModel.getLink("self").get().getHref(), selfLink, () -> "should have self link with url: " + selfLink + ", but had: "
                        + eventsPagedModel.getLink("self").get().getHref()),
                () -> assertTrue(eventsPagedModel.getNextLink().isEmpty(),
                        () -> "should not have next link, but had: " + eventsPagedModel.getNextLink().get().getHref()),
                () -> assertTrue(eventsPagedModel.getPreviousLink().isPresent(),
                        () -> "should have previous link, but haven`t: " + eventsPagedModel),
                () -> assertEquals(eventsPagedModel.getPreviousLink().get().getHref(), previousLink, () -> "should have previous link with url:" + previousLink + ", but had: "
                        + eventsPagedModel.getPreviousLink().get().getHref()),
                () -> assertTrue(eventsPagedModel.getLink("last").isPresent(),
                        () -> "should have last link, but haven`t: " + eventsPagedModel),
                () -> assertEquals(eventsPagedModel.getLink("last").get().getHref(), lastLink, () -> "should have last link with url: " + lastLink + ", but had: "
                        + eventsPagedModel.getLink("last").get().getHref()));
    }

    @Test
    void when_map_event_node_page_to_paged_model_with_previous_and_next_link_should_return_paged_model_with_links() {

        int page = 1;
        int size = 1;
        int listSize = 3;

        List<EventNode> eventsListExpected = createEventNodesList(listSize);

        Page<EventNode> eventsPage = createPageOfEventNodes(eventsListExpected, page, size);

        PagedModel<EventModel> eventsPagedModel = pagedResourcesAssembler.toModel(eventsPage, eventModelAssembler);

        String firstLink = getLink(0, size);
        String selfLink = getLink(page, size);
        String previousLink = getLink(page - 1, size);
        String nextLink = getLink(page + 1, size);
        String lastLink = getLink(listSize / size - 1, size);

        assertAll(
                () -> assertTrue(eventsPagedModel.hasLinks(),
                        () -> "should have links, but haven`t: " + eventsPagedModel),
                () -> assertTrue(eventsPagedModel.getLink("first").isPresent(),
                        () -> "should have first link, but haven`t: " + eventsPagedModel),
                () -> assertEquals(eventsPagedModel.getLink("first").get().getHref(), firstLink, () -> "should have first link with url: " + firstLink + ", but had: "
                        + eventsPagedModel.getLink("first").get().getHref()),
                () -> assertTrue(eventsPagedModel.getLink("self").isPresent(),
                        () -> "should have self link, but haven`t: " + eventsPagedModel),
                () -> assertEquals(eventsPagedModel.getLink("self").get().getHref(), selfLink, () -> "should have self link with url: " + selfLink + ", but had: "
                        + eventsPagedModel.getLink("self").get().getHref()),
                () -> assertTrue(eventsPagedModel.getNextLink().isPresent(),
                        () -> "should have next link, but haven`t: " + eventsPagedModel),
                () -> assertEquals(eventsPagedModel.getNextLink().get().getHref(), nextLink, () -> "should have next link with url:" + nextLink + ", but had: "
                        + eventsPagedModel.getNextLink().get().getHref()),
                () -> assertTrue(eventsPagedModel.getPreviousLink().isPresent(),
                        () -> "should have previous link, but haven`t: " + eventsPagedModel),
                () -> assertEquals(eventsPagedModel.getPreviousLink().get().getHref(), previousLink, () -> "should have previous link with url:" + previousLink + ", but had: "
                        + eventsPagedModel.getPreviousLink().get().getHref()),
                () -> assertTrue(eventsPagedModel.getLink("last").isPresent(),
                        () -> "should have last link, but haven`t: " + eventsPagedModel),
                () -> assertEquals(eventsPagedModel.getLink("last").get().getHref(), lastLink, () -> "should have last link with url: " + lastLink + ", but had: "
                        + eventsPagedModel.getLink("last").get().getHref()));
    }

    private String getLink(int page, int size) {

        return new StringBuilder(BASE_URL).append("?page=").append(page).append("&size=").append(size).toString();
    }

    private List<EventNode> createEventNodesList(int listSize) {

        EventBuilder eventBuilder = new EventBuilder();

        List<EventNode> eventsListExpected = new ArrayList<>();

        int count = 0;

        while (count < listSize) {

            EventNode eventNode = (EventNode) eventBuilder.build(ObjectType.NODE);

            when(modelMapper.map(eventNode, EventModel.class)).thenReturn((EventModel) eventBuilder.build(ObjectType.MODEL));

            eventsListExpected.add(eventNode);

            count++;
        }

        return eventsListExpected;
    }

    private Page<EventNode> createPageOfEventNodes(List<EventNode> eventsListExpected, int page, int size) {

        Pageable pageRequest = PageRequest.of(page, size);

        return new PageImpl<>(eventsListExpected, pageRequest, eventsListExpected.size());
    }
}
