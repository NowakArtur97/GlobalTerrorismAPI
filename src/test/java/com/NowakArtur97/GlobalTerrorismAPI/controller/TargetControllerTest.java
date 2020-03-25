package com.NowakArtur97.GlobalTerrorismAPI.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.NowakArtur97.GlobalTerrorismAPI.assembler.TargetModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.model.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.TargetService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Target Controller Tests")
@Tag("TargetController_Tests")
public class TargetControllerTest {

	private final String BASE_PATH = "http://localhost:8080/api/targets";

	private MockMvc mockMvc;

	private TargetController targetController;

	@Mock
	private TargetService targetService;

	@Mock
	private TargetModelAssembler targetModelAssembler;

	@Mock
	private PagedResourcesAssembler<TargetNode> pagedResourcesAssembler;

	@BeforeEach
	public void setUp() {

		targetController = new TargetController(targetService, targetModelAssembler, pagedResourcesAssembler);
		mockMvc = MockMvcBuilders.standaloneSetup(targetController)
				.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()).build();
	}

	@Test
	@DisplayName("when find all targets with default parameters in link and targets exist return all targets")
	public void when_find_all_targets_with_default_parameters_in_link_and_targets_exist_should_return_all_targets() {

		Long targetId1 = 1L;
		String targetName1 = "target1";
		TargetNode targetNode1 = new TargetNode(targetId1, targetName1);
		TargetModel targetModel1 = new TargetModel(targetId1, targetName1);

		String pathToLink1 = BASE_PATH + targetId1.intValue();
		Link link1 = new Link(pathToLink1);
		targetModel1.add(link1);

		Long targetId2 = 2L;
		String targetName2 = "target2";
		TargetNode targetNode2 = new TargetNode(targetId2, targetName2);
		TargetModel targetModel2 = new TargetModel(targetId2, targetName2);

		String pathToLink2 = BASE_PATH + targetId2.intValue();
		Link link2 = new Link(pathToLink2);
		targetModel2.add(link2);

		Long targetId3 = 3L;
		String targetName3 = "target3";
		TargetNode targetNode3 = new TargetNode(targetId3, targetName3);
		TargetModel targetModel3 = new TargetModel(targetId3, targetName3);

		String pathToLink3 = BASE_PATH + targetId3.intValue();
		Link link3 = new Link(pathToLink3);
		targetModel3.add(link3);

		Long targetId4 = 4L;
		String targetName4 = "target4";
		TargetNode targetNode4 = new TargetNode(targetId4, targetName4);
		TargetModel targetModel4 = new TargetModel(targetId4, targetName4);

		String pathToLink4 = BASE_PATH + targetId4.intValue();
		Link link4 = new Link(pathToLink4);
		targetModel4.add(link4);

		List<TargetNode> targetsListExpected = new ArrayList<>();
		targetsListExpected.add(targetNode1);
		targetsListExpected.add(targetNode2);
		targetsListExpected.add(targetNode3);
		targetsListExpected.add(targetNode4);

		List<TargetModel> targetModelsListExpected = new ArrayList<>();
		targetModelsListExpected.add(targetModel1);
		targetModelsListExpected.add(targetModel2);
		targetModelsListExpected.add(targetModel3);
		targetModelsListExpected.add(targetModel4);

		Page<TargetNode> targetsExpected = new PageImpl<>(targetsListExpected);

		int sizeExpected = 100;
		int totalElementsExpected = 4;
		int totalPagesExpected = 1;
		int numberExpected = 0;
		int pageExpected = 0;
		int lastPageExpected = 0;

		Pageable pageable = PageRequest.of(pageExpected, sizeExpected);

		String urlParameters1 = "?page=" + pageExpected + "&size=" + sizeExpected;
		String urlParameters2 = "?page=" + lastPageExpected + "&size=" + sizeExpected;
		String firstPageLink = BASE_PATH + urlParameters1;
		String lastPageLink = BASE_PATH + urlParameters2;

		Link pageLink1 = new Link(firstPageLink, "first");
		Link pageLink2 = new Link(firstPageLink, "self");
		Link pageLink3 = new Link(lastPageLink, "next");
		Link pageLink4 = new Link(lastPageLink, "last");

		PageMetadata metadata = new PagedModel.PageMetadata(sizeExpected, numberExpected, totalElementsExpected);
		PagedModel<TargetModel> resources = new PagedModel<>(targetModelsListExpected, metadata, pageLink1, pageLink2,
				pageLink3, pageLink4);

		when(targetService.findAll(pageable)).thenReturn(targetsExpected);
		when(pagedResourcesAssembler.toModel(targetsExpected, targetModelAssembler)).thenReturn(resources);

		assertAll(
				() -> mockMvc.perform(get(firstPageLink)).andExpect(status().isOk())
						.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("links[0].href", is(firstPageLink)))
						.andExpect(jsonPath("links[1].href", is(firstPageLink)))
						.andExpect(jsonPath("links[2].href", is(lastPageLink)))
						.andExpect(jsonPath("links[3].href", is(lastPageLink)))
						.andExpect(jsonPath("content[0].id", is(targetId1.intValue())))
						.andExpect(jsonPath("content[0].target", is(targetName1)))
						.andExpect(jsonPath("content[0].links[0].href", is(link1.getHref())))
						.andExpect(jsonPath("content[1].id", is(targetId2.intValue())))
						.andExpect(jsonPath("content[1].target", is(targetName2)))
						.andExpect(jsonPath("content[1].links[0].href", is(link2.getHref())))
						.andExpect(jsonPath("content[2].id", is(targetId3.intValue())))
						.andExpect(jsonPath("content[2].target", is(targetName3)))
						.andExpect(jsonPath("content[2].links[0].href", is(link3.getHref())))
						.andExpect(jsonPath("page.size", is(sizeExpected)))
						.andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
						.andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
						.andExpect(jsonPath("page.number", is(numberExpected))),
				() -> verify(targetService, times(1)).findAll(pageable));
	}

	@Test
	@DisplayName("when find all targets with changed parameters in link and targets exist return all targets")
	public void when_find_all_targets_with_changed_parameters_in_link_and_targets_exist_should_return_all_targets() {

		Long targetId1 = 1L;
		String targetName1 = "target1";
		TargetNode targetNode1 = new TargetNode(targetId1, targetName1);
		TargetModel targetModel1 = new TargetModel(targetId1, targetName1);

		String pathToLink1 = BASE_PATH + targetId1.intValue();
		Link link1 = new Link(pathToLink1);
		targetModel1.add(link1);

		Long targetId2 = 2L;
		String targetName2 = "target2";
		TargetNode targetNode2 = new TargetNode(targetId2, targetName2);
		TargetModel targetModel2 = new TargetModel(targetId2, targetName2);

		String pathToLink2 = BASE_PATH + targetId2.intValue();
		Link link2 = new Link(pathToLink2);
		targetModel2.add(link2);

		Long targetId3 = 3L;
		String targetName3 = "target3";
		TargetNode targetNode3 = new TargetNode(targetId3, targetName3);
		TargetModel targetModel3 = new TargetModel(targetId3, targetName3);

		String pathToLink3 = BASE_PATH + targetId3.intValue();
		Link link3 = new Link(pathToLink3);
		targetModel3.add(link3);

		Long targetId4 = 4L;
		String targetName4 = "target4";
		TargetNode targetNode4 = new TargetNode(targetId4, targetName4);
		TargetModel targetModel4 = new TargetModel(targetId4, targetName4);

		String pathToLink4 = BASE_PATH + targetId4.intValue();
		Link link4 = new Link(pathToLink4);
		targetModel4.add(link4);

		List<TargetNode> targetsListExpected = new ArrayList<>();
		targetsListExpected.add(targetNode1);
		targetsListExpected.add(targetNode2);
		targetsListExpected.add(targetNode3);
		targetsListExpected.add(targetNode4);

		List<TargetModel> targetModelsListExpected = new ArrayList<>();
		targetModelsListExpected.add(targetModel1);
		targetModelsListExpected.add(targetModel2);
		targetModelsListExpected.add(targetModel3);
		targetModelsListExpected.add(targetModel4);

		Page<TargetNode> targetsExpected = new PageImpl<>(targetsListExpected);

		int sizeExpected = 3;
		int totalElementsExpected = 4;
		int totalPagesExpected = 2;
		int numberExpected = 0;
		int pageExpected = 0;
		int lastPageExpected = 1;

		Pageable pageable = PageRequest.of(pageExpected, sizeExpected);

		String urlParameters1 = "?page=" + pageExpected + "&size=" + sizeExpected;
		String urlParameters2 = "?page=" + lastPageExpected + "&size=" + sizeExpected;
		String firstPageLink = BASE_PATH + urlParameters1;
		String lastPageLink = BASE_PATH + urlParameters2;

		Link pageLink1 = new Link(firstPageLink, "first");
		Link pageLink2 = new Link(firstPageLink, "self");
		Link pageLink3 = new Link(lastPageLink, "next");
		Link pageLink4 = new Link(lastPageLink, "last");

		PageMetadata metadata = new PagedModel.PageMetadata(sizeExpected, numberExpected, totalElementsExpected);
		PagedModel<TargetModel> resources = new PagedModel<>(targetModelsListExpected, metadata, pageLink1, pageLink2,
				pageLink3, pageLink4);

		when(targetService.findAll(pageable)).thenReturn(targetsExpected);
		when(pagedResourcesAssembler.toModel(targetsExpected, targetModelAssembler)).thenReturn(resources);

		assertAll(
				() -> mockMvc.perform(get(firstPageLink)).andExpect(status().isOk())
						.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("links[0].href", is(firstPageLink)))
						.andExpect(jsonPath("links[1].href", is(firstPageLink)))
						.andExpect(jsonPath("links[2].href", is(lastPageLink)))
						.andExpect(jsonPath("links[3].href", is(lastPageLink)))
						.andExpect(jsonPath("content[0].id", is(targetId1.intValue())))
						.andExpect(jsonPath("content[0].target", is(targetName1)))
						.andExpect(jsonPath("content[0].links[0].href", is(link1.getHref())))
						.andExpect(jsonPath("content[1].id", is(targetId2.intValue())))
						.andExpect(jsonPath("content[1].target", is(targetName2)))
						.andExpect(jsonPath("content[1].links[0].href", is(link2.getHref())))
						.andExpect(jsonPath("content[2].id", is(targetId3.intValue())))
						.andExpect(jsonPath("content[2].target", is(targetName3)))
						.andExpect(jsonPath("content[2].links[0].href", is(link3.getHref())))
						.andExpect(jsonPath("page.size", is(sizeExpected)))
						.andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
						.andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
						.andExpect(jsonPath("page.number", is(numberExpected))),
				() -> verify(targetService, times(1)).findAll(pageable));
	}

	@Test
	@DisplayName("when find all targets, but targets not exist")
	public void when_find_all_targets_but_targets_not_exist_should_return_empty_list() {

		List<TargetNode> targetsListExpected = new ArrayList<>();

		List<TargetModel> targetModelsListExpected = new ArrayList<>();

		Page<TargetNode> targetsExpected = new PageImpl<>(targetsListExpected);

		int sizeExpected = 100;
		int totalElementsExpected = 0;
		int totalPagesExpected = 0;
		int numberExpected = 0;
		int pageExpected = 0;
		int lastPageExpected = 0;

		Pageable pageable = PageRequest.of(pageExpected, sizeExpected);

		String urlParameters1 = "?page=" + pageExpected + "&size=" + sizeExpected;
		String urlParameters2 = "?page=" + lastPageExpected + "&size=" + sizeExpected;
		String firstPageLink = BASE_PATH + urlParameters1;
		String lastPageLink = BASE_PATH + urlParameters2;

		Link pageLink1 = new Link(firstPageLink, "first");
		Link pageLink2 = new Link(firstPageLink, "self");
		Link pageLink3 = new Link(lastPageLink, "next");
		Link pageLink4 = new Link(lastPageLink, "last");

		PageMetadata metadata = new PagedModel.PageMetadata(sizeExpected, numberExpected, totalElementsExpected);
		PagedModel<TargetModel> resources = new PagedModel<>(targetModelsListExpected, metadata, pageLink1, pageLink2,
				pageLink3, pageLink4);

		when(targetService.findAll(pageable)).thenReturn(targetsExpected);
		when(pagedResourcesAssembler.toModel(targetsExpected, targetModelAssembler)).thenReturn(resources);

		assertAll(
				() -> mockMvc.perform(get(firstPageLink)).andExpect(status().isOk())
						.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("links[0].href", is(firstPageLink)))
						.andExpect(jsonPath("links[1].href", is(firstPageLink)))
						.andExpect(jsonPath("links[2].href", is(lastPageLink)))
						.andExpect(jsonPath("links[3].href", is(lastPageLink))).andExpect(jsonPath("content").isEmpty())
						.andExpect(jsonPath("page.size", is(sizeExpected)))
						.andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
						.andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
						.andExpect(jsonPath("page.number", is(numberExpected))),
				() -> verify(targetService, times(1)).findAll(pageable));
	}

	@Test
	@DisplayName("when find target and target exists")
	public void when_find_target_and_target_exists_should_return_target() {

		Long targetId = 1L;
		String targetName = "target";
		TargetNode targetNode = new TargetNode(targetId, targetName);
		TargetModel targetModel = new TargetModel(targetId, targetName);

		String pathToLink = BASE_PATH + "/" + targetId.intValue();
		Link link = new Link(pathToLink);
		targetModel.add(link);

		String linkWithParameter = BASE_PATH + "/" + "{id}";
		String linkExpected = BASE_PATH + "/" + targetId;

		when(targetService.findById(targetId)).thenReturn(Optional.of(targetNode));
		when(targetModelAssembler.toModel(targetNode)).thenReturn(targetModel);

		assertAll(() -> mockMvc.perform(get(linkWithParameter, targetId)).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("links[0].href", is(linkExpected)))
				.andExpect(jsonPath("id", is(targetId.intValue()))).andExpect(jsonPath("target", is(targetName))),
				() -> verify(targetService, times(1)).findById(targetId));
	}
}