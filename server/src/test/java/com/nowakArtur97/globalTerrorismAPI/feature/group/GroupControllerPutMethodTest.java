package com.nowakArtur97.globalTerrorismAPI.feature.group;

import com.nowakArtur97.globalTerrorismAPI.common.util.JwtUtil;
import com.nowakArtur97.globalTerrorismAPI.feature.city.CityDTO;
import com.nowakArtur97.globalTerrorismAPI.feature.city.CityNode;
import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryDTO;
import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryNode;
import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryRepository;
import com.nowakArtur97.globalTerrorismAPI.feature.event.EventDTO;
import com.nowakArtur97.globalTerrorismAPI.feature.event.EventNode;
import com.nowakArtur97.globalTerrorismAPI.feature.province.ProvinceDTO;
import com.nowakArtur97.globalTerrorismAPI.feature.province.ProvinceNode;
import com.nowakArtur97.globalTerrorismAPI.feature.province.ProvinceRepository;
import com.nowakArtur97.globalTerrorismAPI.feature.region.RegionNode;
import com.nowakArtur97.globalTerrorismAPI.feature.target.TargetDTO;
import com.nowakArtur97.globalTerrorismAPI.feature.target.TargetNode;
import com.nowakArtur97.globalTerrorismAPI.feature.user.shared.RoleNode;
import com.nowakArtur97.globalTerrorismAPI.feature.user.shared.UserNode;
import com.nowakArtur97.globalTerrorismAPI.feature.user.shared.UserRepository;
import com.nowakArtur97.globalTerrorismAPI.feature.victim.VictimDTO;
import com.nowakArtur97.globalTerrorismAPI.feature.victim.VictimNode;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.*;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.nowakArtur97.globalTerrorismAPI.testUtil.configuration.Neo4jTestConfiguration;
import com.nowakArtur97.globalTerrorismAPI.testUtil.database.Neo4jDatabaseUtil;
import com.nowakArtur97.globalTerrorismAPI.testUtil.mapper.ObjectTestMapper;
import com.nowakArtur97.globalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import(Neo4jTestConfiguration.class)
@AutoConfigureMockMvc
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("GroupController_Tests")
class GroupControllerPutMethodTest {

    private final String REGION_BASE_PATH = "http://localhost:8080/api/v1/regions";
    private final String COUNTRY_BASE_PATH = "http://localhost:8080/api/v1/countries";
    private final String PROVINCE_BASE_PATH = "http://localhost:8080/api/v1/provinces";
    private final String CITY_BASE_PATH = "http://localhost:8080/api/v1/cities";
    private final String VICTIM_BASE_PATH = "http://localhost:8080/api/v1/victims";
    private final String GROUP_BASE_PATH = "http://localhost:8080/api/v1/groups";
    private final String LINK_WITH_PARAMETER = GROUP_BASE_PATH + "/" + "{id}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    private static CountryBuilder countryBuilder;
    private static TargetBuilder targetBuilder;
    private static ProvinceBuilder provinceBuilder;
    private static CityBuilder cityBuilder;
    private static VictimBuilder victimBuilder;
    private static EventBuilder eventBuilder;
    private static GroupBuilder groupBuilder;

    private final static UserNode userNode = new UserNode("user1234", "Password1234!", "user1234email@.com",
            Set.of(new RoleNode("user")));

    private final static RegionNode regionNode = new RegionNode("region name");
    private final static RegionNode anotherRegionNode = new RegionNode("region name 2");

    private final static CountryNode countryNode = new CountryNode("country name", regionNode);
    private final static CountryNode anotherCountryNode = new CountryNode("country name 2", anotherRegionNode);

    private final static TargetNode targetNode = new TargetNode("target name", countryNode);
    private final static TargetNode targetNode2 = new TargetNode("target name 2", countryNode);

    private final static ProvinceNode provinceNode = new ProvinceNode("province name", countryNode);

    private final static CityNode cityNode = new CityNode("city name", 25.0, 41.0, provinceNode);
    private final static CityNode anotherCityNode = new CityNode("city name 2", 11.0, 25.0, provinceNode);

    private final static VictimNode victimNode = new VictimNode(10L, 0L, 10L, 0L, 1000L);

    private final static EventNode eventNode = new EventNode("summary", "motive", new Date(),
            true, true, true, targetNode, cityNode, victimNode);
    private final static EventNode eventNode2 = new EventNode("summary 2", "motive 2", new Date(),
            false, false, false, targetNode2, anotherCityNode, victimNode);

    private final static GroupNode groupNode = new GroupNode("group name", List.of(eventNode, eventNode2));

    @BeforeAll
    private static void setUpBuilders() {

        countryBuilder = new CountryBuilder();
        targetBuilder = new TargetBuilder();
        provinceBuilder = new ProvinceBuilder();
        cityBuilder = new CityBuilder();
        victimBuilder = new VictimBuilder();
        eventBuilder = new EventBuilder();
        groupBuilder = new GroupBuilder();
    }

    @BeforeAll
    private static void setUp(@Autowired UserRepository userRepository, @Autowired GroupRepository groupRepository,
                              @Autowired CountryRepository countryRepository) {

        userRepository.save(userNode);

        countryRepository.save(anotherCountryNode);

        groupRepository.save(groupNode);
    }

    @AfterAll
    private static void tearDown(@Autowired Neo4jDatabaseUtil neo4jDatabaseUtil, @Autowired ProvinceRepository provinceRepository) {

        provinceRepository.findAll().forEach(System.out::println);

        neo4jDatabaseUtil.cleanDatabase();
    }

    @Test
    void when_update_valid_group_should_return_updated_group_as_model() {

        String updatedGroupName = "new group name";

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withTarget(targetNode.getTarget()).withCountry(countryDTO)
                .build(ObjectType.DTO);
        TargetDTO targetDTO2 = (TargetDTO) targetBuilder.withTarget(targetNode2.getTarget()).withCountry(countryDTO)
                .build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO2 = (ProvinceDTO) provinceBuilder.withName("new country province")
                .withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        CityDTO cityDTO2 = (CityDTO) cityBuilder.withName("new event city name").withProvince(provinceDTO2)
                .build(ObjectType.DTO);
        VictimDTO victimDTO = (VictimDTO) victimBuilder.build(ObjectType.DTO);
        VictimDTO victimDTO2 = (VictimDTO) victimBuilder.withTotalNumberOfFatalities(1002L).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withSummary(eventNode.getSummary()).withMotive(eventNode.getMotive())
                .withDate(eventNode.getDate()).withIsPartOfMultipleIncidents(eventNode.getIsPartOfMultipleIncidents())
                .withIsSuccessful(eventNode.getIsSuccessful()).withIsSuicidal(eventNode.getIsSuicidal())
                .withTarget(targetDTO).withCity(cityDTO).withVictim(victimDTO)
                .build(ObjectType.DTO);
        EventDTO eventDTO2 = (EventDTO) eventBuilder.withSummary(eventNode2.getSummary()).withMotive(eventNode2.getMotive())
                .withDate(eventNode2.getDate()).withIsPartOfMultipleIncidents(eventNode2.getIsPartOfMultipleIncidents())
                .withIsSuccessful(eventNode2.getIsSuccessful()).withIsSuicidal(eventNode2.getIsSuicidal())
                .withTarget(targetDTO2).withCity(cityDTO2).withVictim(victimDTO2)
                .build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withName(updatedGroupName).withEventsCaused(List.of(eventDTO, eventDTO2))
                .build(ObjectType.DTO);

        String pathToRegionLink = REGION_BASE_PATH + "/" + regionNode.getId().intValue();
        String pathToCountryLink = COUNTRY_BASE_PATH + "/" + countryNode.getId().intValue();
        String pathToGroupLink = GROUP_BASE_PATH + "/" + groupNode.getId().intValue();
        String pathToEventsLink = GROUP_BASE_PATH + "/" + groupNode.getId().intValue() + "/events";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToGroupLink)))
                        .andExpect(jsonPath("links[1].href", is(pathToEventsLink)))
                        .andExpect(jsonPath("id", is(groupNode.getId().intValue())))
                        .andExpect(jsonPath("name", is(groupDTO.getName())))
                        .andExpect(jsonPath("eventsCaused[0].links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].links[1].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].summary", is(eventDTO.getSummary())))
                        .andExpect(jsonPath("eventsCaused[0].motive", is(eventDTO.getMotive())))
                        .andExpect(jsonPath("eventsCaused[0].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventDTO.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("eventsCaused[0].isSuicidal", is(eventDTO.getIsSuicidal())))
                        .andExpect(jsonPath("eventsCaused[0].isSuccessful", is(eventDTO.getIsSuccessful())))
                        .andExpect(jsonPath("eventsCaused[0].isPartOfMultipleIncidents",
                                is(eventDTO.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("eventsCaused[0].target.links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].target.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].target.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].target.target", is(targetDTO.getTarget())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.links[0].href", is(pathToCountryLink)))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.id",
                                is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.region.links[0].href",
                                is(pathToRegionLink)))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.region.id",
                                is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.region.name",
                                is(regionNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].city.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].city.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].city.name", is(cityDTO.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.latitude", is(cityDTO.getLatitude())))
                        .andExpect(jsonPath("eventsCaused[0].city.longitude", is(cityDTO.getLongitude())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].city.province.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].city.province.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].city.province.name", is(provinceDTO.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.links[0].href", is(pathToCountryLink)))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.id",
                                is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.region.links[0].href",
                                is(pathToRegionLink)))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.region.id",
                                is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.region.name", is(regionNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].victim.links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].victim.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].victim.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].victim.totalNumberOfFatalities",
                                is(victimDTO.getTotalNumberOfFatalities().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].victim.numberOfPerpetratorsFatalities",
                                is(victimDTO.getNumberOfPerpetratorsFatalities().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].victim.totalNumberOfInjured",
                                is(victimDTO.getTotalNumberOfInjured().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].victim.numberOfPerpetratorsInjured",
                                is(victimDTO.getNumberOfPerpetratorsInjured().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].victim.valueOfPropertyDamage",
                                is(victimDTO.getValueOfPropertyDamage().intValue())))

                        .andExpect(jsonPath("eventsCaused[1].links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].links[1].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].summary", is(eventDTO2.getSummary())))
                        .andExpect(jsonPath("eventsCaused[1].motive", is(eventDTO2.getMotive())))
                        .andExpect(jsonPath("eventsCaused[1].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventDTO2.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("eventsCaused[1].isSuicidal", is(eventDTO2.getIsSuicidal())))
                        .andExpect(jsonPath("eventsCaused[1].isSuccessful", is(eventDTO2.getIsSuccessful())))
                        .andExpect(jsonPath("eventsCaused[1].isPartOfMultipleIncidents",
                                is(eventDTO2.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("eventsCaused[1].target.links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].target.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].target.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].target.target", is(targetDTO2.getTarget())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.links[0].href", is(pathToCountryLink)))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.id",
                                is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.region.links[0].href",
                                is(pathToRegionLink)))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.region.id",
                                is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.region.name",
                                is(regionNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].city.links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].city.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].city.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].city.name", is(cityDTO2.getName())))
                        .andExpect(jsonPath("eventsCaused[1].city.latitude", is(cityDTO2.getLatitude())))
                        .andExpect(jsonPath("eventsCaused[1].city.longitude", is(cityDTO2.getLongitude())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].city.province.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].city.province.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].city.province.name", is(provinceDTO2.getName())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.links[0].href", is(pathToCountryLink)))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.id",
                                is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.region.links[0].href",
                                is(pathToRegionLink)))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.region.id",
                                is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.region.name", is(regionNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].victim.links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].victim.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].victim.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].victim.totalNumberOfFatalities",
                                is(victimDTO2.getTotalNumberOfFatalities().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].victim.numberOfPerpetratorsFatalities",
                                is(victimDTO2.getNumberOfPerpetratorsFatalities().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].victim.totalNumberOfInjured",
                                is(victimDTO2.getTotalNumberOfInjured().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].victim.numberOfPerpetratorsInjured",
                                is(victimDTO2.getNumberOfPerpetratorsInjured().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].victim.valueOfPropertyDamage",
                                is(victimDTO2.getValueOfPropertyDamage().intValue())))
                        .andExpect(jsonPath("eventsCaused[2]").doesNotExist()));
    }

    @Test
    void when_update_valid_group_with_events_should_return_updated_group_as_model() throws ParseException {

        String updatedTargetName = "new target name";

        String updatedProvinceName = "new province name";
        String updatedCityName = "new city name";
        Double updatedCityLatitude = -15.0;
        Double updatedCityLongitude = -15.0;

        String updatedSummary = "summary updated";
        String updatedMotive = "motive updated";
        Date updatedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS").parse("01/08/2010 02:00:00:000");
        boolean updatedIsPartOfMultipleIncidents = false;
        boolean updatedIsSuccessful = false;
        boolean updatedIsSuicidal = false;

        String updatedGroupName = "new group name";

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        CountryDTO countryDTO2 = (CountryDTO) countryBuilder.withName(anotherCountryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withTarget(updatedTargetName).withCountry(countryDTO)
                .build(ObjectType.DTO);
        TargetDTO targetDTO2 = (TargetDTO) targetBuilder.withTarget(updatedTargetName + " 2").withCountry(countryDTO2)
                .build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO2 = (ProvinceDTO) provinceBuilder.withName(updatedProvinceName)
                .withCountry(countryDTO2).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withName("new city name").withProvince(provinceDTO).build(ObjectType.DTO);
        CityDTO cityDTO2 = (CityDTO) cityBuilder.withName(updatedCityName).withLatitude(updatedCityLatitude)
                .withLongitude(updatedCityLongitude).withProvince(provinceDTO2).build(ObjectType.DTO);
        VictimDTO victimDTO = (VictimDTO) victimBuilder.withTotalNumberOfFatalities(201L).build(ObjectType.DTO);
        VictimDTO victimDTO2 = (VictimDTO) victimBuilder.withTotalNumberOfFatalities(1020L).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withSummary(updatedSummary + " 2").withMotive(updatedMotive + " 2")
                .withDate(updatedDate).withIsPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents)
                .withIsSuccessful(updatedIsSuccessful).withIsSuicidal(updatedIsSuicidal)
                .withTarget(targetDTO).withCity(cityDTO).withVictim(victimDTO)
                .build(ObjectType.DTO);
        EventDTO eventDTO2 = (EventDTO) eventBuilder.withSummary(updatedSummary).withMotive(updatedMotive)
                .withDate(updatedDate).withIsPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents)
                .withIsSuccessful(updatedIsSuccessful).withIsSuicidal(updatedIsSuicidal).withTarget(targetDTO2)
                .withTarget(targetDTO2).withCity(cityDTO2).withVictim(victimDTO2)
                .build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withName(updatedGroupName).withEventsCaused(List.of(eventDTO, eventDTO2))
                .build(ObjectType.DTO);

        String pathToRegionLink = REGION_BASE_PATH + "/" + regionNode.getId().intValue();
        String pathToRegionLink2 = REGION_BASE_PATH + "/" + anotherRegionNode.getId().intValue();
        String pathToCountryLink = COUNTRY_BASE_PATH + "/" + countryNode.getId().intValue();
        String pathToCountryLink2 = COUNTRY_BASE_PATH + "/" + anotherCountryNode.getId().intValue();
        String pathToGroupLink = GROUP_BASE_PATH + "/" + groupNode.getId().intValue();
        String pathToEventsLink = GROUP_BASE_PATH + "/" + groupNode.getId().intValue() + "/events";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToGroupLink)))
                        .andExpect(jsonPath("links[1].href", is(pathToEventsLink)))
                        .andExpect(jsonPath("id", is(groupNode.getId().intValue())))
                        .andExpect(jsonPath("name", is(groupDTO.getName())))
                        .andExpect(jsonPath("eventsCaused[0].links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].links[1].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].summary", is(eventDTO.getSummary())))
                        .andExpect(jsonPath("eventsCaused[0].motive", is(eventDTO.getMotive())))
                        .andExpect(jsonPath("eventsCaused[0].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventDTO.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("eventsCaused[0].isSuicidal", is(eventDTO.getIsSuicidal())))
                        .andExpect(jsonPath("eventsCaused[0].isSuccessful", is(eventDTO.getIsSuccessful())))
                        .andExpect(jsonPath("eventsCaused[0].isPartOfMultipleIncidents",
                                is(eventDTO.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("eventsCaused[0].target.links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].target.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].target.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].target.target", is(targetDTO.getTarget())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.links[0].href", is(pathToCountryLink)))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.id",
                                is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.region.links[0].href",
                                is(pathToRegionLink)))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.region.id",
                                is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.region.name",
                                is(regionNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].city.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].city.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].city.name", is(cityDTO.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.latitude", is(cityDTO.getLatitude())))
                        .andExpect(jsonPath("eventsCaused[0].city.longitude", is(cityDTO.getLongitude())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].city.province.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].city.province.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].city.province.name", is(provinceDTO.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.links[0].href", is(pathToCountryLink)))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.id",
                                is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.region.links[0].href",
                                is(pathToRegionLink)))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.region.id",
                                is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.region.name",
                                is(regionNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].victim.links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].victim.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].victim.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].victim.totalNumberOfFatalities",
                                is(victimDTO.getTotalNumberOfFatalities().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].victim.numberOfPerpetratorsFatalities",
                                is(victimDTO.getNumberOfPerpetratorsFatalities().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].victim.totalNumberOfInjured",
                                is(victimDTO.getTotalNumberOfInjured().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].victim.numberOfPerpetratorsInjured",
                                is(victimDTO.getNumberOfPerpetratorsInjured().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].victim.valueOfPropertyDamage",
                                is(victimDTO.getValueOfPropertyDamage().intValue())))

                        .andExpect(jsonPath("eventsCaused[1].links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].links[1].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].summary", is(eventDTO2.getSummary())))
                        .andExpect(jsonPath("eventsCaused[1].motive", is(eventDTO2.getMotive())))
                        .andExpect(jsonPath("eventsCaused[1].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventDTO2.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("eventsCaused[1].isSuicidal", is(eventDTO2.getIsSuicidal())))
                        .andExpect(jsonPath("eventsCaused[1].isSuccessful", is(eventDTO2.getIsSuccessful())))
                        .andExpect(jsonPath("eventsCaused[1].isPartOfMultipleIncidents",
                                is(eventDTO2.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("eventsCaused[1].target.links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].target.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].target.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].target.target", is(targetDTO2.getTarget())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.id",
                                is(anotherCountryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.id",
                                is(anotherCountryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.name",
                                is(anotherCountryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.region.links[0].href",
                                is(pathToRegionLink2)))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.region.id",
                                is(anotherRegionNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.region.name",
                                is(anotherRegionNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].city.links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].city.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].city.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].city.name", is(cityDTO2.getName())))
                        .andExpect(jsonPath("eventsCaused[1].city.latitude", is(cityDTO2.getLatitude())))
                        .andExpect(jsonPath("eventsCaused[1].city.longitude", is(cityDTO2.getLongitude())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].city.province.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].city.province.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].city.province.name", is(updatedProvinceName)))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.links[0].href", is(pathToCountryLink2)))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.id",
                                is(anotherCountryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.name",
                                is(anotherCountryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.region.links[0].href",
                                is(pathToRegionLink2)))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.region.id",
                                is(anotherRegionNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.region.name",
                                is(anotherRegionNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].victim.links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].victim.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].victim.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].victim.totalNumberOfFatalities",
                                is(victimDTO2.getTotalNumberOfFatalities().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].victim.numberOfPerpetratorsFatalities",
                                is(victimDTO2.getNumberOfPerpetratorsFatalities().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].victim.totalNumberOfInjured",
                                is(victimDTO2.getTotalNumberOfInjured().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].victim.numberOfPerpetratorsInjured",
                                is(victimDTO2.getNumberOfPerpetratorsInjured().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].victim.valueOfPropertyDamage",
                                is(victimDTO2.getValueOfPropertyDamage().intValue())))
                        .andExpect(jsonPath("eventsCaused[2]").doesNotExist()));
    }

    @Test
    void when_update_valid_group_with_events_using_existing_city_should_return_updated_group_as_model() {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        TargetDTO targetDTO2 = (TargetDTO) targetBuilder.withTarget("target2").withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withName(provinceNode.getName()).withCountry(countryDTO)
                .build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withName(cityNode.getName()).withLatitude(cityNode.getLatitude())
                .withLongitude(cityNode.getLongitude()).withProvince(provinceDTO).build(ObjectType.DTO);
        VictimDTO victimDTO = (VictimDTO) victimBuilder.withTotalNumberOfFatalities(201L).build(ObjectType.DTO);
        VictimDTO victimDTO2 = (VictimDTO) victimBuilder.withTotalNumberOfFatalities(1020L).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).withVictim(victimDTO)
                .build(ObjectType.DTO);
        EventDTO eventDTO2 = (EventDTO) eventBuilder.withTarget(targetDTO2).withCity(cityDTO).withVictim(victimDTO2)
                .build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO, eventDTO2))
                .build(ObjectType.DTO);

        String pathToRegionLink = REGION_BASE_PATH + "/" + regionNode.getId().intValue();
        String pathToCountryLink = COUNTRY_BASE_PATH + "/" + countryNode.getId().intValue();
        String pathToProvinceLink = PROVINCE_BASE_PATH + "/" + provinceNode.getId().intValue();
        String pathToCityLink = CITY_BASE_PATH + "/" + cityNode.getId().intValue();
        String pathToGroupLink = GROUP_BASE_PATH + "/" + groupNode.getId().intValue();
        String pathToEventsLink = GROUP_BASE_PATH + "/" + groupNode.getId().intValue() + "/events";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToGroupLink)))
                        .andExpect(jsonPath("links[1].href", is(pathToEventsLink)))
                        .andExpect(jsonPath("id", is(groupNode.getId().intValue())))
                        .andExpect(jsonPath("name", is(groupDTO.getName())))
                        .andExpect(jsonPath("eventsCaused[0].links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].links[1].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].summary", is(eventDTO.getSummary())))
                        .andExpect(jsonPath("eventsCaused[0].motive", is(eventDTO.getMotive())))
                        .andExpect(jsonPath("eventsCaused[0].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventDTO.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("eventsCaused[0].isSuicidal", is(eventDTO.getIsSuicidal())))
                        .andExpect(jsonPath("eventsCaused[0].isSuccessful", is(eventDTO.getIsSuccessful())))
                        .andExpect(jsonPath("eventsCaused[0].isPartOfMultipleIncidents",
                                is(eventDTO.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("eventsCaused[0].target.links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].target.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].target.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].target.target", is(targetDTO.getTarget())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.links[0].href", is(pathToCountryLink)))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.id",
                                is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.region.links[0].href",
                                is(pathToRegionLink)))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.region.id",
                                is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.region.name",
                                is(regionNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.links[0].href", is(pathToCityLink)))
                        .andExpect(jsonPath("eventsCaused[0].city.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].city.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].city.name", is(cityNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.latitude", is(cityNode.getLatitude())))
                        .andExpect(jsonPath("eventsCaused[0].city.longitude", is(cityNode.getLongitude())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.links[0].href", is(pathToProvinceLink)))
                        .andExpect(jsonPath("eventsCaused[0].city.province.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].city.province.id", is(provinceNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.name", is(provinceNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.links[0].href", is(pathToCountryLink)))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.id",
                                is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.region.links[0].href",
                                is(pathToRegionLink)))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.region.id",
                                is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.region.name", is(regionNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].victim.links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].victim.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].victim.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].victim.totalNumberOfFatalities",
                                is(victimDTO.getTotalNumberOfFatalities().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].victim.numberOfPerpetratorsFatalities",
                                is(victimDTO.getNumberOfPerpetratorsFatalities().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].victim.totalNumberOfInjured",
                                is(victimDTO.getTotalNumberOfInjured().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].victim.numberOfPerpetratorsInjured",
                                is(victimDTO.getNumberOfPerpetratorsInjured().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].victim.valueOfPropertyDamage",
                                is(victimDTO.getValueOfPropertyDamage().intValue())))

                        .andExpect(jsonPath("eventsCaused[1].links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].links[1].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].summary", is(eventDTO2.getSummary())))
                        .andExpect(jsonPath("eventsCaused[1].motive", is(eventDTO2.getMotive())))
                        .andExpect(jsonPath("eventsCaused[1].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventDTO2.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("eventsCaused[1].isSuicidal", is(eventDTO2.getIsSuicidal())))
                        .andExpect(jsonPath("eventsCaused[1].isSuccessful", is(eventDTO2.getIsSuccessful())))
                        .andExpect(jsonPath("eventsCaused[1].isPartOfMultipleIncidents",
                                is(eventDTO2.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("eventsCaused[1].target.links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].target.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].target.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].target.target", is(targetDTO2.getTarget())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.links[0].href", is(pathToCountryLink)))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.id",
                                is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.region.links[0].href",
                                is(pathToRegionLink)))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.region.id",
                                is(is(regionNode.getId().intValue()))))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.region.name",
                                is(regionNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].city.links[0].href", is(pathToCityLink)))
                        .andExpect(jsonPath("eventsCaused[1].city.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].city.id", is(cityNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].city.name", is(cityNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].city.latitude", is(cityNode.getLatitude())))
                        .andExpect(jsonPath("eventsCaused[1].city.longitude", is(cityNode.getLongitude())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.links[0].href", is(pathToProvinceLink)))
                        .andExpect(jsonPath("eventsCaused[1].city.province.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].city.province.id", is(provinceNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.name", is(provinceNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.links[0].href", is(pathToCountryLink)))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.id",
                                is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.region.links[0].href",
                                is(pathToRegionLink)))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.region.id",
                                is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.region.name", is(regionNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].victim.links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].victim.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].victim.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].victim.totalNumberOfFatalities",
                                is(victimDTO2.getTotalNumberOfFatalities().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].victim.numberOfPerpetratorsFatalities",
                                is(victimDTO2.getNumberOfPerpetratorsFatalities().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].victim.totalNumberOfInjured",
                                is(victimDTO2.getTotalNumberOfInjured().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].victim.numberOfPerpetratorsInjured",
                                is(victimDTO2.getNumberOfPerpetratorsInjured().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].victim.valueOfPropertyDamage",
                                is(victimDTO2.getValueOfPropertyDamage().intValue())))
                        .andExpect(jsonPath("eventsCaused[2]").doesNotExist()));
    }

    @Test
    void when_update_valid_group_with_not_existing_id_should_return_new_group_as_model() {

        Long notExistingId = 10000L;

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        CountryDTO countryDTO2 = (CountryDTO) countryBuilder.withName(anotherCountryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        TargetDTO targetDTO2 = (TargetDTO) targetBuilder.withTarget("target 2").withCountry(countryDTO2)
                .build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withName(provinceNode.getName())
                .withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO2 = (ProvinceDTO) provinceBuilder.withName("new country province")
                .withCountry(countryDTO2).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withName(anotherCityNode.getName())
                .withLatitude(anotherCityNode.getLatitude()).withLongitude(anotherCityNode.getLongitude())
                .withProvince(provinceDTO).build(ObjectType.DTO);
        CityDTO cityDTO2 = (CityDTO) cityBuilder.withName("new group city name").withProvince(provinceDTO2)
                .build(ObjectType.DTO);
        VictimDTO victimDTO = (VictimDTO) victimBuilder.withTotalNumberOfFatalities(201L).build(ObjectType.DTO);
        VictimDTO victimDTO2 = (VictimDTO) victimBuilder.withTotalNumberOfFatalities(1020L).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).withVictim(victimDTO)
                .build(ObjectType.DTO);
        EventDTO eventDTO2 = (EventDTO) eventBuilder.withMotive("motive 2").withSummary("summary 2")
                .withIsSuicidal(false).withIsSuccessful(false).withIsPartOfMultipleIncidents(false)
                .withTarget(targetDTO2).withCity(cityDTO2).withVictim(victimDTO2).build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO, eventDTO2)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        String pathToRegionLink = REGION_BASE_PATH + "/" + regionNode.getId().intValue();
        String pathToRegionLink2 = REGION_BASE_PATH + "/" + anotherRegionNode.getId().intValue();
        String pathToCountryLink = COUNTRY_BASE_PATH + "/" + countryNode.getId().intValue();
        String pathToCountryLink2 = COUNTRY_BASE_PATH + "/" + anotherCountryNode.getId().intValue();
        String pathToProvinceLink = PROVINCE_BASE_PATH + "/" + provinceNode.getId().intValue();
        String pathToCityLink = CITY_BASE_PATH + "/" + anotherCityNode.getId().intValue();

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, notExistingId)
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", notNullValue()))
                        .andExpect(jsonPath("links[1].href", notNullValue()))
                        .andExpect(jsonPath("id", notNullValue()))
                        .andExpect(jsonPath("name", is(groupDTO.getName())))
                        .andExpect(jsonPath("eventsCaused[0].links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].links[1].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].summary", is(eventDTO.getSummary())))
                        .andExpect(jsonPath("eventsCaused[0].motive", is(eventDTO.getMotive())))
                        .andExpect(jsonPath("eventsCaused[0].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventDTO.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("eventsCaused[0].isSuicidal", is(eventDTO.getIsSuicidal())))
                        .andExpect(jsonPath("eventsCaused[0].isSuccessful", is(eventDTO.getIsSuccessful())))
                        .andExpect(jsonPath("eventsCaused[0].isPartOfMultipleIncidents",
                                is(eventDTO.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("eventsCaused[0].target.links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].target.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].target.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].target.target", is(targetDTO.getTarget())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.links[0].href", is(pathToCountryLink)))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.id",
                                is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.region.links[0].href",
                                is(pathToRegionLink)))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.region.id",
                                is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.region.name",
                                is(regionNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.links[0].href", is(pathToCityLink)))
                        .andExpect(jsonPath("eventsCaused[0].city.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].city.id", is(anotherCityNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].city.name", is(anotherCityNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.latitude", is(anotherCityNode.getLatitude())))
                        .andExpect(jsonPath("eventsCaused[0].city.longitude", is(anotherCityNode.getLongitude())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.links[0].href", is(pathToProvinceLink)))
                        .andExpect(jsonPath("eventsCaused[0].city.province.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].city.province.id", is(provinceNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.name", is(provinceNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.links[0].href", is(pathToCountryLink)))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.id",
                                is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.region.links[0].href",
                                is(pathToRegionLink)))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.region.id",
                                is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.region.name", is(regionNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].victim.links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].victim.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].victim.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].victim.totalNumberOfFatalities",
                                is(victimDTO.getTotalNumberOfFatalities().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].victim.numberOfPerpetratorsFatalities",
                                is(victimDTO.getNumberOfPerpetratorsFatalities().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].victim.totalNumberOfInjured",
                                is(victimDTO.getTotalNumberOfInjured().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].victim.numberOfPerpetratorsInjured",
                                is(victimDTO.getNumberOfPerpetratorsInjured().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].victim.valueOfPropertyDamage",
                                is(victimDTO.getValueOfPropertyDamage().intValue())))

                        .andExpect(jsonPath("eventsCaused[1].links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].links[1].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].summary", is(eventDTO2.getSummary())))
                        .andExpect(jsonPath("eventsCaused[1].motive", is(eventDTO2.getMotive())))
                        .andExpect(jsonPath("eventsCaused[1].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventDTO2.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("eventsCaused[1].isSuicidal", is(eventDTO2.getIsSuicidal())))
                        .andExpect(jsonPath("eventsCaused[1].isSuccessful", is(eventDTO2.getIsSuccessful())))
                        .andExpect(jsonPath("eventsCaused[1].isPartOfMultipleIncidents",
                                is(eventDTO2.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("eventsCaused[1].target.links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].target.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].target.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].target.target", is(targetDTO2.getTarget())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.links[0].href",
                                is(pathToCountryLink2)))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.id",
                                is(anotherCountryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.name",
                                is(anotherCountryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.region.links[0].href",
                                is(pathToRegionLink2)))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.region.id",
                                is(anotherRegionNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.region.name",
                                is(anotherRegionNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].city.links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].city.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].city.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].city.name", is(cityDTO2.getName())))
                        .andExpect(jsonPath("eventsCaused[1].city.latitude", is(cityDTO2.getLatitude())))
                        .andExpect(jsonPath("eventsCaused[1].city.longitude", is(cityDTO2.getLongitude())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].city.province.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].city.province.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].city.province.name", is(provinceDTO2.getName())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.links[0].href", is(pathToCountryLink2)))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.id",
                                is(anotherCountryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.name",
                                is(anotherCountryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.region.links[0].href",
                                is(pathToRegionLink2)))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.region.id",
                                is(anotherRegionNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.region.name",
                                is(anotherRegionNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].victim.links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].victim.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].victim.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].victim.totalNumberOfFatalities",
                                is(victimDTO2.getTotalNumberOfFatalities().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].victim.numberOfPerpetratorsFatalities",
                                is(victimDTO2.getNumberOfPerpetratorsFatalities().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].victim.totalNumberOfInjured",
                                is(victimDTO2.getTotalNumberOfInjured().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].victim.numberOfPerpetratorsInjured",
                                is(victimDTO2.getNumberOfPerpetratorsInjured().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].victim.valueOfPropertyDamage",
                                is(victimDTO2.getValueOfPropertyDamage().intValue())))
                        .andExpect(jsonPath("eventsCaused[2]").doesNotExist()));
    }

    @Test
    void when_update_group_with_null_fields_should_return_errors() {

        GroupDTO groupDTO = (GroupDTO) groupBuilder.withName(null).withEventsCaused(null).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("Group name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("List of Events caused by the Group cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(2))));
    }

    @Test
    void when_update_group_with_null_event_fields_should_return_errors() {

        EventDTO eventDTO = (EventDTO) eventBuilder.withId(null).withSummary(null).withMotive(null).withDate(null)
                .withIsPartOfMultipleIncidents(null).withIsSuccessful(null).withIsSuicidal(null)
                .withTarget(null).withCity(null)
                .build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("Event summary cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("Event motive cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("Event date cannot be null.")))
                        .andExpect(jsonPath("errors", hasItem("Event must have information on whether it has been part of many incidents.")))
                        .andExpect(jsonPath("errors", hasItem("Event must have information about whether it was successful.")))
                        .andExpect(jsonPath("errors", hasItem("Event must have information about whether it was a suicidal attack.")))
                        .andExpect(jsonPath("errors", hasItem("Target name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("City name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("Victim data cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(9))));
    }

    @Test
    void when_update_group_with_empty_events_list_should_return_errors() {

        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(new ArrayList<>()).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("List of Events caused by the Group cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: For Group name: {0}")
    @EmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void when_update_group_with_invalid_name_should_return_errors(String invalidName) {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        VictimDTO victimDTO = (VictimDTO) victimBuilder.build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).withVictim(victimDTO)
                .build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withName(invalidName).withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Group name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: Group Target Country: {0}")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void when_update_group_with_not_existing_country_should_return_errors(String invalidCountryName) {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(invalidCountryName).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        VictimDTO victimDTO = (VictimDTO) victimBuilder.build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).withVictim(victimDTO)
                .build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("A country with the provided name does not exist.")))
                        .andExpect(jsonPath("errors", hasSize(2))));
    }

    @ParameterizedTest(name = "{index}: For Group Target: {0}")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void when_update_group_event_with_invalid_target_should_return_errors(String invalidTarget) {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withTarget(invalidTarget).withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        VictimDTO victimDTO = (VictimDTO) victimBuilder.build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).withVictim(victimDTO)
                .build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Target name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: For Group event summary: {0}")
    @EmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void when_update_group_event_with_invalid_summary_should_return_errors(String invalidSummary) {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        VictimDTO victimDTO = (VictimDTO) victimBuilder.build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withSummary(invalidSummary).withTarget(targetDTO).withCity(cityDTO)
                .withVictim(victimDTO).build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Event summary cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: For Group event motive: {0}")
    @EmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void when_update_group_event_with_invalid_motive_should_return_errors(String invalidMotive) {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        VictimDTO victimDTO = (VictimDTO) victimBuilder.build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withMotive(invalidMotive).withTarget(targetDTO).withCity(cityDTO)
                .withVictim(victimDTO).build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Event motive cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_group_event_with_date_in_the_future_should_return_errors() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(2090, Calendar.FEBRUARY, 1);
        Date invalidDate = calendar.getTime();
        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        VictimDTO victimDTO = (VictimDTO) victimBuilder.build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withDate(invalidDate).withTarget(targetDTO).withCity(cityDTO)
                .withVictim(victimDTO).build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Event date cannot be in the future.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: For Group Event City name: {0}")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void when_update_group_event_with_invalid_city_name_should_return_errors(String invalidCityName) {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withName(invalidCityName).withProvince(provinceDTO).build(ObjectType.DTO);
        VictimDTO victimDTO = (VictimDTO) victimBuilder.build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).withVictim(victimDTO)
                .build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("City name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_group_event_with_invalid_geographical_location_of_city_should_return_errors() {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withLatitude(null).withLongitude(null).withProvince(null)
                .build(ObjectType.DTO);
        VictimDTO victimDTO = (VictimDTO) victimBuilder.build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).withVictim(victimDTO)
                .build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("City latitude cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("City longitude cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("Province name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("Province and target should be located in the same country.")))
                        .andExpect(jsonPath("errors", hasSize(4))));
    }

    @Test
    void when_update_group_event_with_too_small_city_latitude_should_return_errors() {

        Double invalidCityLatitude = -91.0;

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withLatitude(invalidCityLatitude).withProvince(provinceDTO)
                .build(ObjectType.DTO);
        VictimDTO victimDTO = (VictimDTO) victimBuilder.build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).withVictim(victimDTO)
                .build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("City latitude must be greater or equal to -90.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_group_event_with_too_big_city_latitude_should_return_errors() {

        Double invalidCityLatitude = 91.0;

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withLatitude(invalidCityLatitude).withProvince(provinceDTO)
                .build(ObjectType.DTO);
        VictimDTO victimDTO = (VictimDTO) victimBuilder.build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).withVictim(victimDTO)
                .build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("City latitude must be less or equal to 90.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_group_event_with_too_small_city_longitude_should_return_errors() {

        Double invalidCityLongitude = -181.0;

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withLongitude(invalidCityLongitude).withProvince(provinceDTO)
                .build(ObjectType.DTO);
        VictimDTO victimDTO = (VictimDTO) victimBuilder.build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).withVictim(victimDTO)
                .build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("City longitude must be greater or equal to -180.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_group_event_with_too_big_city_longitude_should_return_errors() {

        Double invalidCityLongitude = 181.0;

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withLongitude(invalidCityLongitude).withProvince(provinceDTO)
                .build(ObjectType.DTO);
        VictimDTO victimDTO = (VictimDTO) victimBuilder.build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).withVictim(victimDTO)
                .build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("City longitude must be less or equal to 180.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_group_event_with_province_and_target_in_different_countries_should_return_errors() {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        CountryDTO countryDTO2 = (CountryDTO) countryBuilder.withName(anotherCountryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO2).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        VictimDTO victimDTO = (VictimDTO) victimBuilder.build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).withVictim(victimDTO)
                .build(ObjectType.DTO);

        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Province and target should be located in the same country.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: For Group Event Province name: {0}")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void when_update_group_event_with_invalid_province_name_should_return_errors(String invalidProvinceName) {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withName(invalidProvinceName)
                .withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        VictimDTO victimDTO = (VictimDTO) victimBuilder.build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).withVictim(victimDTO)
                .build(ObjectType.DTO);

        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Province name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_group_event_without_province_country_should_return_errors() {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(null).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        VictimDTO victimDTO = (VictimDTO) victimBuilder.build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).withVictim(victimDTO)
                .build(ObjectType.DTO);

        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("Country name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("Province and target should be located in the same country.")))
                        .andExpect(jsonPath("errors", hasSize(2))));
    }

    @Test
    void when_update_group_event_without_total_number_of_fatalities_should_return_errors() {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        VictimDTO victimDTO = (VictimDTO) victimBuilder.withTotalNumberOfFatalities(null).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).withVictim(victimDTO)
                .build(ObjectType.DTO);

        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Event total number of fatalities cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_group_event_with_negative_total_number_of_fatalities_should_return_errors() {

        long negativeTotalNumberOfFatalities = -10L;

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        VictimDTO victimDTO = (VictimDTO) victimBuilder.withTotalNumberOfFatalities(negativeTotalNumberOfFatalities)
                .build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).withVictim(victimDTO)
                .build(ObjectType.DTO);

        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token).content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Event total number of fatalities must be greater or equal to 0.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_group_event_without_number_of_perpetrators_fatalities_should_return_errors() {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        VictimDTO victimDTO = (VictimDTO) victimBuilder.withNumberOfPerpetratorsFatalities(null).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).withVictim(victimDTO)
                .build(ObjectType.DTO);

        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Event number of perpetrators fatalities cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_group_event_with_negative_number_of_perpetrators_fatalities_should_return_errors() {

        long negativeNumberOfPerpetratorsFatalities = -10L;

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        VictimDTO victimDTO = (VictimDTO) victimBuilder
                .withNumberOfPerpetratorsFatalities(negativeNumberOfPerpetratorsFatalities)
                .build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).withVictim(victimDTO)
                .build(ObjectType.DTO);

        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Event number of perpetrators fatalities must be greater or equal to 0.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }


    @Test
    void when_update_group_event_with_number_of_perpetrators_fatalities_bigger_than_total_value_of_fatalities_should_return_errors() {

        long numberOfPerpetratorsFatalities = 20L;
        long totalNumberOfFatalities = 10L;

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        VictimDTO victimDTO = (VictimDTO) victimBuilder.withTotalNumberOfFatalities(totalNumberOfFatalities)
                .withNumberOfPerpetratorsFatalities(numberOfPerpetratorsFatalities).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).withVictim(victimDTO)
                .build(ObjectType.DTO);

        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]",
                                is("Event number of perpetrators fatalities should not exceed the total number of victims.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_group_event_without_total_number_of_injured_should_return_errors() {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        VictimDTO victimDTO = (VictimDTO) victimBuilder.withTotalNumberOfInjured(null).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).withVictim(victimDTO)
                .build(ObjectType.DTO);

        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Event total number of injured cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_group_event_with_negative_total_number_of_injured_should_return_errors() {

        long negativeTotalNumberOfInjured = -10L;

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        VictimDTO victimDTO = (VictimDTO) victimBuilder.withTotalNumberOfInjured(negativeTotalNumberOfInjured)
                .build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).withVictim(victimDTO)
                .build(ObjectType.DTO);

        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Event total number of injured must be greater or equal to 0.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_group_event_without_number_of_perpetrators_injured_should_return_errors() {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        VictimDTO victimDTO = (VictimDTO) victimBuilder.withNumberOfPerpetratorsInjured(null).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).withVictim(victimDTO)
                .build(ObjectType.DTO);

        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Event number of perpetrators injured cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_group_event_with_number_of_perpetrators_injured_bigger_than_total_value_of_injured_should_return_errors() {

        long numberOfPerpetratorsInjured = 20L;
        long totalNumberOfInjured = 10L;

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        VictimDTO victimDTO = (VictimDTO) victimBuilder.withTotalNumberOfInjured(totalNumberOfInjured)
                .withNumberOfPerpetratorsInjured(numberOfPerpetratorsInjured).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).withVictim(victimDTO)
                .build(ObjectType.DTO);

        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]",
                                is("Event number of perpetrators injured should not exceed the total number of injured.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_group_event_with_negative_number_of_perpetrators_injured_should_return_errors() {

        long negativeNumberOfPerpetratorsInjured = -10L;

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        VictimDTO victimDTO = (VictimDTO) victimBuilder
                .withNumberOfPerpetratorsInjured(negativeNumberOfPerpetratorsInjured)
                .build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).withVictim(victimDTO)
                .build(ObjectType.DTO);

        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Event number of perpetrators injured must be greater or equal to 0.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_group_event_without_value_of_property_damage_should_return_errors() {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        VictimDTO victimDTO = (VictimDTO) victimBuilder.withValueOfPropertyDamage(null).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).withVictim(victimDTO)
                .build(ObjectType.DTO);

        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Event total value of property damage cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_group_event_with_negative_value_of_property_damage_should_return_errors() {

        long negativeValueOfPropertyDamage = -100L;

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        VictimDTO victimDTO = (VictimDTO) victimBuilder.withValueOfPropertyDamage(negativeValueOfPropertyDamage)
                .build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).withVictim(victimDTO)
                .build(ObjectType.DTO);

        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Event total value of property damage must be greater or equal to 0.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }
}
