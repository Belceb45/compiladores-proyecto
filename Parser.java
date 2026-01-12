import java.util.ArrayList;
import java.util.List;

public class Parser {

    private final List<Token> tokens;
    private int current = 0;
    private boolean error = false;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Expresion parse() {
        try {
            return statement();
        } catch (ParseError e) {
            return null;
        }
    }

    private Expresion statement() {
        Expresion expr = expression();

        if (match(TipoToken.SEMICOLON)) {
        }
        return expr;
    }

    private Expresion expression() {
        return assignment();
    }

    private Expresion assignment() {
        Expresion expr = term();

        if (match(TipoToken.EQUAL)) {
            Token igual = previous();
            Expresion valor = assignment();

            if (expr instanceof Variable) {
                Token nombre = ((Variable) expr).nombre;
                return new Asignacion(nombre, valor);
            }

            error(igual, "Lado izquierdo de asignación inválido.");
            throw new ParseError();
        }

        return expr;
    }

    private Expresion term() {
        Expresion expr = factor();

        while (match(TipoToken.PLUS, TipoToken.MINUS)) {
            Token operador = previous();
            Expresion derecha = factor();
            expr = new Binaria(expr, operador, derecha);
        }

        return expr;
    }

    private Expresion factor() {
        Expresion expr = unary();

        while (match(TipoToken.STAR, TipoToken.SLASH, TipoToken.MOD)) {
            Token operador = previous();
            Expresion derecha = unary();
            expr = new Binaria(expr, operador, derecha);
        }

        return expr;
    }

    private Expresion unary() {
        if (match(TipoToken.MINUS)) {
            Token operador = previous();
            Expresion derecha = unary();
            return new Unaria(operador, derecha);
        }
        return call();
    }

    private Expresion call() {
        Expresion expr = primary();

        if (match(TipoToken.LEFT_PAREN)) {
            Token parentesis = previous();
            List<Expresion> argumentos = arguments();
            consume(TipoToken.RIGHT_PAREN, "Se esperaba ')' después de argumentos.");
            expr = new Llamada(expr, parentesis, argumentos);
        }

        return expr;
    }

    private Expresion primary() {
        if (match(TipoToken.NUMBER)) {
            String lexema = previous().lexema;
            return new Literal(Double.parseDouble(lexema));
        }

        if (match(TipoToken.STRING)) {
            String lexema = previous().lexema;
            String valor = lexema.substring(1, lexema.length() - 1);
            return new Literal(valor);
        }

        if (match(TipoToken.NULL)) {
            return new Literal(null);
        }

        if (match(TipoToken.IDENTIFIER)) {
            return new Variable(previous());
        }

        if (match(TipoToken.LEFT_PAREN)) {
            Expresion expr = expression();
            consume(TipoToken.RIGHT_PAREN, "Se esperaba ')' después de la expresión.");
            return new Agrupacion(expr);
        }

        error(peek(), "Expresión inválida.");
        throw new ParseError();
    }

    private List<Expresion> arguments() {
        List<Expresion> argumentos = new ArrayList<>();

        if (!check(TipoToken.RIGHT_PAREN)) {
            argumentos.add(expression());
            while (match(TipoToken.COMMA)) {
                argumentos.add(expression());
            }
        }

        return argumentos;
    }

    private boolean match(TipoToken... tipos) {
        for (TipoToken t : tipos) {
            if (check(t)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private Token consume(TipoToken tipo, String mensaje) {
        if (check(tipo)) return advance();

        error(peek(), mensaje);
        throw new ParseError();
    }

    private boolean check(TipoToken tipo) {
        if (isAtEnd()) return false;
        return peek().tipo == tipo;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().tipo == TipoToken.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private void error(Token token, String mensaje) {
        System.err.println("[Error] en '" + token.lexema + "': " + mensaje);
        this.error = true;
    }

    private static class ParseError extends RuntimeException {}
}