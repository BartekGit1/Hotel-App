package controllers;

import dto.ReservationForRoomsDto;
import dto.RoomDto;
import dto.RoomWithReservationDto;
import exceptions.ReservationException;
import exceptions.RoomException;
import managers.ReservationManager;
import managers.RoomManager;
import model.Reservation;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Path("/rooms")
public class RoomController {

    @Inject
    private RoomManager roomManager;

    @Inject
    private ReservationManager reservationManager;

    private final Logger log = Logger.getLogger(getClass().getName());

    @POST
    @RolesAllowed({"ADMIN", "MODERATOR"})
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addRoom(@Valid RoomDto room) {
        try {
            roomManager.addRoom(room);
            return Response.created(URI.create("/rooms/%s".formatted(room.getRoomNumber()))).build();
        } catch (RoomException e) {
            log.warning("Room already exists %s ".formatted(room.getRoomNumber()));
            return Response.status(Response.Status.CONFLICT.getStatusCode(), e.getMessage()).build();
        } catch (ValidationException e) {
            String message = "Room validation failed for room: %s ".formatted(room);
            log.warning(message);
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(),message).build();
        }
    }

    @GET
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRooms() {
        return Response.ok().entity(roomManager.getAllRooms()).build();
    }

    @GET
    @Path("/{roomNumber}")
    @RolesAllowed({"ADMIN", "MODERATOR", "USER"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoom(@PathParam("roomNumber") int roomNumber) {
        try {
            return Response.ok().entity(roomManager.getRoom(roomNumber)).build();
        } catch (NoSuchElementException e) {
            log.warning("Room %s does not exist. ".formatted(roomNumber));
            return Response.status(Response.Status.NOT_FOUND.getStatusCode(), e.getMessage()).build();
        }
    }

    @PUT
    @RolesAllowed({"ADMIN", "MODERATOR"})
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateRoom(@Valid RoomDto room) {
        try {
            roomManager.updateRoom(room);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (RoomException e) {
            return Response.status(Response.Status.NOT_FOUND.getStatusCode(), e.getMessage()).build();
        } catch (ValidationException e) {
            String message = "Room validation failed for room: %s ".formatted(room);
            log.warning(message);
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), message).build();
        }

    }

    @DELETE
    @RolesAllowed({"ADMIN"})
    @Path("/{roomNumber}")
    public Response removeRoom(@PathParam("roomNumber") int roomNumber) {
        try {
            roomManager.removeRoom(roomNumber);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (RoomException e) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
        } catch (NoSuchElementException e) {
            return Response.status(Response.Status.NOT_FOUND.getStatusCode(), e.getMessage()).build();
        }
    }

    @GET
    @Path("/{roomNumber}/reservations")
    @RolesAllowed({"ADMIN", "MODERATOR"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoomWithReservations(@PathParam("roomNumber") int roomNumber) {
        try {
            RoomDto room = roomManager.getRoom(roomNumber);
            List<Reservation> reservation = reservationManager.getReservationsForRoom(roomNumber);
            List<ReservationForRoomsDto> reservations = reservation.stream().
                    map(reservation1 -> new ReservationForRoomsDto(reservation1.getBeginTime(),
                            reservation1.getEndTime(), reservation1.getUser().getUsername())).collect(Collectors.toList());
            RoomWithReservationDto roomWithReservationDto =
                    new RoomWithReservationDto(room.getRoomNumber(), room.getCapacity(), room.getPrice(),
                            room.getEquipmentType(), reservations);
            return Response.ok().entity(roomWithReservationDto).build();
        } catch (NoSuchElementException e) {
            log.warning("Room %s does not exist.".formatted(roomNumber));
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        catch (ReservationException e) {
            log.warning("Not found reservations for room %s".formatted(roomNumber));
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
