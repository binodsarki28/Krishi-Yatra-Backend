package com.krishiYatra.krishiYatra.Service;

import com.krishiYatra.krishiYatra.address.AddressEntity;
import com.krishiYatra.krishiYatra.address.AddressRepo;
import com.krishiYatra.krishiYatra.address.AddressService;
import com.krishiYatra.krishiYatra.address.dto.AddressRequest;
import com.krishiYatra.krishiYatra.address.dto.AddressResponse;
import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.user.UserConst;
import com.krishiYatra.krishiYatra.user.UserEntity;
import com.krishiYatra.krishiYatra.utils.UserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddressServiceTest {

    @Mock
    private AddressRepo addressRepo;

    @InjectMocks
    private AddressService addressService;

    private UserEntity mockUser;
    private AddressEntity mockAddress;
    private AddressRequest mockRequest;

    @BeforeEach
    void setUp() {
        mockUser = new UserEntity();
        mockUser.setUserId(UUID.randomUUID().toString());
        mockUser.setUsername("testuser");

        mockAddress = new AddressEntity();
        mockAddress.setAddressId("addr-1");
        mockAddress.setUser(mockUser);
        mockAddress.setProvince("Bagmati");
        mockAddress.setDistrict("Kathmandu");
        mockAddress.setMunicipality("KMC");
        mockAddress.setWardNo(10);
        mockAddress.setStreetName("Baneshwor");

        mockRequest = new AddressRequest();
        mockRequest.setProvince("Bagmati");
        mockRequest.setDistrict("Kathmandu");
        mockRequest.setMunicipality("KMC");
        mockRequest.setWardNo(10);
        mockRequest.setStreetName("Baneshwor");
    }

    @Test
    void saveOrUpdateAddress_New_Success() {
        try (MockedStatic<UserUtil> utilities = mockStatic(UserUtil.class)) {
            utilities.when(UserUtil::getCurrentUser).thenReturn(mockUser);

            when(addressRepo.findByUser(any())).thenReturn(Optional.empty());
            when(addressRepo.save(any())).thenReturn(mockAddress);

            ServerResponse response = addressService.saveOrUpdateAddress(mockRequest);

            assertEquals(HttpStatus.OK, response.getHttpStatus());
            assertEquals("Address saved successfully.", response.getMessage());
            assertNotNull(response.getResponse());
            verify(addressRepo).save(any());
        }
    }

    @Test
    void saveOrUpdateAddress_Existing_Success() {
        try (MockedStatic<UserUtil> utilities = mockStatic(UserUtil.class)) {
            utilities.when(UserUtil::getCurrentUser).thenReturn(mockUser);

            when(addressRepo.findByUser(any())).thenReturn(Optional.of(mockAddress));
            when(addressRepo.save(any())).thenReturn(mockAddress);

            ServerResponse response = addressService.saveOrUpdateAddress(mockRequest);

            assertEquals(HttpStatus.OK, response.getHttpStatus());
            assertEquals("Address saved successfully.", response.getMessage());
            verify(addressRepo).save(any());
        }
    }

    @Test
    void saveOrUpdateAddress_UserNotAuthenticated_Failure() {
        try (MockedStatic<UserUtil> utilities = mockStatic(UserUtil.class)) {
            utilities.when(UserUtil::getCurrentUser).thenReturn(null);

            ServerResponse response = addressService.saveOrUpdateAddress(mockRequest);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getHttpStatus());
            assertEquals(UserConst.USER_NOT_FOUND, response.getMessage());
        }
    }

    @Test
    void getMyAddress_Success() {
        try (MockedStatic<UserUtil> utilities = mockStatic(UserUtil.class)) {
            utilities.when(UserUtil::getCurrentUser).thenReturn(mockUser);

            when(addressRepo.findByUser(any())).thenReturn(Optional.of(mockAddress));

            ServerResponse response = addressService.getMyAddress();

            assertEquals(HttpStatus.OK, response.getHttpStatus());
            assertEquals("Address fetched.", response.getMessage());
            assertNotNull(response.getResponse());
            assertInstanceOf(AddressResponse.class, response.getResponse());
        }
    }

    @Test
    void getMyAddress_NotFound_Failure() {
        try (MockedStatic<UserUtil> utilities = mockStatic(UserUtil.class)) {
            utilities.when(UserUtil::getCurrentUser).thenReturn(mockUser);

            when(addressRepo.findByUser(any())).thenReturn(Optional.empty());

            ServerResponse response = addressService.getMyAddress();

            assertEquals(HttpStatus.NOT_FOUND, response.getHttpStatus());
            assertEquals("No address found.", response.getMessage());
        }
    }

    @Test
    void deleteMyAddress_Success() {
        try (MockedStatic<UserUtil> utilities = mockStatic(UserUtil.class)) {
            utilities.when(UserUtil::getCurrentUser).thenReturn(mockUser);

            when(addressRepo.findByUser(any())).thenReturn(Optional.of(mockAddress));
            doNothing().when(addressRepo).deleteByUser(any());

            ServerResponse response = addressService.deleteMyAddress();

            assertEquals(HttpStatus.OK, response.getHttpStatus());
            assertEquals("Address deleted successfully.", response.getMessage());
            verify(addressRepo).deleteByUser(any());
        }
    }

    @Test
    void deleteMyAddress_NotFound_Failure() {
        try (MockedStatic<UserUtil> utilities = mockStatic(UserUtil.class)) {
            utilities.when(UserUtil::getCurrentUser).thenReturn(mockUser);

            when(addressRepo.findByUser(any())).thenReturn(Optional.empty());

            ServerResponse response = addressService.deleteMyAddress();

            assertEquals(HttpStatus.NOT_FOUND, response.getHttpStatus());
            assertEquals("No address found.", response.getMessage());
        }
    }

    @Test
    void getAddressByUser_Success() {
        when(addressRepo.findByUser(any())).thenReturn(Optional.of(mockAddress));

        AddressResponse response = addressService.getAddressByUser(mockUser);

        assertNotNull(response);
        assertEquals("Baneshwor, Ward 10, KMC, Kathmandu, Bagmati", response.getFullAddress());
    }
}
