import java.util.ArrayList;
import java.util.List;

public class Scanner {

    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    private int start = 0;
    private int current = 0;

    private boolean state_error = false;

    public Scanner(String source) {
        this.source = source;
    }

    public List<Token> scan() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        if (state_error) {
            System.out.println("Error lexico");
            return null;
        }

        tokens.add(new Token(TipoToken.EOF, "$"));
        return tokens;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void scanToken() {
        char c = advance();

        switch (c) {
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

            case '"':
                string();
                break;

            case ' ':
            case '\r':
            case '\t':
            case '\n':
                break;

            default:
                if (isDigit(c)) {
                    number();
                }
                else if (isAlpha(c)) {
                    identifier();
                }
                else {
                    System.out.println("Caracter inesperado: " + c);
                    state_error = true;
                }
                break;
        }
    }

    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(TipoToken tipo) {
        String text = source.substring(start, current);
        tokens.add(new Token(tipo, text));
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private void number() {
        while (!isAtEnd() && isDigit(peek())) {
            advance();
        }

        if (!isAtEnd() && peek() == '.' && isDigit(peekNext())) {
            advance();

            while (!isAtEnd() && isDigit(peek())) {
                advance();
            }
        }

        String num = source.substring(start, current);
        tokens.add(new Token(TipoToken.NUMBER, num));
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

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

    private void string() {
        while (!isAtEnd() && peek() != '"') {
            advance();
        }

        if (isAtEnd()) {
            System.out.println("ERROR: cadena sin cerrar");
            state_error = true;
            return;
        }

        advance();

        String value = source.substring(start, current);
        tokens.add(new Token(TipoToken.STRING, value));
    }

    private char peek() {
        if (isAtEnd())
            return '\0';
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length())
            return '\0';
        return source.charAt(current + 1);
    }
}