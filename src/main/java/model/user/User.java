package model.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import model.Role;

@Entity
@Getter
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "hotel_users")
public class User implements Serializable {

    @Id
    @NonNull
    @NotNull
    @Column(name = "username", nullable = false)
    private String username;

    @NonNull
    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    @NonNull
    @Size(min = 3, max = 35)
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NonNull
    @NotNull
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NonNull
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.USER;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Size(max = 25)
    @Column(name = "city")
    private String city;

    @Size(max = 96)
    @Column(name = "street")
    private String street;

    @Size(max = 4)
    @Column(name = "streetNumber")
    private String streetNumber;

    @Pattern(regexp = "^\\d{2}-\\d{3}$")
    @Column(name = "postal_code", columnDefinition = "VARCHAR(6) CHECK (POSTAL_CODE ~ '^\\d{2}-\\d{3}$')")
    private String postalCode;

    public User(@NonNull String username, @NonNull String password, @NonNull String firstName, @NonNull String lastName,
                String city, String street, String streetNumber, String postalCode) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
        this.street = street;
        this.streetNumber = streetNumber;
        this.postalCode = postalCode;
    }
}
