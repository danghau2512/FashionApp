package com.example.fashionshop.service;

import com.example.fashionshop.dto.AddressRequest;
import com.example.fashionshop.dto.AddressResponse;
import com.example.fashionshop.entity.User;
import com.example.fashionshop.entity.UserAddress;
import com.example.fashionshop.repository.UserAddressRepository;
import com.example.fashionshop.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserAddressService {

    private final UserAddressRepository addressRepository;
    private final UserRepository userRepository;

    public UserAddressService(UserAddressRepository addressRepository,
                              UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public AddressResponse createAddress(AddressRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        boolean isFirstAddress = addressRepository.countByUser_Id(user.getId()) == 0;
        boolean shouldSetDefault = isFirstAddress || Boolean.TRUE.equals(request.getDefaultAddress());

        if (shouldSetDefault) {
            addressRepository.clearDefaultAddressByUserId(user.getId());
        }

        UserAddress address = new UserAddress();
        address.setUser(user);
        address.setReceiverName(request.getReceiverName());
        address.setReceiverPhone(request.getReceiverPhone());
        address.setAddressDetail(request.getAddressDetail());
        address.setWard(request.getWard());
        address.setDistrict(request.getDistrict());
        address.setProvince(request.getProvince());
        address.setProvinceId(request.getProvinceId());
        address.setDistrictId(request.getDistrictId());
        address.setWardCode(request.getWardCode());
        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());
        address.setDefaultAddress(shouldSetDefault);

        UserAddress savedAddress = addressRepository.save(address);

        return toAddressResponse(savedAddress);
    }

    public List<AddressResponse> getAddressesByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Không tìm thấy người dùng");
        }

        return addressRepository.findByUser_IdOrderByDefaultAddressDescCreatedAtDesc(userId)
                .stream()
                .map(this::toAddressResponse)
                .toList();
    }

    @Transactional
    public AddressResponse updateAddress(Long addressId, AddressRequest request) {
        UserAddress address = addressRepository.findByIdAndUser_Id(addressId, request.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ của người dùng"));

        boolean shouldSetDefault = Boolean.TRUE.equals(request.getDefaultAddress());

        if (shouldSetDefault) {
            addressRepository.clearDefaultAddressByUserId(request.getUserId());
        }

        address.setReceiverName(request.getReceiverName());
        address.setReceiverPhone(request.getReceiverPhone());
        address.setAddressDetail(request.getAddressDetail());
        address.setWard(request.getWard());
        address.setDistrict(request.getDistrict());
        address.setProvince(request.getProvince());
        address.setProvinceId(request.getProvinceId());
        address.setDistrictId(request.getDistrictId());
        address.setWardCode(request.getWardCode());
        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());

        if (shouldSetDefault) {
            address.setDefaultAddress(true);
        }

        UserAddress savedAddress = addressRepository.save(address);

        return toAddressResponse(savedAddress);
    }

    @Transactional
    public void deleteAddress(Long addressId, Long userId) {
        UserAddress address = addressRepository.findByIdAndUser_Id(addressId, userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ của người dùng"));

        boolean wasDefault = Boolean.TRUE.equals(address.getDefaultAddress());

        addressRepository.delete(address);

        if (wasDefault) {
            List<UserAddress> remainingAddresses =
                    addressRepository.findByUser_IdOrderByDefaultAddressDescCreatedAtDesc(userId);

            if (!remainingAddresses.isEmpty()) {
                UserAddress newDefaultAddress = remainingAddresses.get(0);
                newDefaultAddress.setDefaultAddress(true);
                addressRepository.save(newDefaultAddress);
            }
        }
    }

    @Transactional
    public AddressResponse setDefaultAddress(Long addressId, Long userId) {
        UserAddress address = addressRepository.findByIdAndUser_Id(addressId, userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ của người dùng"));

        addressRepository.clearDefaultAddressByUserId(userId);

        address.setDefaultAddress(true);

        UserAddress savedAddress = addressRepository.save(address);

        return toAddressResponse(savedAddress);
    }

    private AddressResponse toAddressResponse(UserAddress address) {
        return new AddressResponse(
                address.getId(),
                address.getUser().getId(),
                address.getReceiverName(),
                address.getReceiverPhone(),
                address.getAddressDetail(),
                address.getWard(),
                address.getDistrict(),
                address.getProvince(),
                address.getProvinceId(),
                address.getDistrictId(),
                address.getWardCode(),
                address.getLatitude(),
                address.getLongitude(),
                address.getDefaultAddress()
        );
    }
}