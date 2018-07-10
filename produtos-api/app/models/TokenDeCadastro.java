package models;

import com.avaje.ebean.Model;
import org.apache.commons.codec.digest.Crypt;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class TokenDeCadastro extends Model {
    @Id
    @GeneratedValue
    private  Long id;
    private  String codigo;
    @OneToOne
    private Usuario usuario;

    public TokenDeCadastro(Usuario usuario){
        this.usuario = usuario;
        this.codigo = akka.util.Crypt.sha1(usuario.getNome()+usuario.getEmail()+ akka.util.Crypt.generateSecureCookie());
    }

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
