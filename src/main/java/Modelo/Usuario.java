package Modelo;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;
public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String nombre, documento, sal, claveHash;
    private final RolUsuario rol;
    private final Integer legajo;
    public Usuario(String nombre, String clave, RolUsuario rol, String documento, Integer legajo) throws ReglaNegocioException {
        this.nombre = Validacion.texto(nombre, "Usuario");
        Validacion.exigir(clave != null && clave.length() >= 6 && rol != null, "Clave de al menos seis caracteres y rol requeridos");
        this.rol = rol; this.documento = documento; this.legajo = legajo;
        byte[] bytes = new byte[16]; new SecureRandom().nextBytes(bytes);
        sal = HexFormat.of().formatHex(bytes); claveHash = hash(sal + clave);
    }
    private static String hash(String texto) {
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(texto.getBytes(StandardCharsets.UTF_8))); }
        catch (NoSuchAlgorithmException e) { throw new IllegalStateException("Java no ofrece SHA-256", e); }
    }
    public boolean validarClave(String clave) { return clave != null && MessageDigest.isEqual(
            claveHash.getBytes(StandardCharsets.UTF_8), hash(sal + clave).getBytes(StandardCharsets.UTF_8)); }
    public String getNombre() { return nombre; }
    public RolUsuario getRol() { return rol; }
    public String getDocumento() { return documento; }
    public Integer getLegajo() { return legajo; }
}
