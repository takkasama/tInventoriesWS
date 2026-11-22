package io.github.takkasama.web.tinventoriesws.util;

import jakarta.ws.rs.NameBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author takkasama
 */

@NameBinding
@Retention(RetentionPolicy.RUNTIME) // indicamos el cuando se va usar
@Target({ElementType.TYPE , ElementType.METHOD}) // indicamos el donde se va usar (en este caso tipos y metodos)

//para la creacion de la notacion se debe usar una interfaz del mismo modo que interface va acompannado con el @
public @interface Secure {
    
}
