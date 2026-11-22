package cr.ac.una.sicobuws.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author cbcar
 */
public class JwTokenHelper {

    //  EL TOKEN DE RENOVACION TIENE UNA DURACION  MAYOR AL DE RENOVACION, CUANDO SE GASTA EL DE RENOVACION SE CERRARA EL USUARIO Y SE SOLICITARA EL LOGIN
    private static JwTokenHelper jwTokenHelper = null;
    private static final long EXPIRATION_LIMIT = 10; //
    private static final long EXPIRATION_RENEWAL_LIMIT = 20; // TIEMPO DE EXPIRACION PARA PROCESOS EDUCATVIO , LOS ESTANDARES VARIA SEGUN EL TIPO DE APLICACION 
    private static final String AUTHENTICATION_SCHEME = "Bearer ";
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); //GENERACION DE LLAVE DE CIERRE DE TOKEN, QUE NOS PERMITE EL TOKEN SEA DIFERENTE, Y QUE LA VALIDEZ DEL TOKEN SEA VARAIBLE 

    //SI SE TIENE UN KEY FIJO SE ES VUNERABLE A QUE SE OBTENGA ESTE Y QUE SE TENGA ACCESO A ESTE DE FORMA AJENA A LA APLIACACION
    private JwTokenHelper() {
    }

    public static JwTokenHelper getInstance() {
        if (jwTokenHelper == null) {
            jwTokenHelper = new JwTokenHelper();
        }
        return jwTokenHelper;
    }

    public String generatePrivateKey(String username) {
        return AUTHENTICATION_SCHEME + Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(getExpirationDate(false))
                .claim("rnt", AUTHENTICATION_SCHEME + Jwts.builder()
                        .setSubject(username)
                        .setIssuedAt(new Date())
                        .setExpiration(getExpirationDate(true))
                        .claim("rnw", true)
                        .signWith(key)
                        .compact())
                .signWith(key)
                .compact();
    }

    private Date getExpirationDate(boolean renewal) {
        long currentTimeInMillis = System.currentTimeMillis();
        long expMilliSecond = TimeUnit.MINUTES.toMillis(EXPIRATION_LIMIT);
        if (renewal) {
            expMilliSecond = TimeUnit.MINUTES.toMillis(EXPIRATION_RENEWAL_LIMIT);
        }
        return new Date(currentTimeInMillis + expMilliSecond);
    }

    public Claims claimKey(String privateKey) throws ExpiredJwtException, MalformedJwtException {
        return Jwts.parser().setSigningKey(key)
                .parseClaimsJws(privateKey)
                .getBody();
    }

}
