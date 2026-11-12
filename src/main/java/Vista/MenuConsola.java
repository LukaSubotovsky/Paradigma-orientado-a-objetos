package Vista;

import Modelo.*;
import Controlador.*;
import Persistencia.ArchivoBarrio;
import java.io.*;
import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.*;
public class MenuConsola {
    private final Barrio barrio;
    private final ArchivoBarrio archivo;
    private final Clock reloj;
    private final Scanner entrada;
    private final PrintStream salida;
    private Sesion sesion;
    private ControladorDatos datos;
    private ControladorReclamos reclamos;
    private ControladorExpensas expensas;
    private ControladorEquipos equipos;
    private ControladorVisitas visitas;
    private ControladorReportes reportes;
    public MenuConsola(Barrio barrio, ArchivoBarrio archivo, Clock reloj, InputStream entrada, PrintStream salida) {
        this.barrio = barrio; this.archivo = archivo; this.reloj = reloj; this.entrada = new Scanner(entrada); this.salida = salida;
    }
    public void ejecutar() {
        salida.println("LOS AROMOS - Gestión del barrio");
        boolean continuar = true;
        while (continuar) {
            try {
                revisarPlazos();
                if (sesion == null) {
                    String usuario = texto("Usuario (0 para salir)");
                    if (usuario.equals("0")) { guardar(); break; }
                    sesion = Sesion.ingresar(barrio, usuario, texto("Clave")); crearControladores();
                }
                mostrarOpciones(); int opcion = entero("Opción");
                if (opcion == 0) { guardar(); continuar = false; }
                else if (opcion == 90) { guardar(); sesion = null; }
                else { operar(opcion); guardar(); salida.println("Operación terminada."); }
            } catch (ReglaNegocioException e) { salida.println("No se pudo realizar: " + e.getMessage()); }
            catch (NumberFormatException | DateTimeParseException e) { salida.println("Formato inválido. Use números, fechas AAAA-MM-DD y horas HH:mm."); }
            catch (IOException e) { salida.println("No se pudo guardar: " + e.getMessage() + ". Los cambios siguen en memoria; reintente con 60."); }
            catch (NoSuchElementException e) {
                try { guardar(); } catch (IOException fallo) { salida.println("No se pudo guardar al finalizar la entrada: " + fallo.getMessage()); }
                continuar = false;
            }
        }
        salida.println("Fin de la sesión.");
    }
    private void revisarPlazos() throws ReglaNegocioException {
        new GestionReclamos(barrio).revisarPlazos(LocalDateTime.now(reloj));
        new GestionEquipos(barrio).verificarCamaraSinFuncionar(LocalDateTime.now(reloj));
        new GestionExpensas(barrio).verificarMorosidad(LocalDate.now(reloj));
    }
    private void crearControladores() {
        datos = new ControladorDatos(barrio, sesion, reloj); reclamos = new ControladorReclamos(barrio, sesion, reloj);
        expensas = new ControladorExpensas(barrio, sesion, reloj); equipos = new ControladorEquipos(barrio, sesion, reloj);
        visitas = new ControladorVisitas(barrio, sesion, reloj); reportes = new ControladorReportes(barrio, sesion, reloj);
    }
    private void mostrarOpciones() {
        RolUsuario rol = sesion.getUsuario().getRol();
        salida.println("\nUsuario: " + sesion.getUsuario().getNombre() + " (" + rol + ")");
        if (rol == RolUsuario.ADMINISTRADOR) {
            salida.println("1 Datos registrados | 2 Alta persona | 3 Alta lote | 4 Agregar vínculo | 5 Alta personal | 6 Alta usuario");
            salida.println("11 Asignar reclamo | 12 Avanzar estado | 14 Comentar | 15 Detalle reclamo | 16 Mis reclamos");
            salida.println("20 Emitir expensas | 21 Estado de cuenta | 22 Registrar pago | 23 Ajustar pago | 24 Notificaciones de mora");
            salida.println("30 Alta equipo | 31 Registrar falla y orden | 32 Iniciar reparación | 34 Fuera de servicio | 35 Detalle equipo");
            salida.println("44 Historial de visitas | 50 Reclamos pendientes | 51 Morosidad | 52 Equipos fuera de servicio | 53 Avisos");
        } else if (rol == RolUsuario.VECINO) {
            salida.println("10 Registrar reclamo | 13 Reabrir | 14 Comentar | 15 Detalle reclamo | 16 Mis reclamos");
            salida.println("31 Registrar falla | 40 Autorizar visita | 44 Mis autorizaciones");
        } else {
            salida.println("12 Avanzar reclamo | 14 Comentar | 15 Detalle reclamo | 16 Mis reclamos | 35 Detalle equipo");
            if (rol == RolUsuario.GUARDIA) salida.println("31 Registrar falla | 41 Consultar autorización | 42 Ingreso | 43 Egreso | 44 Historial de visitas");
            else salida.println("33 Cerrar orden de mantenimiento");
        }
        salida.println("60 Guardar | 90 Cambiar usuario | 0 Guardar y salir");
    }
    private void operar(int opcion) throws ReglaNegocioException {
        switch (opcion) {
            case 1 -> { mostrar(datos.personas()); mostrar(datos.lotes()); mostrar(datos.personal()); mostrar(datos.equipos()); }
            case 2 -> datos.registrarPersona(texto("Nombre"), texto("Apellido"), texto("DNI"), texto("Teléfono"), texto("Correo"));
            case 3 -> salida.println(datos.registrarLote(entero("Número"), monto("Superficie m2"), texto("DNI titular registrado")));
            case 4 -> {
                int lote = entero("Lote"); String dni = texto("DNI"); RolLote rol = elegir(RolLote.values());
                String hasta = texto("Fecha hasta AAAA-MM-DD (vacío: sin vencimiento)");
                datos.agregarVinculo(lote, dni, rol, hasta.isBlank() ? null : LocalDate.parse(hasta));
            }
            case 5 -> altaPersonal();
            case 6 -> {
                String usuario = texto("Usuario"), clave = texto("Clave (mínimo 6 caracteres)"); RolUsuario rol = elegir(RolUsuario.values());
                boolean persona = rol == RolUsuario.ADMINISTRADOR || rol == RolUsuario.VECINO;
                datos.registrarUsuario(usuario, clave, rol, persona ? texto("DNI registrado") : null, persona ? null : entero("Legajo registrado"));
            }
            case 10 -> salida.println(reclamos.registrarReclamo(entero("Lote"), elegir(TipoReclamo.values()), texto("Descripción")));
            case 11 -> reclamos.asignarReclamo(entero("Reclamo"), entero("Legajo"));
            case 12 -> reclamos.avanzarEstado(entero("Reclamo"), texto("Observación"));
            case 13 -> reclamos.reabrirReclamo(entero("Reclamo"), texto("Motivo"));
            case 14 -> reclamos.agregarComentario(entero("Reclamo"), texto("Comentario"));
            case 15 -> { Reclamo r = reclamos.consultar(entero("Reclamo")); salida.println(r); salida.println("Responsable: " + r.getResponsable()); mostrar(r.getHistorial()); mostrar(r.getComentarios()); }
            case 16 -> mostrar(reclamos.listar());
            case 20 -> expensas.emitirExpensas(YearMonth.parse(texto("Período AAAA-MM")), monto("Presupuesto"));
            case 21 -> estadoCuenta();
            case 22 -> salida.println(expensas.registrarPago(entero("Lote"), monto("Importe"), elegir(MedioDePago.values()), fecha("Fecha del pago")));
            case 23 -> expensas.registrarAjuste(entero("Lote"), entero("Número de pago"), monto("Diferencia (+ aumenta / - disminuye)"), texto("Motivo"));
            case 24 -> mostrar(expensas.consultarNotificaciones(entero("Lote")));
            case 30 -> altaEquipo();
            case 31 -> salida.println(equipos.registrarFalla(texto("Código de equipo"), texto("Descripción")));
            case 32 -> equipos.iniciarReparacion(texto("Código de equipo"), entero("Legajo técnico"));
            case 33 -> equipos.cerrarOrden(texto("Código de equipo"), texto("Descripción de reparación"));
            case 34 -> equipos.retirarEquipo(texto("Código de equipo"));
            case 35 -> { Equipo e = equipos.consultarEstado(texto("Código de equipo")); salida.println(e); mostrar(e.getFallas()); mostrar(e.getOrdenes()); }
            case 40 -> salida.println(visitas.registrarAutorizacion(entero("Lote"), texto("Nombre visitante"), texto("DNI visitante"),
                    fecha("Desde"), fecha("Hasta"), LocalTime.parse(texto("Hora desde HH:mm")), LocalTime.parse(texto("Hora hasta HH:mm")), texto("Recurrente: s/n").equalsIgnoreCase("s")));
            case 41 -> salida.println(visitas.consultarAutorizacion(texto("DNI visitante")));
            case 42 -> salida.println(visitas.registrarIngreso(texto("DNI visitante")));
            case 43 -> {
                String dni = texto("DNI visitante"); String nota = texto("Observación (vacío: salida normal; completar para egreso manual)");
                if (nota.isBlank()) visitas.registrarEgreso(dni); else visitas.registrarEgresoManual(dni, nota);
            }
            case 44 -> { for (Autorizacion a : visitas.consultarHistorial()) { salida.println(a); mostrar(a.getAccesos()); } }
            case 50 -> {
                LocalDate desde = fecha("Desde"), hasta = fecha("Hasta");
                TipoReclamo tipo = texto("Filtrar por tipo: s/n").equalsIgnoreCase("s") ? elegir(TipoReclamo.values()) : null;
                mostrar(reportes.reclamosPendientes(desde, hasta, tipo));
            }
            case 51 -> mostrar(reportes.morosidadPorLote(fecha("Calcular hasta")));
            case 52 -> mostrar(reportes.equiposFueraDeServicio(texto("Filtrar sector: s/n").equalsIgnoreCase("s") ? elegir(Sector.values()) : null));
            case 53 -> mostrar(reportes.avisosAdministrador());
            case 60 -> salida.println("Guardando...");
            default -> throw new ReglaNegocioException("Opción inexistente");
        }
    }
    private void estadoCuenta() throws ReglaNegocioException {
        int numero = entero("Lote"); LocalDate hasta = fecha("Calcular hasta"); Lote lote = expensas.consultarEstadoCuenta(numero, hasta);
        for (Expensa e : lote.getExpensas()) salida.println(e + " | interés " + new CalculadorIntereses().calcular(e, hasta) + " | " + e.getEstado(hasta));
        for (Pago pago : lote.getPagos()) { salida.println(pago); mostrar(pago.getAplicaciones()); mostrar(pago.getAjustes()); }
        salida.println("Capital: " + lote.calcularDeudaCapital() + " | Intereses: " + lote.calcularDeudaIntereses(hasta));
    }
    private void altaPersonal() throws ReglaNegocioException {
        int tipo = entero("1 Guardia / 2 Mantenimiento"), legajo = entero("Legajo"); String nombre = texto("Nombre"); LocalDate fecha = fecha("Ingreso laboral");
        if (tipo == 1) datos.registrarPersonal(new GuardiaDeSeguridad(legajo, nombre, fecha, texto("Turno"), elegir(PuestoGuardia.values())));
        else if (tipo == 2) datos.registrarPersonal(new EmpleadoDeMantenimiento(legajo, nombre, fecha, elegir(Especialidad.values())));
        else throw new ReglaNegocioException("Tipo de personal inválido");
    }
    private void altaEquipo() throws ReglaNegocioException {
        int tipo = entero("1 Cámara / 2 Luminaria"); String codigo = texto("Código"); Sector sector = elegir(Sector.values()); LocalDate fecha = fecha("Instalación");
        if (tipo == 1) equipos.registrarEquipo(new Camara(codigo, sector, fecha, entero("Resolución"), entero("Días de grabación")));
        else if (tipo == 2) equipos.registrarEquipo(new Luminaria(codigo, sector, fecha, texto("Tipo de lámpara"), entero("Potencia")));
        else throw new ReglaNegocioException("Tipo de equipo inválido");
    }
    private String texto(String pregunta) { salida.print(pregunta + ": "); return entrada.nextLine().trim(); }
    private int entero(String pregunta) { return Integer.parseInt(texto(pregunta)); }
    private BigDecimal monto(String pregunta) { return new BigDecimal(texto(pregunta).replace(',', '.')); }
    private LocalDate fecha(String pregunta) { return LocalDate.parse(texto(pregunta + " AAAA-MM-DD")); }
    private <E extends Enum<E>> E elegir(E[] valores) throws ReglaNegocioException {
        for (int i = 0; i < valores.length; i++) salida.println((i + 1) + " " + valores[i]);
        int indice = entero("Elegir") - 1; Validacion.exigir(indice >= 0 && indice < valores.length, "Valor fuera de rango"); return valores[indice];
    }
    private void mostrar(Collection<?> elementos) {
        if (elementos.isEmpty()) salida.println("Sin resultados."); else elementos.forEach(salida::println);
    }
    private void guardar() throws IOException { archivo.guardar(barrio); }
}
