package controllers;

import dto.ReservationDto;
import dto.ReservationSelfDto;
import exceptions.LogicException;
import exceptions.ReservationException;
import managers.ReservationManager;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.security.enterprise.SecurityContext;
import java.net.URI;
import java.util.NoSuchElementException;
import java.util.UUID;

@Path("/reservations")
public class ReservationController {

    @Inject
    private ReservationManager reservationManager;

    @Inject
    private SecurityContext securityContext;

    @POST
    @RolesAllowed({"ADMIN", "MODERATOR"})
    @Consumes(MediaType.APPLICATION_JSON)
    public Response reserveRoom(@Valid ReservationDto reservation) throws LogicException {
        try {
            reservationManager.reserveRoom(reservation);
            return Response.created(URI.create("/Reservations/%s".formatted(reservation.getRoomNumber()))).build();
        } catch (ReservationException | ValidationException e) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
        }
    }

    @POST
    @RolesAllowed({"USER"})
    @Path("/self")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response reserveRoomByClient(@Valid ReservationSelfDto reservation) throws LogicException {
        try {
            String username = securityContext.getCallerPrincipal().getName();
            reservationManager.reserveRoom(new ReservationDto(reservation.getRoomNumber(), reservation.getBeginTime(), reservation.getEndTime(), username));
            return Response.created(URI.create("/Reservations/%s".formatted(reservation.getRoomNumber()))).build();
        } catch (ReservationException | ValidationException e) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
        }
    }

    @POST
    @RolesAllowed({"ADMIN", "MODERATOR"})
    @Path("/{reservationId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response endReserveRoom(@PathParam("reservationId") String reservationId) {
        try {
            reservationManager.endRoomReservation(UUID.fromString(reservationId));
            return Response.ok().build();
        } catch (NoSuchElementException e) {
            return Response.status(Response.Status.NOT_FOUND.getStatusCode(), e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
        }
    }

    @GET
    @RolesAllowed({"ADMIN", "MODERATOR"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllReservations() {
        return Response.ok().entity(reservationManager.getAllReservations()).build();
    }

    @GET
    @RolesAllowed({"ADMIN", "MODERATOR"})
    @Path("/{reservationId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReservationById(@PathParam("reservationId") String reservationId) {
        try {
            return Response.ok().entity(reservationManager.aboutReservation(UUID.fromString(reservationId))).build();
        } catch (ReservationException e) {
            return Response.status(Response.Status.NOT_FOUND.getStatusCode(), e.getMessage()).build();
        }
    }
}
