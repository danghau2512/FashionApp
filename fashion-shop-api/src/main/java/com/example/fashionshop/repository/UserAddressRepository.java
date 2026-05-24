package com.example.fashionshop.repository;

import com.example.fashionshop.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {

    List<UserAddress> findByUser_IdOrderByDefaultAddressDescCreatedAtDesc(Long userId);

    Optional<UserAddress> findByIdAndUser_Id(Long id, Long userId);

    long countByUser_Id(Long userId);

    @Modifying
    @Query("UPDATE UserAddress a SET a.defaultAddress = false WHERE a.user.id = :userId")
    void clearDefaultAddressByUserId(@Param("userId") Long userId);
}