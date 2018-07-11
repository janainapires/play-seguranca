package controllers;

import autenticadores.UsuarioAutenticado;
import daos.TokenDeCadastroDAO;
import daos.UsuarioDAO;
import models.EmailDeCadastro;
import models.TokenDeCadastro;
import models.Usuario;
import play.api.mvc.Call;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.libs.mailer.MailerClient;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import validadores.ValidadorDeUsuario;
import views.html.formularioDeLogin;
import views.html.formularioDeNovoUsuario;

import javax.inject.Inject;
import java.util.Optional;

public class UsuarioController extends Controller {

	public static final String AUTH = "AUTH";
	@Inject
	private FormFactory formularios;
	@Inject
	private ValidadorDeUsuario validadorDeUsuario;
	@Inject
	private MailerClient enviador;
	@Inject
	private UsuarioDAO usuarioDAO;
	@Inject
	private TokenDeCadastroDAO tokenDeCadastroDAO;

	public Result formularioDeNovoUsuario() {
		Form<Usuario> formulario =  formularios.form(Usuario.class);
		return ok(formularioDeNovoUsuario.render(formulario));
	}

	public Result salvaNovoUsuario() {
		Form<Usuario> formulario =  formularios.form(Usuario.class).bindFromRequest();

		if(validadorDeUsuario.temErros(formulario)){
			flash("danger", "Existem erros no formulário de cadastro");
			return badRequest(formularioDeNovoUsuario.render(formulario));
		}
		Usuario usuario = formulario.get();
		String senhaCrypto = akka.util.Crypt.sha1(usuario.getSenha());
		usuario.setSenha(senhaCrypto);
		usuario.setVerificado(false);
		usuario.save();
		TokenDeCadastro token = new TokenDeCadastro(usuario);
		token.save();
		enviador.send(new EmailDeCadastro(token));
		flash("success", "Um email foi enviado para confirmar seu cadastro");
		return redirect("/login"); //TODO: rota deve ser criada
	}


	public Result confirmaUsuario(String email, String codigo){
		Optional<Usuario> posssivelUsuario = usuarioDAO.comEmail(email);
		Optional<TokenDeCadastro> posssivelToken = tokenDeCadastroDAO.comCodigo(codigo);

		if (posssivelToken.isPresent() && posssivelUsuario.isPresent()){
			TokenDeCadastro token = posssivelToken.get();
			Usuario usuario = posssivelUsuario.get();
			if(token.getUsuario().equals(usuario)){
				token.delete();
				usuario.setVerificado(true);
				usuario.update();
				flash("success", "Cadastro confirmado com sucesso!");
				return redirect(routes.UsuarioController.painel());
			}
		}
		flash("danger", "Ocorreu um erro ao tentar confirmar o cadastro");
		return redirect(routes.UsuarioController.formularioDeLogin());
	}


	public Result formularioDeLogin(){
	return ok(formularioDeLogin.render(formularios.form()));
	}

	public Result fazLogin(){
		DynamicForm formulario = formularios.form().bindFromRequest();
		String email = formulario.get("email");
		String senha = akka.util.Crypt.sha1(formulario.get("senha"));

	    Optional<Usuario> possivelUsuario =	usuarioDAO.comEmailESenha(email, senha);

	    if(possivelUsuario.isPresent()){
	    	Usuario usuario = possivelUsuario.get();
	    	if(usuario.isVerificado()){
	    		session(AUTH, usuario.getEmail());
	    		flash("success", "Login efetuado com sucesso.");
				return redirect(routes.UsuarioController.painel());
			}else{
	    		flash("warning", "Usuário ainda não confirmado! Confira seu email.");
			}
		}else{
	    	flash("danger", "Credencias inválidas!");
		}

		return  redirect(routes.UsuarioController.formularioDeLogin());
	}


	@Security.Authenticated(UsuarioAutenticado.class)
	public Result painel() {
		return ok("Bem vindo, ao sistema de teste do Play Framework.");
	}
}
