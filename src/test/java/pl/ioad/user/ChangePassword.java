package pl.ioad.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChangePassword implements Cloneable {

    @JsonProperty("current_password")
    String currentPassword;
    @JsonProperty("new_password")
    String newPassword;
    @JsonProperty("new_password_confirmation")
    String newPasswordConfirmation;

    @Override
    public UserCreate clone() {
        try {
            return (UserCreate) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
