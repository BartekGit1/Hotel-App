import javax.annotation.security.DeclareRoles;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/api")
@ApplicationScoped
@DeclareRoles({"ADMIN", "MODERATOR", "USER", "GUEST"})
public class Main extends Application {

}

