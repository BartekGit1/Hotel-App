package controllers;

import com.nimbusds.jose.JOSEException;
import dto.*;
import exceptions.ChangePasswordException;
import exceptions.JwsException;
import exceptions.UserException;
import exceptions.ReservationException;
import managers.UserManager;
import managers.ReservationManager;
import model.Reservation;
import model.user.User;
import org.jetbrains.annotations.NotNull;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.List;
import java.util.stream.Collectors;

@Path("/users")
public class UserController {

    @Inject
    private UserManager userManager;

    @Inject
    private ReservationManager reservationManager;

    @Inject
    private SecurityContext securityContext;

    private final Logger log = Logger.getLogger(getClass().getName());

    @GET
    @PermitAll
    @Path("/{username}/jws")
    public Response getJws(@PathParam("username") String username) {
        try {
            String jws = userManager.getJwsForUser(username);
            return Response.noContent().header("ETag", jws).build();
        } catch (UserException e) {
            return Response.status(Response.Status.NOT_FOUND.getStatusCode(), e.getMessage()).build();
        } catch (JOSEException e) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
        }
    }

    @GET
    @RolesAllowed({"ADMIN", "MODERATOR"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        List<GetUserDto> users = userManager.getAllUsers().stream()
                .map(user -> new GetUserDto(user.getUsername(), user.getFirstName(), user.getLastName(),
                        Optional.ofNullable(user.getCity()).orElse(""),
                        Optional.ofNullable(user.getStreet()).orElse(""),
                        Optional.ofNullable(user.getStreetNumber()).orElse(""),
                        Optional.ofNullable(user.getPostalCode()).orElse(""),
                        user.getRole().toString(), user.getIsActive()))
                .toList();
        return Response.ok().entity(users).build();
    }

    @GET
    @Path("/clients")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN", "MODERATOR"})
    public Response getAllClients() {
        List<GetUserDto> users = userManager.getAllClients().stream()
                .map(user -> new GetUserDto(user.getUsername(), user.getFirstName(), user.getLastName(), user.getCity(),
                        user.getStreet(), user.getStreetNumber(), user.getPostalCode(), user.getRole().toString(), user.getIsActive())).toList();
        return Response.ok().entity(users).build();
    }

    @GET
    @Path("/user")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN", "MODERATOR"})
    public Response getUsersByUsername(@QueryParam("username") String pattern) {
        try {
            List<GetUserDto> users = userManager.getClientsByUsername(pattern).stream()
                    .map(user -> new GetUserDto(user.getUsername(), user.getFirstName(), user.getLastName(),
                            Optional.ofNullable(user.getCity()).orElse(""),
                            Optional.ofNullable(user.getStreet()).orElse(""),
                            Optional.ofNullable(user.getStreetNumber()).orElse(""),
                            Optional.ofNullable(user.getPostalCode()).orElse(""),
                            user.getRole().toString(),
                            user.getIsActive())).toList();
            return Response.ok().entity(users).build();
        } catch (NoSuchElementException e) {
            return Response.status(Response.Status.NOT_FOUND.getStatusCode(), e.getMessage()).build();
        }
    }

    @GET
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN", "MODERATOR", "USER"})
    public Response getUser(@PathParam("username") String username) {
        try {
            User user = userManager.getClientByUsername(username);
            return Response.ok().entity(new GetUserDto(user.getUsername(), user.getFirstName(), user.getLastName(),
                    user.getCity(), user.getStreet(), user.getStreetNumber(), user.getPostalCode(), user.getRole().toString(),
                    user.getIsActive())).build();
        } catch (NoSuchElementException e) {
            String message = "Client %s does not exist.".formatted(username);
            log.warning(message);
            return Response.status(Response.Status.NOT_FOUND.getStatusCode(), message).build();
        }
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("GUEST")
    public Response addUser(@Valid CreateUserDto user) {
        try {
            userManager.addClientToHotel(user);
            return Response.created(URI.create("/users/%s".formatted(user.getUsername()))).build();
        } catch (UserException e) {
            return Response.status(Response.Status.CONFLICT.getStatusCode(), e.getMessage()).build();
        } catch (ValidationException e) {
            String message = "User validation failed for user: %s".formatted(user.toString());
            log.warning(message);
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), message).build();
        }
    }

    @PUT
    @RolesAllowed({"ADMIN", "MODERATOR", "USER"})
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(@Valid UpdateUserDto client, @Context HttpServletRequest request) {
        try {
            String jws = request.getHeader("If-Match");
            if (jws == null) {
                throw new BadRequestException();
            }
            userManager.modifyClient(client, jws);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (UserException e) {
            return Response.status(Response.Status.NOT_FOUND.getStatusCode(), e.getMessage()).build();
        } catch (BadRequestException | JwsException | IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
        }
    }

    @POST
    @Path("/{username}/activate")
    @RolesAllowed({"ADMIN"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response activateUser(@PathParam("username") String username) {
        try {
            userManager.activateClient(username);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (NoSuchElementException | UserException e) {
            log.warning("User %s does not exist.".formatted(username));
            return Response.status(Response.Status.NOT_FOUND.getStatusCode(), e.getMessage()).build();
        }
    }

    @POST
    @RolesAllowed({"ADMIN"})
    @Path("/{username}/deactivate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deactivateUser(@PathParam("username") String username) {
        try {
            userManager.deactivateClient(username);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (NoSuchElementException | UserException e) {
            log.warning("Client %s does not exist.".formatted(username));
            return Response.status(Response.Status.NOT_FOUND.getStatusCode(), e.getMessage()).build();
        }
    }

    @GET
    @Path("/{username}/reservations")
    @RolesAllowed({"ADMIN", "MODERATOR"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClientWithReservations(@PathParam("username") String username) {
        return getInfoClientWithReservations(username);
    }

    @GET
    @Path("/reservations")
    @RolesAllowed({"USER"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClientWithReservations() {
        return getInfoClientWithReservations(securityContext.getCallerPrincipal().getName());
    }

    @PATCH
    @RolesAllowed({"ADMIN", "MODERATOR", "USER"})
    @Path("/updatePassword")
    public Response changeUserPassword(@Valid @NotNull ChangePasswordDto passwordDto) {
        try {
            userManager.changePassword(passwordDto.getOldPassword(), passwordDto.getNewPassword());
            return Response.ok().build();
        } catch (UserException e) {
            return Response.status(Response.Status.NOT_FOUND.getStatusCode(), e.getMessage()).build();
        } catch (ChangePasswordException | ValidationException e) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
        }
    }

    private Response getInfoClientWithReservations(String username) {
        try {
            User user = userManager.getClientByUsername(username);
            List<Reservation> reservation = reservationManager.getReservationsForClient(username);
            List<ReservationForUsersDto> reservations = reservation.stream().
                    map(reservation1 -> new ReservationForUsersDto(
                            reservation1.getRoom().getRoomNumber(),
                            reservation1.getBeginTime(),
                            reservation1.getEndTime()))
                    .collect(Collectors.toList());
            UserWithReservationsDto userWithReservationsDto =
                    new UserWithReservationsDto(user.getUsername(), user.getFirstName(), user.getLastName(),
                            user.getRole(), user.getIsActive(), user.getCity(), user.getStreet(),
                            user.getStreetNumber(), user.getPostalCode(), reservations);
            return Response.ok().entity(userWithReservationsDto).build();
        } catch (NoSuchElementException e) {
            log.warning("Client %s does not exist.".formatted(username));
            return Response.status(Response.Status.NOT_FOUND.getStatusCode(), e.getMessage()).build();
        } catch (ReservationException e) {
            log.warning("Not found reservations for client %s".formatted(username));
            return Response.status(Response.Status.NOT_FOUND.getStatusCode(), e.getMessage()).build();
        }
    }
}
