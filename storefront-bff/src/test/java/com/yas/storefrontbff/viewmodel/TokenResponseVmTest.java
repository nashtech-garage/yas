package com.yas.storefrontbff.viewmodel;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TokenResponseVmTest {

    @Test
    void accessToken_shouldReturnValuePassedToConstructor() {
        TokenResponseVm vm = new TokenResponseVm("access-abc", "refresh-xyz");
        assertThat(vm.accessToken()).isEqualTo("access-abc");
    }

    @Test
    void refreshToken_shouldReturnValuePassedToConstructor() {
        TokenResponseVm vm = new TokenResponseVm("access-abc", "refresh-xyz");
        assertThat(vm.refreshToken()).isEqualTo("refresh-xyz");
    }

    @Test
    void accessToken_shouldAllowNull() {
        TokenResponseVm vm = new TokenResponseVm(null, "refresh");
        assertThat(vm.accessToken()).isNull();
    }

    @Test
    void refreshToken_shouldAllowNull() {
        TokenResponseVm vm = new TokenResponseVm("access", null);
        assertThat(vm.refreshToken()).isNull();
    }

    @Test
    void bothTokens_shouldAllowNull() {
        TokenResponseVm vm = new TokenResponseVm(null, null);
        assertThat(vm.accessToken()).isNull();
        assertThat(vm.refreshToken()).isNull();
    }

    @Test
    void accessToken_shouldAllowJwtFormat() {
        String jwt = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJ1c2VyIn0.signature";
        TokenResponseVm vm = new TokenResponseVm(jwt, "refresh");
        assertThat(vm.accessToken()).isEqualTo(jwt);
    }

    @Test
    void equals_shouldReturnTrue_forSameValues() {
        assertThat(new TokenResponseVm("a", "b")).isEqualTo(new TokenResponseVm("a", "b"));
    }

    @Test
    void equals_shouldReturnFalse_whenAccessTokenDiffers() {
        assertThat(new TokenResponseVm("a", "b")).isNotEqualTo(new TokenResponseVm("c", "b"));
    }

    @Test
    void equals_shouldReturnFalse_whenRefreshTokenDiffers() {
        assertThat(new TokenResponseVm("a", "b")).isNotEqualTo(new TokenResponseVm("a", "c"));
    }

    @Test
    void equals_shouldReturnFalse_whenComparedToNull() {
        assertThat(new TokenResponseVm("a", "b")).isNotEqualTo(null);
    }

    @Test
    void equals_shouldReturnFalse_whenComparedToDifferentType() {
        assertThat(new TokenResponseVm("a", "b")).isNotEqualTo("a");
    }

    @Test
    void equals_shouldReturnTrue_forSameInstance() {
        TokenResponseVm vm = new TokenResponseVm("a", "b");
        assertThat(vm).isEqualTo(vm);
    }

    @Test
    void equals_shouldReturnTrue_forBothNullTokens() {
        assertThat(new TokenResponseVm(null, null)).isEqualTo(new TokenResponseVm(null, null));
    }

    @Test
    void hashCode_shouldBeEqual_forSameValues() {
        assertThat(new TokenResponseVm("a", "b").hashCode())
                .isEqualTo(new TokenResponseVm("a", "b").hashCode());
    }

    @Test
    void hashCode_shouldDiffer_whenAccessTokenDiffers() {
        assertThat(new TokenResponseVm("a", "b").hashCode())
                .isNotEqualTo(new TokenResponseVm("x", "b").hashCode());
    }

    @Test
    void hashCode_shouldDiffer_whenRefreshTokenDiffers() {
        assertThat(new TokenResponseVm("a", "b").hashCode())
                .isNotEqualTo(new TokenResponseVm("a", "y").hashCode());
    }

    @Test
    void toString_shouldContainRecordName() {
        assertThat(new TokenResponseVm("a", "b").toString()).contains("TokenResponseVm");
    }

    @Test
    void toString_shouldContainAccessToken() {
        assertThat(new TokenResponseVm("my-access-token", "r").toString())
                .contains("my-access-token");
    }

    @Test
    void toString_shouldContainRefreshToken() {
        assertThat(new TokenResponseVm("a", "my-refresh-token").toString())
                .contains("my-refresh-token");
    }
}
