package dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ReservationForRoomsDto {

    @NonNull
    @NotNull
    private LocalDate beginTime;

    @NonNull
    @NotNull
    private LocalDate endTime;

    @NonNull
    @NotNull
    private String username;
}
