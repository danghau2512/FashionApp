package com.example.fashionshop.integration.ghn.service;

import com.example.fashionshop.integration.ghn.client.GhnClient;
import com.example.fashionshop.integration.ghn.dto.GhnDistrict;
import com.example.fashionshop.integration.ghn.dto.GhnProvince;
import com.example.fashionshop.integration.ghn.dto.GhnWard;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GhnAddressService {

    private final GhnClient ghnClient;

    public GhnAddressService(GhnClient ghnClient) {
        this.ghnClient = ghnClient;
    }

    public List<GhnProvince> getProvinces() {
        List<GhnProvince> provinces = ghnClient.getProvinces();
        List<GhnProvince> activeProvinces = new ArrayList<>();

        for (GhnProvince province : provinces) {
            if (province.getStatus() == null
                    || province.getStatus() == 1) {
                activeProvinces.add(province);
            }
        }

        return activeProvinces;
    }

    public List<GhnDistrict> getDistricts(Integer provinceId) {
        List<GhnDistrict> districts =
                ghnClient.getDistricts(provinceId);

        List<GhnDistrict> activeDistricts = new ArrayList<>();

        for (GhnDistrict district : districts) {
            if (district.getStatus() == null
                    || district.getStatus() == 1) {
                activeDistricts.add(district);
            }
        }

        return activeDistricts;
    }

    public List<GhnWard> getWards(Integer districtId) {
        List<GhnWard> wards = ghnClient.getWards(districtId);
        List<GhnWard> activeWards = new ArrayList<>();

        for (GhnWard ward : wards) {
            if (ward.getStatus() == null
                    || ward.getStatus() == 1) {
                activeWards.add(ward);
            }
        }

        return activeWards;
    }
}