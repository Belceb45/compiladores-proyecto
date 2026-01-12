import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Interprete {

    static boolean existenErrores = false;
    static Interpreter interpreter = new Interpreter();

    public static void main(String[] args) throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        System.out.println("Interprete iniciado. Escribe 'salir' para terminar.");

        for (;;) {
            System.out.print(">>> ");
            String linea = reader.readLine();
            if (linea == null || linea.trim().equals("salir")) break;

            ejecutar(linea);
            existenErrores = false;
        }
    }

    private static void ejecutar(String source) {
        try {
            boolean tienePuntoComa = source.trim().endsWith(";");

            Scanner scanner = new Scanner(source);
            List<Token> tokens = scanner.scan();

            if (tokens == null) {
                return;
            }

            Parser parser = new Parser(tokens);
            Expresion expresion = parser.parse();

            if (expresion == null) {
                System.err.println("Error sintáctico.");
                return;
            }

            Object resultado = interpreter.interpretar(expresion);

            if (resultado != null && !tienePuntoComa) {
                System.out.println(stringify(resultado));
            }

        } catch (Exception ex) {
            System.err.println("Error inesperado: " + ex.getMessage());
        }
    }

    private static String stringify(Object objeto) {
        if (objeto == null) return "null";
        if (objeto instanceof Double) {
            String texto = objeto.toString();
            if (texto.endsWith(".0")) {
                texto = texto.substring(0, texto.length() - 2);
            }
            return texto;
        }
        return objeto.toString();
    }

    static void error(int linea, String mensaje) {
        reportar(linea, "", mensaje);
    }

    private static void reportar(int linea, String posicion, String mensaje) {
        System.err.println(
                "[linea " + linea + "] Error " + posicion + ": " + mensaje
        );
        existenErrores = true;
    }
}