package repositories;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import connectors.DatabaseConnector;
import lombok.Getter;
import lombok.NoArgsConstructor;
import model.user.User;

import javax.inject.Inject;

@Getter
@NoArgsConstructor
public class UserRepository implements Repository<User> {

    @Inject
    private DatabaseConnector connector;

    @Override
    public User get(Object element) {
        return Optional.ofNullable(find(element).get(0)).orElseThrow();
    }

    @Override
    public List<User> find(Object... elements) {
        return Optional.of(Arrays.stream(elements)
                .map(element -> connector.getEntityManager().find(User.class, element))
                .collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    @Override
    public List<User> getAll() {
        return connector.getEntityManager().createQuery("SELECT user FROM User user", User.class)
                .getResultList();
    }

    @Override
    public void add(User user) {
        connector.getEntityManager().persist(user);
    }

    @Override
    public void remove(User... elements) {
        Arrays.asList(elements).forEach(element -> connector.getEntityManager().remove(element));
    }

    @Override
    public void update(User... elements) {
        Arrays.asList(elements).forEach(element -> connector.getEntityManager().merge(element));
    }

    public List<User> getAllClients() {
        return connector.getEntityManager().createQuery("SELECT user FROM User user WHERE role = 'USER'", User.class)
                .getResultList();
    }

    public List<User> getByUsername(String pattern) {
        return connector.getEntityManager().createQuery("SELECT user FROM User user WHERE username LIKE :id", User.class)
                .setParameter("id", "%" + pattern + "%")
                .getResultList();
    }

    public User getByUsernameAndPasswd(String username, String password) {
        return connector.getEntityManager().createQuery("SELECT user FROM User user WHERE username LIKE :id AND password LIKE :password", User.class)
                .setParameter("id", username)
                .setParameter("password", password)
                .getResultList().get(0);
    }



}
