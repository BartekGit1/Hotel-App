package dto;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class ChangePasswordDto {

    @NotNull
    @NotEmpty
    private String oldPassword;

    @NotNull
    @NotEmpty
    private String newPassword;
}
