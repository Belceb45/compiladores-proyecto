import java.util.ArrayList;
import java.util.List;

public class Scanner {

    // Código fuente a analizar
    private final String source;
    // Lista de tokens generados
    private final List<Token> tokens = new ArrayList<>();

    // Índices para recorrer el código
    private int start = 0;
    private int current = 0;

    // Indica si ocurrió un error léxico
    private boolean state_error = false;

    public Scanner(String source) {
        this.source = source;
    }

    // Escanea toda la entrada y genera la lista de tokens
    public List<Token> scan() {
        while (!isAtEnd()) {
            start = current;
            scanToken(); // Analiza un token por iteración
        }

        // Si hubo error léxico, no continuar con el parser
        if (state_error) {
            System.out.println("Error lexico");
            return null;
        }

        // Agregar token de fin de cadena
        tokens.add(new Token(TipoToken.EOF, "$"));
        return tokens;
    }

    // Verdadero cuando se llegó al final del texto
    private boolean isAtEnd() {
        return current >= source.length();
    }

    // Clasifica el siguiente carácter y genera el token correspondiente
    private void scanToken() {
        char c = advance();

        switch (c) {
            // Operadores aritméticos y símbolos sencillos
            case '+':
                addToken(TipoToken.PLUS);
                break;
            case '-':
                addToken(TipoToken.MINUS);
                break;
            case '*':
                addToken(TipoToken.STAR);
                break;
            case '/':
                addToken(TipoToken.SLASH);
                break;
            case '%':
                addToken(TipoToken.MOD);
                break;

            // Paréntesis y separadores
            case '(':
                addToken(TipoToken.LEFT_PAREN);
                break;
            case ')':
                addToken(TipoToken.RIGHT_PAREN);
                break;
            case ';':
                addToken(TipoToken.SEMICOLON);
                break;
            case ',':
                addToken(TipoToken.COMMA);
                break;
            case '=':
                addToken(TipoToken.EQUAL);
                break;

            // Inicia cadena
            case '"':
                string();
                break;

            // Ignorar espacios en blanco
            case ' ':
            case '\r':
            case '\t':
            case '\n':
                break;

            default:
                // Número
                if (isDigit(c)) {
                    number();
                }
                // Identificador o palabra reservada
                else if (isAlpha(c)) {
                    identifier();
                }
                // Símbolo inválido
                else {
                    System.out.println("Caracter inesperado: " + c);
                    state_error = true;
                }
                break;
        }
    }

    // Avanza una posición en el texto y regresa el carácter leído
    private char advance() {
        return source.charAt(current++);
    }

    // Agrega un token utilizando el texto leído desde 'start' a 'current'
    private void addToken(TipoToken tipo) {
        String text = source.substring(start, current);
        tokens.add(new Token(tipo, text));
    }

    // Verifica si el carácter es un dígito
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    // Reconoce un número entero
    private void number() {
        while (!isAtEnd() && isDigit(peek())) {
            advance();
        }
        String num = source.substring(start, current);
        tokens.add(new Token(TipoToken.NUMBER, num));
    }

    // Verifica si es letra o guion bajo (inicio de identificador)
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    // Letras o dígitos (continuación de identificador)
    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    // Reconoce identificadores y la palabra reservada "null"
    private void identifier() {
        while (isAlphaNumeric(peek()))
            advance();

        String lexema = source.substring(start, current);

        if (lexema.equals("null")) {
            tokens.add(new Token(TipoToken.NULL, lexema));
        } else {
            tokens.add(new Token(TipoToken.IDENTIFIER, lexema));
        }
    }

    // Reconoce cadenas entre comillas dobles
    private void string() {
        while (!isAtEnd() && peek() != '"') {
            advance();
        }

        // Si nunca se cerró la comilla
        if (isAtEnd()) {
            System.out.println("ERROR: cadena sin cerrar");
            state_error = true;
            return;
        }

        // Consumir la comilla de cierre
        advance();

        String value = source.substring(start, current);
        tokens.add(new Token(TipoToken.STRING, value));
    }

    // Mira el carácter actual sin consumirlo
    private char peek() {
        if (isAtEnd())
            return '\0';
        return source.charAt(current);
    }
}
