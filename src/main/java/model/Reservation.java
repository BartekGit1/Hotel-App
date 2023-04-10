package model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import model.room.Room;
import model.user.User;
import org.apache.commons.math3.util.Precision;

import java.time.LocalDate;
import java.util.UUID;

@Data
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reservation")
public class Reservation implements Serializable {

    @Id
    @Column(name = "reservation_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "room_room_number", nullable = false)
    private Room room;

    @NonNull
    @Column(name = "begin_time", nullable = false)
    private LocalDate beginTime;

    @NonNull
    @Column(name = "end_time", nullable = false)
    private LocalDate endTime;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "client_personal_id", nullable = false)
    private User user;

    @NotNull
    @Column(name = "reservation_cost", nullable = false)
    private double reservationCost;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    public int getRentDays() {
        return endTime.getDayOfYear() - beginTime.getDayOfYear();
    }

    public void calculateReservationCost() {
        reservationCost = Precision.round(getRentDays() * room.getPrice(), 2);
        if (reservationCost < 0) {
            reservationCost = 0;
        }
        if (getRentDays() == 0) {
            Precision.round((getRentDays() + 1) * room.getPrice(), 2);
        }
    }
}
