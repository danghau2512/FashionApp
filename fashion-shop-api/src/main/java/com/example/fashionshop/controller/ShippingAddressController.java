package com.example.fashionshop.controller;

import com.example.fashionshop.integration.ghn.dto.GhnDistrict;
import com.example.fashionshop.integration.ghn.dto.GhnProvince;
import com.example.fashionshop.integration.ghn.dto.GhnWard;
import com.example.fashionshop.integration.ghn.service.GhnAddressService;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/shipping")
@CrossOrigin("*")
public class ShippingAddressController {

    private final GhnAddressService ghnAddressService;

    public ShippingAddressController(
            GhnAddressService ghnAddressService
    ) {
        this.ghnAddressService = ghnAddressService;
    }

    @GetMapping("/provinces")
    public List<GhnProvince> getProvinces() {
        return ghnAddressService.getProvinces();
    }

    @GetMapping("/districts")
    public List<GhnDistrict> getDistricts(
            @RequestParam Integer provinceId
    ) {
        return ghnAddressService.getDistricts(provinceId);
    }

    @GetMapping("/wards")
    public List<GhnWard> getWards(
            @RequestParam Integer districtId
    ) {
        return ghnAddressService.getWards(districtId);
    }
}