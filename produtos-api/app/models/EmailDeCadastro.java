package models;

import play.libs.mailer.Email;

public class EmailDeCadastro extends Email {
    private static final String REMETENTE = "Teste Email <teste.email.tramita@gmail.com>";
    private static final String ASSUNTO = "Confirmação de Cadastro de Usuário";
    private static final String COPROFORMAT = "Olá %s, por favor clique no link a seguir para confirmar seu cadastro! <a href='%s'>Confirmar cadastro!</a>";

    public EmailDeCadastro(TokenDeCadastro token) {
            Usuario usuario = token.getUsuario();
            this.setFrom(REMETENTE);
            this.setSubject(ASSUNTO);
            String destinatario = String.format("%s <%s>", usuario.getNome(), usuario.getEmail());
            this.addTo(destinatario);
            String link = String.format("http://astec11:9000/usuario/confirma/%s/%s", usuario.getEmail(), token.getCodigo());
            String corpo = String.format(COPROFORMAT, usuario.getNome(), link);
            this.setBodyHtml(corpo);
    }
}
