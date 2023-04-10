package dto;

import lombok.*;
import model.Role;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserWithReservationsDto {

    @NonNull
    @NotNull
    @Size(min = 6, max = 20)
    @Length(min = 5, message = "The field must be at least 5 characters")
    private String username;

    @NonNull
    @NotNull
    @Size(min = 3, max = 35)
    private String firstName;

    @NonNull
    @NotNull
    @Size(min = 3, max = 35)
    private String lastName;

    @NonNull
    @NotNull
    private Role role;

    @NonNull
    @NotNull
    private Boolean isActive;

    @Size(max = 40)
    private String city;

    @Size(max = 96)
    private String street;

    @Size(max = 30)
    private String streetNumber;

    @Pattern(regexp = "^\\d{2}-\\d{3}$")
    private String postalCode;

    @NonNull
    @NotNull
    List<ReservationForUsersDto> reservations;
}
