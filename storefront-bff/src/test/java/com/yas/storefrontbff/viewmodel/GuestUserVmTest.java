package com.yas.storefrontbff.viewmodel;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GuestUserVmTest {

    @Test
    void userId_shouldReturnValuePassedToConstructor() {
        GuestUserVm vm = new GuestUserVm("user-1", "guest@example.com", "secret");
        assertThat(vm.userId()).isEqualTo("user-1");
    }

    @Test
    void email_shouldReturnValuePassedToConstructor() {
        GuestUserVm vm = new GuestUserVm("user-1", "guest@example.com", "secret");
        assertThat(vm.email()).isEqualTo("guest@example.com");
    }

    @Test
    void password_shouldReturnValuePassedToConstructor() {
        GuestUserVm vm = new GuestUserVm("user-1", "guest@example.com", "secret");
        assertThat(vm.password()).isEqualTo("secret");
    }

    @Test
    void userId_shouldAllowNull() {
        GuestUserVm vm = new GuestUserVm(null, "e@e.com", "pwd");
        assertThat(vm.userId()).isNull();
    }

    @Test
    void email_shouldAllowNull() {
        GuestUserVm vm = new GuestUserVm("id", null, "pwd");
        assertThat(vm.email()).isNull();
    }

    @Test
    void password_shouldAllowNull() {
        GuestUserVm vm = new GuestUserVm("id", "e@e.com", null);
        assertThat(vm.password()).isNull();
    }

    @Test
    void allFields_shouldAllowNull() {
        GuestUserVm vm = new GuestUserVm(null, null, null);
        assertThat(vm.userId()).isNull();
        assertThat(vm.email()).isNull();
        assertThat(vm.password()).isNull();
    }

    @Test
    void email_shouldAllowSpecialCharacters() {
        GuestUserVm vm = new GuestUserVm("id", "user+tag@sub.domain.com", "pwd");
        assertThat(vm.email()).isEqualTo("user+tag@sub.domain.com");
    }

    @Test
    void equals_shouldReturnTrue_forSameValues() {
        assertThat(new GuestUserVm("id", "email", "pwd"))
                .isEqualTo(new GuestUserVm("id", "email", "pwd"));
    }

    @Test
    void equals_shouldReturnFalse_whenUserIdDiffers() {
        assertThat(new GuestUserVm("id1", "email", "pwd"))
                .isNotEqualTo(new GuestUserVm("id2", "email", "pwd"));
    }

    @Test
    void equals_shouldReturnFalse_whenEmailDiffers() {
        assertThat(new GuestUserVm("id", "email1@e.com", "pwd"))
                .isNotEqualTo(new GuestUserVm("id", "email2@e.com", "pwd"));
    }

    @Test
    void equals_shouldReturnFalse_whenPasswordDiffers() {
        assertThat(new GuestUserVm("id", "email", "pwd1"))
                .isNotEqualTo(new GuestUserVm("id", "email", "pwd2"));
    }

    @Test
    void equals_shouldReturnFalse_whenComparedToNull() {
        assertThat(new GuestUserVm("id", "email", "pwd")).isNotEqualTo(null);
    }

    @Test
    void equals_shouldReturnFalse_whenComparedToDifferentType() {
        assertThat(new GuestUserVm("id", "email", "pwd")).isNotEqualTo("string");
    }

    @Test
    void equals_shouldReturnTrue_forSameInstance() {
        GuestUserVm vm = new GuestUserVm("id", "email", "pwd");
        assertThat(vm).isEqualTo(vm);
    }

    @Test
    void hashCode_shouldBeEqual_forSameValues() {
        assertThat(new GuestUserVm("id", "email", "pwd").hashCode())
                .isEqualTo(new GuestUserVm("id", "email", "pwd").hashCode());
    }

    @Test
    void hashCode_shouldDiffer_forDifferentValues() {
        assertThat(new GuestUserVm("id1", "email", "pwd").hashCode())
                .isNotEqualTo(new GuestUserVm("id2", "email", "pwd").hashCode());
    }

    @Test
    void toString_shouldContainRecordName() {
        assertThat(new GuestUserVm("id", "e@e.com", "pwd").toString())
                .contains("GuestUserVm");
    }

    @Test
    void toString_shouldContainUserId() {
        assertThat(new GuestUserVm("user-xyz", "e@e.com", "pwd").toString())
                .contains("user-xyz");
    }

    @Test
    void toString_shouldContainEmail() {
        assertThat(new GuestUserVm("id", "my@email.com", "pwd").toString())
                .contains("my@email.com");
    }
}