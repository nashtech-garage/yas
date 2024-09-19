package com.yas.customer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.customer.config.IntegrationTestConfiguration;
import com.yas.commonlibrary.exception.AccessDeniedException;
import com.yas.customer.model.UserAddress;
import com.yas.customer.repository.UserAddressRepository;
import com.yas.customer.util.SecurityContextUtils;
import com.yas.customer.utils.Constants;
import com.yas.customer.viewmodel.address.ActiveAddressVm;
import com.yas.customer.viewmodel.address.AddressDetailVm;
import com.yas.customer.viewmodel.address.AddressPostVm;
import com.yas.customer.viewmodel.address.AddressVm;
import com.yas.customer.viewmodel.useraddress.UserAddressVm;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(IntegrationTestConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserAddressServiceIT {

    private static final String USER_ID_1 = "user1";

    private static final String USER_ID_2 = "user2";

    @Autowired
    private UserAddressRepository userAddressRepository;

    @MockBean
    private LocationService locationService;

    @Autowired
    private UserAddressService userAddressService;

    @BeforeEach
    void setUp() {
        List<UserAddress> userAddressList = new ArrayList<>();
        userAddressList.add(createUserAddress(1L, USER_ID_1, 101L, true));
        userAddressList.add(createUserAddress(2L, USER_ID_2, 102L, false));
        userAddressList.add(createUserAddress(3L, USER_ID_2, 103L, true));
        userAddressRepository.saveAll(userAddressList);
    }

    @AfterEach
    void clearTestData() {
        userAddressRepository.deleteAll();
    }

    @Test
    void testGetUserAddressList_whenUserIsNotAuthenticated_throwAccessDeniedException() {

        SecurityContextUtils.setUpSecurityContext("anonymousUser");

        Exception exception = assertThrows(AccessDeniedException.class, () -> {
            userAddressService.getUserAddressList();
        });
        assertThat(exception.getMessage()).isEqualTo(Constants.ErrorCode.UNAUTHENTICATED);
    }

    @Test
    void testGetUserAddressList_whenUserAddressReturnEmpty_returnListIsEmpty() {

        SecurityContextUtils.setUpSecurityContext("test-user");
        List<ActiveAddressVm> result = userAddressService.getUserAddressList();
        assertThat(result).isEmpty();

    }

    @Test
    void testGetUserAddressList_whenAddressDetailVmReturnEmpty_returnListIsEmpty() {

        SecurityContextUtils.setUpSecurityContext(USER_ID_1);
        ArgumentCaptor<List<Long>> argumentCaptor = ArgumentCaptor.forClass(List.class);

        when(locationService.getAddressesByIdList(argumentCaptor.capture())).thenReturn(List.of());
        List<ActiveAddressVm> result = userAddressService.getUserAddressList();
        assertThat(result).isEmpty();
        List<Long> addressId = argumentCaptor.getValue();
        assertThat(addressId.getFirst()).isEqualTo(101L);
    }

    @Test
    void testGetUserAddressList_whenAddressDetailVmNotEmpty_returnActiveAddressVms() {

        List<AddressDetailVm> addressVmList = getAddressDetailVms();
        SecurityContextUtils.setUpSecurityContext(USER_ID_2);
        when(locationService.getAddressesByIdList(any(List.class)))
            .thenReturn(addressVmList);

        List<ActiveAddressVm> result = userAddressService.getUserAddressList();

        assertThat(result).hasSize(2);
        assertThat(result.getFirst().id()).isEqualTo(103L);
        assertThat(result.getLast().id()).isEqualTo(102L);
    }

    @Test
    void testGetAddressDefault_whenUserIsNotAuthenticated_throwAccessDeniedException() {

        SecurityContextUtils.setUpSecurityContext("anonymousUser");

        Exception exception = assertThrows(AccessDeniedException.class, () -> {
            userAddressService.getAddressDefault();
        });
        assertThat(exception.getMessage()).isEqualTo(Constants.ErrorCode.UNAUTHENTICATED);
    }

    @Test
    void testGetAddressDefault_whenNotFoundUserAddress_throwNotFoundException() {

        SecurityContextUtils.setUpSecurityContext(USER_ID_1);

        userAddressRepository.deleteAll();
        Exception exception = assertThrows(NotFoundException.class, () -> {
            userAddressService.getAddressDefault();
        });
        assertThat(exception.getMessage()).isEqualTo("User address not found");
    }

    @Test
    void testGetAddressDefault_whenAddressDetailVmNotEmpty_returnActiveAddressVm() {

        AddressDetailVm addressVm = getAddressDetailVms().getLast();
        SecurityContextUtils.setUpSecurityContext(USER_ID_2);
        when(locationService.getAddressById(103L))
            .thenReturn(addressVm);

        AddressDetailVm result = userAddressService.getAddressDefault();

        assertThat(result.id()).isEqualTo(103L);
    }

    @Test
    void testCreateAddress_whenNormalCase_createAddressSuccess() {

        Long addressId = 123L;
        AddressPostVm addressPost = getAddressPostVm();

        SecurityContextUtils.setUpSecurityContext(USER_ID_1);
        when(locationService.createAddress(addressPost))
            .thenReturn(AddressVm.builder().id(addressId).build());

        UserAddressVm userAddressVm = userAddressService.createAddress(addressPost);

        assertThat(userAddressVm.userId()).isEqualTo(USER_ID_1);
        assertThat(userAddressVm.addressGetVm().id()).isEqualTo(addressId);
        assertThat(userAddressVm.isActive()).isFalse();
    }

    @Test
    void testDeleteAddress_whenNormalCase_deleteSuccess() {

        SecurityContextUtils.setUpSecurityContext(USER_ID_1);
        userAddressService.deleteAddress(101L);

        UserAddress userAddress = userAddressRepository.findOneByUserIdAndAddressId(USER_ID_1, 101L);
        assertThat(userAddress).isNull();
    }

    @Test
    void testDeleteAddress_whenUserAddressIsNull_throwNotFoundException() {
        SecurityContextUtils.setUpSecurityContext(USER_ID_1);
        Exception exception = assertThrows(NotFoundException.class, () -> {
            userAddressService.deleteAddress(102L);
        });
        assertThat(exception.getMessage()).isEqualTo("User address not found");
    }

    @Test
    void testChooseDefaultAddress_whenNormalCase_chooseDefaultAddressSuccess() {

        SecurityContextUtils.setUpSecurityContext(USER_ID_2);
        userAddressService.chooseDefaultAddress(102L);

        List<UserAddress> userAddressList = userAddressRepository.findAllByUserId(USER_ID_2);

        UserAddress userAddress1 = userAddressList.getFirst();
        UserAddress userAddress2 = userAddressList.getLast();
        assertThat(userAddress1.getAddressId()).isEqualTo(102L);
        assertThat(userAddress1.getIsActive()).isTrue();

        assertThat(userAddress2.getAddressId()).isEqualTo(103L);
        assertThat(userAddress2.getIsActive()).isFalse();
    }

    private AddressPostVm getAddressPostVm() {
        return new AddressPostVm(
            "Alice Johnson",
            "555-555-5555",
            "789 Maple Road",
            "Springfield",
            "54321",
            3L,
            2L,
            1L
        );
    }

    private UserAddress createUserAddress(Long id, String userId, Long addressId, Boolean isActive) {
        UserAddress userAddress = new UserAddress();
        userAddress.setId(id);
        userAddress.setUserId(userId);
        userAddress.setAddressId(addressId);
        userAddress.setIsActive(isActive);
        return userAddress;
    }

    private List<AddressDetailVm> getAddressDetailVms() {
        return List.of(
            AddressDetailVm.builder()
                .id(102L)
                .contactName("John Doe")
                .phone("123-456-7890")
                .addressLine1("123 Elm Street")
                .city("Springfield")
                .zipCode("12345")
                .districtId(1L)
                .districtName("District A")
                .stateOrProvinceId(1L)
                .stateOrProvinceName("State A")
                .countryId(1L)
                .countryName("Country A")
                .build(),

            AddressDetailVm.builder()
                .id(103L)
                .contactName("Jane Smith")
                .phone("987-654-3210")
                .addressLine1("456 Oak Avenue")
                .city("Springfield")
                .zipCode("67890")
                .districtId(2L)
                .districtName("District B")
                .stateOrProvinceId(2L)
                .stateOrProvinceName("State B")
                .countryId(2L)
                .countryName("Country B")
                .build()
        );
    }
}