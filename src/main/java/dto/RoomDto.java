package dto;


import lombok.*;
import model.room.EquipmentType;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public class RoomDto {

    @NonNull
    @NotNull
    @Min(1)
    private Integer roomNumber;

    @NonNull
    @NotNull
    @Min(1)
    private Integer capacity;

    @NonNull
    @NotNull
    @Positive
    private Double price;

    @NonNull
    private EquipmentType equipmentType = EquipmentType.BASIC;
}
