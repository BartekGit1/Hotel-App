package controllers;

import auth.AuthIdentityStore;
import auth.JwtGenerator;
import dto.LoginDto;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("/auth")
@RequestScoped
public class AuthController {

    @Context
    private SecurityContext securityContext;

    @Inject
    private AuthIdentityStore authIdentityStore;

    private final JwtGenerator jwtGenerator = new JwtGenerator();

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("GUEST")
    public Response login(LoginDto loginDto) {
        try {
            UsernamePasswordCredential usernamePasswordCredential = new UsernamePasswordCredential(loginDto.getUsername(), loginDto.getPassword());
            CredentialValidationResult credentialValidationResult = authIdentityStore.validate(usernamePasswordCredential);

            if (credentialValidationResult.getStatus().equals(CredentialValidationResult.Status.VALID)) {
                String jwt = jwtGenerator.generateJWT(loginDto.getUsername(), credentialValidationResult.getCallerGroups().iterator().next());
                return Response.ok().entity(jwt).build();
            }
        } catch (IndexOutOfBoundsException e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
}
