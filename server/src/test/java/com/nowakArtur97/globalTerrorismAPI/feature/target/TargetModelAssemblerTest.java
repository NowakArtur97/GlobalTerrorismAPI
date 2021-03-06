package com.nowakArtur97.globalTerrorismAPI.feature.target;

import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryModel;
import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryModelAssembler;
import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryNode;
import com.nowakArtur97.globalTerrorismAPI.feature.region.RegionModel;
import com.nowakArtur97.globalTerrorismAPI.feature.region.RegionNode;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.CountryBuilder;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.RegionBuilder;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.TargetBuilder;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.nowakArtur97.globalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("TargetModelAssembler_Tests")
@DisabledOnOs(OS.LINUX)
class TargetModelAssemblerTest {

    private final String COUNTRY_BASE_PATH = "http://localhost/api/v1/countries";
    private final String TARGET_BASE_PATH = "http://localhost/api/v1/targets";

    private TargetModelAssembler targetModelAssembler;

    private static RegionBuilder regionBuilder;
    private static CountryBuilder countryBuilder;
    private static TargetBuilder targetBuilder;

    @Mock
    private CountryModelAssembler countryModelAssembler;

    @Mock
    private ModelMapper modelMapper;

    @BeforeAll
    private static void setUpBuilders() {

        regionBuilder = new RegionBuilder();
        countryBuilder = new CountryBuilder();
        targetBuilder = new TargetBuilder();
    }

    @BeforeEach
    private void setUp() {

        targetModelAssembler = new TargetModelAssembler(countryModelAssembler, modelMapper);
    }

    @Test
    void when_map_target_node_to_model_should_return_target_model() {

        Long countryId = 1L;
        Long targetId = 2L;

        RegionNode regionNode = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNode = (CountryNode) countryBuilder.withId(countryId).withRegion(regionNode).build(ObjectType.NODE);
        TargetNode targetNode = (TargetNode) targetBuilder.withId(targetId).withCountry(countryNode).build(ObjectType.NODE);

        RegionModel regionModel = (RegionModel) regionBuilder.build(ObjectType.MODEL);
        CountryModel countryModel = (CountryModel) countryBuilder.withId(countryId).withRegion(regionModel).build(ObjectType.MODEL);
        String pathToCountryLink = COUNTRY_BASE_PATH + "/" + countryId.intValue();
        countryModel.add(new Link(pathToCountryLink));

        TargetModel targetModel = (TargetModel) targetBuilder.withId(targetId).withCountry(countryModel).build(ObjectType.MODEL);
        String pathToTargetLink = TARGET_BASE_PATH + "/" + targetId.intValue();

        when(modelMapper.map(targetNode, TargetModel.class)).thenReturn(targetModel);
        when(countryModelAssembler.toModel(countryNode)).thenReturn(countryModel);

        TargetModel targetModelActual = targetModelAssembler.toModel(targetNode);

        assertAll(
                () -> assertNotNull(targetModelActual.getId(),
                        () -> "should return target node with id, but was null"),
                () -> assertEquals(pathToTargetLink, targetModelActual.getLink("self").get().getHref(),
                        () -> "should return target model with self link: " + pathToTargetLink + ", but was: "
                                + targetModelActual.getLink("self").get().getHref()),
                () -> assertEquals(pathToCountryLink, targetModelActual.getCountryOfOrigin().getLink("self").get().getHref(),
                        () -> "should return target model with country model with self link: " + pathToCountryLink + ", but was: "
                                + targetModelActual.getCountryOfOrigin().getLink("self").get().getHref()),
                () -> assertFalse(targetModelActual.getLinks().isEmpty(),
                        () -> "should return target model with links, but wasn't"),
                () -> assertFalse(targetModelActual.getCountryOfOrigin().getLinks().isEmpty(),
                        () -> "should return target country model with links, but wasn't"),

                () -> assertEquals(targetNode.getId(), targetModelActual.getId(),
                        () -> "should return target model with id: " + targetNode.getId() + ", but was: "
                                + targetModelActual.getId()),
                () -> assertEquals(targetNode.getTarget(), targetModelActual.getTarget(),
                        () -> "should return target model with target: " + targetNode.getTarget() + ", but was: "
                                + targetModelActual.getTarget()),
                () -> assertNotNull(targetModelActual.getLinks(),
                        () -> "should return target model with links, but was: " + targetModelActual),
                () -> assertFalse(targetModelActual.getLinks().isEmpty(),
                        () -> "should return target model with links, but was: " + targetModelActual),
                () -> assertEquals(countryModel.getId(), targetModelActual.getCountryOfOrigin().getId(),
                        () -> "should return target model with country id: " + targetModelActual.getCountryOfOrigin().getId()
                                + ", but was: " + targetModelActual.getId()),
                () -> assertEquals(countryModel.getName(), targetModelActual.getCountryOfOrigin().getName(),
                        () -> "should return target model with country name: " + targetModelActual.getCountryOfOrigin().getName()
                                + ", but was: " + targetModelActual.getCountryOfOrigin()),
                () -> assertEquals(regionModel.getId(), targetModelActual.getCountryOfOrigin().getRegion().getId(),
                        () -> "should return target model with region id: " + regionModel.getId()
                                + ", but was: " + targetModelActual.getCountryOfOrigin().getRegion().getId()),
                () -> assertEquals(regionModel.getName(), targetModelActual.getCountryOfOrigin().getRegion().getName(),
                        () -> "should return target model with region name: " + regionModel.getName()
                                + ", but was: " + targetModelActual.getCountryOfOrigin().getRegion().getName()),
                () -> assertTrue(targetModelActual.getCountryOfOrigin().getRegion().getLinks().isEmpty(),
                        () -> "should return target with region model without links, but was: " +
                                targetModelActual.getCountryOfOrigin().getRegion().getLinks()),
                () -> verify(modelMapper, times(1)).map(targetNode, TargetModel.class),
                () -> verifyNoMoreInteractions(modelMapper),
                () -> verify(countryModelAssembler, times(1)).toModel(countryNode),
                () -> verifyNoMoreInteractions(countryModelAssembler)
        );
    }
}
