package dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CreateUserDto {

    @NonNull
    @NotEmpty
    @Size(min = 6, max = 20)
    private String username;

    @NonNull
    @NotEmpty
    @Size(min = 6, max = 20)
    private String password;

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
}
