package com.nowakArtur97.globalTerrorismAPI.feature.province;

import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryModel;
import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryModelAssembler;
import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryNode;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.CountryBuilder;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.ProvinceBuilder;
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
@Tag("ProvinceModelAssembler_Tests")
@DisabledOnOs(OS.LINUX)
class ProvinceModelAssemblerTest {

    private final String COUNTRY_BASE_PATH = "http://localhost/api/v1/countries";
    private final String PROVINCE_BASE_PATH = "http://localhost/api/v1/provinces";

    private ProvinceModelAssembler modelAssembler;

    @Mock
    private CountryModelAssembler countryModelAssembler;

    @Mock
    private ModelMapper modelMapper;

    private static CountryBuilder countryBuilder;
    private static ProvinceBuilder provinceBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        countryBuilder = new CountryBuilder();
        provinceBuilder = new ProvinceBuilder();
    }

    @BeforeEach
    private void setUp() {

        modelAssembler = new ProvinceModelAssembler(countryModelAssembler, modelMapper);
    }

    @Test
    void when_map_province_node_to_model_should_return_province_model() {

        Long countryId = 1L;
        Long provinceId = 2L;
        CountryNode countryNode = (CountryNode) countryBuilder.withId(countryId).build(ObjectType.NODE);
        ProvinceNode provinceNode = (ProvinceNode) provinceBuilder.withId(provinceId).withCountry(countryNode)
                .build(ObjectType.NODE);

        CountryModel countryModelExpected = (CountryModel) countryBuilder.withId(countryId).build(ObjectType.MODEL);
        String pathToCountryLink = COUNTRY_BASE_PATH + "/" + countryId.intValue();
        countryModelExpected.add(new Link(pathToCountryLink));

        ProvinceModel provinceModelExpected = (ProvinceModel) provinceBuilder.withId(provinceId).withCountry(countryModelExpected)
                .build(ObjectType.MODEL);

        String pathToProvinceLink = PROVINCE_BASE_PATH + "/" + provinceId.intValue();

        when(modelMapper.map(provinceNode, ProvinceModel.class)).thenReturn(provinceModelExpected);
        when(countryModelAssembler.toModel(countryNode)).thenReturn(countryModelExpected);

        ProvinceModel provinceModelActual = modelAssembler.toModel(provinceNode);

        assertAll(
                () -> assertEquals(pathToProvinceLink, provinceModelActual.getLink("self").get().getHref(),
                        () -> "should return province model with self link: " + pathToProvinceLink + ", but was: "
                                + provinceModelActual.getLink("self").get().getHref()),
                () -> assertEquals(pathToCountryLink, provinceModelActual.getCountry().getLink("self").get().getHref(),
                        () -> "should return province model with country model with self link: " + pathToProvinceLink + ", but was: "
                                + provinceModelActual.getCountry().getLink("self").get().getHref()),

                () -> assertNotNull(provinceModelActual, () -> "should return not null province model, but was: null"),
                () -> assertEquals(provinceModelExpected.getId(), provinceModelActual.getId(),
                        () -> "should return province model with province model id: " + provinceModelExpected.getId() + ", but was: "
                                + provinceModelActual.getId()),
                () -> assertEquals(provinceModelExpected.getName(), provinceModelActual.getName(),
                        () -> "should return province model with name: " + provinceModelExpected.getName() + ", but was: "
                                + provinceModelActual.getName()),

                () -> assertNotNull(provinceModelExpected.getCountry(),
                        () -> "should return province model with not null country, but was: null"),
                () -> assertEquals(countryModelExpected, provinceModelExpected.getCountry(),
                        () -> "should return province model with country: " + provinceModelExpected + ", but was: "
                                + provinceModelExpected.getCountry()),
                () -> assertEquals(countryModelExpected.getId(), provinceModelExpected.getCountry().getId(),
                        () -> "should return province model with country id: " + countryModelExpected.getId()
                                + ", but was: " + provinceModelExpected.getCountry().getId()),
                () -> assertEquals(countryModelExpected.getName(), provinceModelExpected.getCountry().getName(),
                        () -> "should return province model with country name: " + countryModelExpected.getName() + ", but was: "
                                + provinceModelExpected.getCountry().getName()),

                () -> assertFalse(provinceModelActual.getLinks().isEmpty(),
                        () -> "should return province model with links, but wasn't"),
                () -> assertFalse(provinceModelActual.getCountry().getLinks().isEmpty(),
                        () -> "should return province model with country model with links, but was: " +
                                provinceModelActual.getCountry().getLinks()),
                () -> verify(modelMapper, times(1)).map(provinceNode, ProvinceModel.class),
                () -> verifyNoMoreInteractions(modelMapper),
                () -> verify(countryModelAssembler, times(1)).toModel(countryNode),
                () -> verifyNoMoreInteractions(countryModelAssembler));
    }
}
