package com.example.fashionshop.controller;

import com.example.fashionshop.dto.ShippingQuoteRequest;
import com.example.fashionshop.dto.ShippingQuoteResponse;
import com.example.fashionshop.integration.ghn.dto.GhnDistrict;
import com.example.fashionshop.integration.ghn.dto.GhnProvince;
import com.example.fashionshop.integration.ghn.dto.GhnWard;
import com.example.fashionshop.integration.ghn.service.GhnAddressService;

import com.example.fashionshop.integration.ghn.service.GhnShippingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipping")
@CrossOrigin("*")
public class ShippingAddressController {

    private final GhnAddressService ghnAddressService;
    private final GhnShippingService ghnShippingService;
    public ShippingAddressController(GhnAddressService ghnAddressService, GhnShippingService ghnShippingService) {
        this.ghnAddressService = ghnAddressService;
        this.ghnShippingService = ghnShippingService;
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
    @PostMapping("/quote")
        public ShippingQuoteResponse getShippingQuote(
            @Valid @RequestBody ShippingQuoteRequest request
    ) {
        return ghnShippingService.getQuote(request);
    }
}