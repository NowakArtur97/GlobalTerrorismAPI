package com.nowakArtur97.globalTerrorismAPI.feature.group;

import com.nowakArtur97.globalTerrorismAPI.advice.GenericRestControllerAdvice;
import com.nowakArtur97.globalTerrorismAPI.common.controller.GenericRestController;
import com.nowakArtur97.globalTerrorismAPI.common.service.GenericService;
import com.nowakArtur97.globalTerrorismAPI.common.util.PatchUtil;
import com.nowakArtur97.globalTerrorismAPI.common.util.ViolationUtil;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.GroupBuilder;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.nowakArtur97.globalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("GroupController_Tests")
class GroupControllerDeleteMethodTest {

    private final String GROUP_BASE_PATH = "http://localhost:8080/api/v1/groups";

    private MockMvc mockMvc;
    
    @Mock
    private GenericService<GroupNode, GroupDTO> groupService;

    @Mock
    private RepresentationModelAssemblerSupport<GroupNode, GroupModel> modelAssembler;

    @Mock
    private PagedResourcesAssembler<GroupNode> pagedResourcesAssembler;

    @Mock
    private PatchUtil patchUtil;

    @Mock
    private ViolationUtil<GroupNode, GroupDTO> violationUtil;

    private static GroupBuilder groupBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        groupBuilder = new GroupBuilder();
    }

    @BeforeEach
    private void setUp() {

        GenericRestController<GroupModel, GroupDTO> groupController
                = new GroupController(groupService, modelAssembler, pagedResourcesAssembler,
                patchUtil, violationUtil);

        mockMvc = MockMvcBuilders.standaloneSetup(groupController).setControllerAdvice(new GenericRestControllerAdvice())
                .build();
    }

    @Test
    void when_delete_existing_group_should_not_return_content() {

        Long groupId = 1L;

        GroupNode groupNode = (GroupNode) groupBuilder.build(ObjectType.NODE);

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

        when(groupService.delete(groupId)).thenReturn(Optional.of(groupNode));

        assertAll(
                () -> mockMvc.perform(delete(linkWithParameter, groupId))
                        .andExpect(status().isNoContent())
                        .andExpect(jsonPath("$").doesNotExist()),
                () -> verify(groupService, times(1)).delete(groupId),
                () -> verifyNoMoreInteractions(groupService),
                () -> verifyNoInteractions(modelAssembler),
                () -> verifyNoInteractions(patchUtil),
                () -> verifyNoInteractions(violationUtil),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    @Test
    void when_delete_group_but_group_does_not_exist_should_return_error_response() {

        Long groupId = 1L;

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

        when(groupService.delete(groupId)).thenReturn(Optional.empty());

        assertAll(
                () -> mockMvc.perform(delete(linkWithParameter, groupId))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty()).andExpect(content().json("{'status': 404}"))
                        .andExpect(jsonPath("errors[0]", is("Could not find GroupModel with id: " + groupId + ".")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(groupService, times(1)).delete(groupId),
                () -> verifyNoMoreInteractions(groupService),
                () -> verifyNoInteractions(modelAssembler),
                () -> verifyNoInteractions(patchUtil),
                () -> verifyNoInteractions(violationUtil),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }
}
