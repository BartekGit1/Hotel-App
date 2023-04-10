package repositories;

import connectors.DatabaseConnector;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NoArgsConstructor;
import model.Reservation;

import javax.inject.Inject;

@Getter
@NoArgsConstructor
public class ReservationRepository implements Repository<Reservation> {

    @Inject
    private DatabaseConnector connector;

    @Override
    public Reservation get(Object element) {
        return Optional.ofNullable(find(element).get(0)).orElseThrow();
    }

    @Override
    public List<Reservation> find(Object... elements) {
        return Optional.of(Arrays.stream(elements)
                .map(element -> connector.getEntityManager().find(Reservation.class, element))
                .collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    @Override
    public List<Reservation> getAll() {
        return connector.getEntityManager().createQuery("SELECT reservation FROM Reservation reservation", Reservation.class)
                .getResultList();
    }

    public List<Reservation> getReservationsForClient(String username) {
        return connector.getEntityManager().createQuery("SELECT reservation FROM Reservation reservation WHERE reservation.user.username = :username", Reservation.class)
                .setParameter("username", username)
                .getResultList();
    }

    public List<Reservation> getReservationsForRoom(int roomNumber) {
        return connector.getEntityManager().createQuery("SELECT reservation FROM Reservation reservation WHERE reservation.room.roomNumber = :roomNumber", Reservation.class)
                .setParameter("roomNumber", roomNumber)
                .getResultList();
    }

    @Override
    public void add(Reservation element) {
        connector.getEntityManager().persist(element);
    }

    @Override
    public void remove(Reservation... elements) {
        Arrays.asList(elements).forEach(element -> connector.getEntityManager().remove(element));
    }

    @Override
    public void update(Reservation... elements) {
        Arrays.asList(elements).forEach(element -> connector.getEntityManager().merge(element));
    }
}
