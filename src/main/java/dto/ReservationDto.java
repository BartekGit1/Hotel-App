package dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
public class ReservationDto {

    @NonNull
    @NotNull
    private Integer roomNumber;

    @NonNull
    @NotNull
    private String beginTime;

    @NonNull
    @NotNull
    private String endTime;

    @NonNull
    @NotNull
    private String username;
}
