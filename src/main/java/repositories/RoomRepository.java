package repositories;

import connectors.DatabaseConnector;
import dto.RoomDto;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.NoArgsConstructor;
import model.room.Room;

import javax.inject.Inject;

@Getter
@NoArgsConstructor
public class RoomRepository implements Repository<Room> {

    @Inject
    private DatabaseConnector connector;


    @Override
    public Room get(Object element) {
        return Optional.ofNullable(find(element).get(0)).orElseThrow();
    }

    @Override
    public List<Room> find(Object... elements) {
        return Optional.of(Arrays.stream(elements)
                .map(element -> connector.getEntityManager().find(Room.class, element))
                .collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    @Override
    public List<Room> getAll() {
        return connector.getEntityManager().createQuery("SELECT room FROM Room room", Room.class)
                .getResultList();
    }

    @Override
    public void add(Room element) {
        connector.getEntityManager().persist(element);
    }

    @Override
    public void remove(Room... elements) {
        Arrays.asList(elements).forEach(element -> connector.getEntityManager().remove(element));
    }

    @Override
    public void update(Room... elements) {
        Arrays.asList(elements).forEach(element -> connector.getEntityManager().merge(element));
    }

    public List<RoomDto> convertToRoomDto(Room... rooms) {
        return Stream.of(rooms)
                .map(room -> new RoomDto(room.getRoomNumber(), room.getCapacity(), room.getPrice(),
                        room.getEquipmentType()))
                .collect(Collectors.toList());
    }

    public List<Room> convertToRoom(RoomDto... rooms) {
        return Stream.of(rooms)
                .map(oldRoom -> new Room(oldRoom.getRoomNumber(), oldRoom.getCapacity(), oldRoom.getPrice(),
                        oldRoom.getEquipmentType()))
                .collect(Collectors.toList());
    }
}
