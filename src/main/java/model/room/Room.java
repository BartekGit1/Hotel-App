package model.room;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Data
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"capacity", "price", "equipmentType", "version"})
@Entity
public class Room implements Serializable {

    @Id
    @NonNull
    @Column(name = "room_number", nullable = false)
    private Integer roomNumber;

    @NonNull
    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @NonNull
    @Column(name = "price", nullable = false)
    private Double price;

    @NonNull
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "equipment_type", nullable = false)
    private EquipmentType equipmentType;

    @Version
    private Long version;
}
