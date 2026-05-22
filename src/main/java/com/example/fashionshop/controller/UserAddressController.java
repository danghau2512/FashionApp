package com.example.fashionshop.controller;

import com.example.fashionshop.dto.AddressRequest;
import com.example.fashionshop.dto.AddressResponse;
import com.example.fashionshop.service.UserAddressService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@CrossOrigin("*")
public class UserAddressController {

    private final UserAddressService addressService;

    public UserAddressController(UserAddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping
    public AddressResponse createAddress(@Valid @RequestBody AddressRequest request) {
        return addressService.createAddress(request);
    }

    @GetMapping("/user/{userId}")
    public List<AddressResponse> getAddressesByUserId(@PathVariable Long userId) {
        return addressService.getAddressesByUserId(userId);
    }

    @PutMapping("/{addressId}")
    public AddressResponse updateAddress(@PathVariable Long addressId,
                                         @Valid @RequestBody AddressRequest request) {
        return addressService.updateAddress(addressId, request);
    }

    @DeleteMapping("/{addressId}")
    public void deleteAddress(@PathVariable Long addressId,
                              @RequestParam Long userId) {
        addressService.deleteAddress(addressId, userId);
    }

    @PutMapping("/{addressId}/default")
    public AddressResponse setDefaultAddress(@PathVariable Long addressId,
                                             @RequestParam Long userId) {
        return addressService.setDefaultAddress(addressId, userId);
    }
}