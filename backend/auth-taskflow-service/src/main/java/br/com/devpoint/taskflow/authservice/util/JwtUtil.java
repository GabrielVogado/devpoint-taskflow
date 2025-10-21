package br.com.devpoint.taskflow.authservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    /**
     * Esta é a chave secreta usada para assinar os tokens JWT.
     */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * Injeta o tempo de expiração dos tokens em milissegundos
     */
    @Value("${jwt.expirationMs}")
    private Long jwtExpirationMs;

    /**
     * Método privado que converte a string secreta em uma SecretKey usando o algoritmo HMAC SHA.
     * Esta chave é usada para assinar e verificar os tokens..
     *
     * @return O token JWT gerado.
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Método público que extrai o nome de usuário (subject) do token JWT.
     * Usa o método extractClaim com uma referência de método para Claims::getSubject
     */
    private String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Método público que extrai a data de expiração do token JWT.
     * Usa o método extractClaim com uma referência de método para Claims::getExpiration
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Método genérico que extrai qualquer claim do token usando uma função resolvedora:
     * Primeiro obtém todos os claims do token
     * Depois aplica a função específica para extrair o claim desejado
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrai todos os claims (informações) do token:
     * parserBuilder(): Cria um construtor de parser JWT
     * setSigningKey(): Define a chave para verificar a assinatura do token
     * build(): Constrói o parser configurado
     * parseClaimsJws(token): Analisa e valida o token JWS (JSON Web Signature)
     * getBody(): Retorna o payload (claims) do token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    /**
     * Método que verifica se o token expirou comparando a data de expiração com a data atual.
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new java.util.Date());
    }

    /**
     * Método público para gerar um novo token JWT:
     * Cria um mapa vazio para claims adicionais
     * Chama createToken com os claims e o nome de usuário
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Cria um token JWT:
     * builder(): Inicia a construção de um novo token
     * setClaims(): Define claims personalizados (se houver)
     * setSubject(): Define o subject (normalmente o nome de usuário)
     * setIssuedAt(): Define a data de criação do token
     * setExpiration(): Define a data de expiração baseada no tempo configurado
     * signWith(): Assina o token com a chave e algoritmo especificados
     * compact(): Finaliza e retorna o token como string
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Método que valida um token:
     * Extrai o nome de usuário do token
     * Verifica se corresponde ao nome de usuário do UserDetails
     * Verifica se o token não está expirado
     * Retorna true se ambas as condições forem satisfeitas
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
