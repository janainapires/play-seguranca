package autenticadores;

import play.mvc.Http;
import play.mvc.Security.Authenticator;

import static controllers.UsuarioController.AUTH;

public class UsuarioAutenticado extends Authenticator {


    @Override
    public String getUsername(Http.Context context) {
        return  context.session().get(AUTH);
    }
}
