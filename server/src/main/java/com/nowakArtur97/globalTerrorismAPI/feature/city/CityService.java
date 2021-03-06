package com.nowakArtur97.globalTerrorismAPI.feature.city;

import com.nowakArtur97.globalTerrorismAPI.common.service.GenericServiceImpl;
import com.nowakArtur97.globalTerrorismAPI.feature.province.ProvinceDTO;
import com.nowakArtur97.globalTerrorismAPI.feature.province.ProvinceNode;
import com.nowakArtur97.globalTerrorismAPI.feature.province.ProvinceService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CityService extends GenericServiceImpl<CityNode, CityDTO> {

    private final int DEFAULT_DEPTH_FOR_CITY_NODE = 2;

    private final CityRepository repository;

    private final ProvinceService provinceService;

    CityService(CityRepository repository, ModelMapper modelMapper, ProvinceService provinceService) {
        super(repository, modelMapper);
        this.repository = repository;
        this.provinceService = provinceService;
    }

    @Override
    public CityNode save(CityNode cityNode) {

        ProvinceNode provinceNode = cityNode.getProvince();

        Optional<ProvinceNode> provinceNodeOptional = provinceService
                .findByNameAndCountryName(provinceNode.getName(), provinceNode.getCountry().getName());

        if (provinceNodeOptional.isPresent()) {
            cityNode.setProvince(provinceNodeOptional.get());
        } else {
            cityNode.setProvince(provinceService.save(cityNode.getProvince()));
        }

        return repository.save(cityNode);
    }

    @Override
    public CityNode saveNew(CityDTO cityDTO) {

        CityNode cityNode = modelMapper.map(cityDTO, CityNode.class);

        ProvinceDTO provinceDTO = cityDTO.getProvince();

        Optional<ProvinceNode> provinceNodeOptional = provinceService
                .findByNameAndCountryName(provinceDTO.getName(), provinceDTO.getCountry().getName());

        if (provinceNodeOptional.isPresent()) {
            cityNode.setProvince(provinceNodeOptional.get());
        } else {
            cityNode.setProvince(provinceService.saveNew(cityDTO.getProvince()));
        }

        return repository.save(cityNode);
    }

    @Override
    public CityNode update(CityNode cityNode, CityDTO cityDTO) {

        Long id = cityNode.getId();

        ProvinceDTO provinceDTO = cityDTO.getProvince();

        Optional<ProvinceNode> provinceNodeOptional = provinceService
                .findByNameAndCountryName(provinceDTO.getName(), provinceDTO.getCountry().getName());

        ProvinceNode updatedProvince;

        if (provinceNodeOptional.isPresent()) {
            updatedProvince = provinceNodeOptional.get();
        } else {
            updatedProvince = provinceService.update(cityNode.getProvince(), cityDTO.getProvince());
        }

        cityNode = modelMapper.map(cityDTO, CityNode.class);

        cityNode.setId(id);
        cityNode.setProvince(updatedProvince);

        return repository.save(cityNode);
    }

    public Optional<CityNode> findByNameAndLatitudeAndLongitude(String name, Double latitude, Double longitude) {

        return repository.findByNameAndLatitudeAndLongitude(name, latitude, longitude, DEFAULT_DEPTH_FOR_CITY_NODE);
    }
}
