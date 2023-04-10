package dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {

    @NotNull
    @NonNull
    private String username;

    @NotNull
    @NonNull
    private String password;
}
