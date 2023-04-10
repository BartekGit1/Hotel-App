package managers;

import auth.JwsGenerator;
import com.nimbusds.jose.JOSEException;
import dto.CreateUserDto;

import dto.UpdateUserDto;
import exceptions.ChangePasswordException;
import exceptions.JwsException;
import exceptions.UserException;
import jakarta.persistence.*;
import model.Role;
import model.user.User;
import repositories.UserRepository;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import java.text.ParseException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

@RequestScoped
public class UserManager {

    @Inject
    private UserRepository userRepository;

    @Inject
    private SecurityContext securityContext;

    private final JwsGenerator jwsGenerator = new JwsGenerator();

    private final Logger log = Logger.getLogger(getClass().getName());

    public List<User> getAllUsers() {
        return userRepository.getAll();
    }

    public List<User> getAllClients() {
        return userRepository.getAllClients();
    }

    public List<User> getClientsByUsername(String pattern) {
        return userRepository.getByUsername(pattern);
    }

    public User getClientByUsername(String username) {
        return userRepository.get(username);
    }

    public void addClientToHotel(CreateUserDto client) throws UserException {
        try {
            userRepository.getConnector().getEntityTransaction().begin();
            userRepository.get(client.getUsername());
            userRepository.getConnector().getEntityManager().lock(userRepository.get(client.getUsername()), LockModeType.PESSIMISTIC_WRITE);
            userRepository.getConnector().getEntityTransaction().rollback();
            log.warning("Client %s already exists".formatted(client.getUsername()));
            throw new UserException("Client %s already exists".formatted(client.getUsername()));
        } catch (NoSuchElementException e) {
            userRepository.add(new User(client.getUsername(), client.getPassword(), client.getFirstName(),
                    client.getLastName(), client.getCity(), client.getStreet(), client.getStreetNumber(), client.getPostalCode()));
            userRepository.getConnector().getEntityTransaction().commit();
        }
    }

    public void modifyClient(UpdateUserDto client, String jws) throws JwsException, UserException {
        try {
            if (jwsGenerator.verify(jws)) {
                String newJws = jwsGenerator.generateJws(client.getUsername());
                if (!newJws.equals(jws)) {
                    throw new JwsException("Username can not be changed");
                }
            }
            userRepository.getConnector().getEntityTransaction().begin();
            User oldUser = userRepository.get(client.getUsername());
            userRepository.getConnector().getEntityManager().lock(oldUser, LockModeType.PESSIMISTIC_WRITE);

            oldUser.setUsername(client.getUsername());
            oldUser.setFirstName(client.getFirstName());
            oldUser.setLastName(client.getLastName());
            oldUser.setCity(client.getCity());
            oldUser.setStreet(client.getStreet());
            oldUser.setStreetNumber(client.getStreetNumber());
            oldUser.setPostalCode(client.getPostalCode());
            oldUser.setRole(Role.valueOf(client.getRole()));
            userRepository.update(oldUser);
            userRepository.getConnector().getEntityTransaction().commit();
        } catch (NoSuchElementException e) {
            userRepository.getConnector().getEntityTransaction().rollback();
            log.warning("Client %s does not exist".formatted(client.getUsername()));
            throw new UserException("Client %s does not exist".formatted(client.getUsername()));
        } catch (ParseException | JOSEException e) {
            throw new JwsException("JWS exception");
        }
    }

    public void deactivateClient(String username) throws UserException {
        try {
            userRepository.getConnector().getEntityTransaction().begin();
            User user = userRepository.get(username);
            userRepository.getConnector().getEntityManager().lock(user, LockModeType.PESSIMISTIC_WRITE);
            user.setIsActive(false);
            userRepository.update(user);
            userRepository.getConnector().getEntityTransaction().commit();
        } catch (NoSuchElementException e) {
            userRepository.getConnector().getEntityTransaction().rollback();
            log.warning("Client %s does not exist".formatted(username));
            throw new UserException("Client %s does not exist".formatted(username));
        }
    }

    public void activateClient(String username) throws UserException {
        try {
            userRepository.getConnector().getEntityTransaction().begin();
            User user = userRepository.get(username);
            userRepository.getConnector().getEntityManager().lock(user, LockModeType.PESSIMISTIC_WRITE);
            user.setIsActive(true);
            userRepository.update(user);
            userRepository.getConnector().getEntityTransaction().commit();
        } catch (NoSuchElementException e) {
            userRepository.getConnector().getEntityTransaction().rollback();
            log.warning("Client %s does not exist".formatted(username));
            throw new UserException("Client %s does not exist".formatted(username));
        }
    }

    public void changePassword(String oldPassword, String newPassword) throws UserException, ChangePasswordException {
        String username = securityContext.getCallerPrincipal().getName();
        try {
            User user = userRepository.get(username);
            if (!user.getPassword().equals(oldPassword)) {
                throw new ChangePasswordException("Old password is wrong.");
            }
            userRepository.getConnector().getEntityTransaction().begin();
            user.setPassword(newPassword);
            userRepository.update(user);
            userRepository.getConnector().getEntityTransaction().commit();
        } catch (NoSuchElementException e) {
            userRepository.getConnector().getEntityTransaction().rollback();
            log.warning("Client %s does not exist".formatted(username));
            throw new UserException("Client %s does not exist".formatted(username));
        }
    }

    public String getJwsForUser(String username) throws UserException, JOSEException {
        try {
            User user = getClientByUsername(username);
            return jwsGenerator.generateJws(user.getUsername());
        } catch (NoSuchElementException e) {
            log.warning("Client %s does not exist".formatted(username));
            throw new UserException("Client %s does not exist".formatted(username));
        }
    }

}
