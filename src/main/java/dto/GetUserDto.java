package dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class GetUserDto {

    @NonNull
    @NotEmpty
    @Size(min = 6, max = 20)
    private String username;

    @NonNull
    @NotEmpty
    @Size(min = 3, max = 35)
    private String firstName;

    @NonNull
    @NotEmpty
    @Size(min = 3, max = 35)
    private String lastName;

    @Size(max = 40)
    private String city;

    @Size(max = 96)
    private String street;

    @Size(max = 30)
    private String streetNumber;

    @Pattern(regexp = "^\\d{2}-\\d{3}$")
    private String postalCode;

    @NotNull
    @NonNull
    private String role;

    @NotNull
    @NonNull
    private Boolean isActive;
}