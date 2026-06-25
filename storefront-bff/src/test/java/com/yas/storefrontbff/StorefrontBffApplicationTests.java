package com.yas.storefrontbff;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import java.util.Map;

import com.yas.storefrontbff.viewmodel.AuthenticatedUserVm;
import com.yas.storefrontbff.viewmodel.AuthenticationInfoVm;
import com.yas.storefrontbff.viewmodel.CartDetailVm;
import com.yas.storefrontbff.viewmodel.CartGetDetailVm;
import com.yas.storefrontbff.viewmodel.CartItemVm;
import com.yas.storefrontbff.viewmodel.GuestUserVm;
import com.yas.storefrontbff.viewmodel.TokenResponseVm;
import com.yas.storefrontbff.config.ServiceUrlConfig;

class StorefrontBffApplicationTests {

    @Test
    void testViewModels() {
        AuthenticatedUserVm authUser = new AuthenticatedUserVm("testuser");
        assertThat(authUser.username()).isEqualTo("testuser");

        AuthenticationInfoVm authInfo = new AuthenticationInfoVm(true, authUser);
        assertThat(authInfo.isAuthenticated()).isTrue();
        assertThat(authInfo.authenticatedUser()).isEqualTo(authUser);

        CartDetailVm cartDetail = new CartDetailVm(1L, 2L, 3);
        assertThat(cartDetail.id()).isEqualTo(1L);
        assertThat(cartDetail.productId()).isEqualTo(2L);
        assertThat(cartDetail.quantity()).isEqualTo(3);

        CartGetDetailVm cartGetDetail = new CartGetDetailVm(1L, "customer1", List.of(cartDetail));
        assertThat(cartGetDetail.id()).isEqualTo(1L);
        assertThat(cartGetDetail.customerId()).isEqualTo("customer1");
        assertThat(cartGetDetail.cartDetails()).hasSize(1);

        CartItemVm cartItem = new CartItemVm(1L, 2);
        assertThat(cartItem.productId()).isEqualTo(1L);
        assertThat(cartItem.quantity()).isEqualTo(2);

        GuestUserVm guestUser = new GuestUserVm("user1", "email@test.com", "pass");
        assertThat(guestUser.userId()).isEqualTo("user1");
        assertThat(guestUser.email()).isEqualTo("email@test.com");
        assertThat(guestUser.password()).isEqualTo("pass");

        TokenResponseVm tokenResponse = new TokenResponseVm("access", "refresh");
        assertThat(tokenResponse.accessToken()).isEqualTo("access");
        assertThat(tokenResponse.refreshToken()).isEqualTo("refresh");
        
        ServiceUrlConfig serviceUrlConfig = new ServiceUrlConfig(Map.of("service", "url"));
        assertThat(serviceUrlConfig.services()).containsKey("service");
    }

}
