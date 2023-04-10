package dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
public class ReservationSelfDto {
    @NonNull
    @NotNull
    private Integer roomNumber;

    @NonNull
    @NotNull
    private String beginTime;

    @NonNull
    @NotNull
    private String endTime;
}
