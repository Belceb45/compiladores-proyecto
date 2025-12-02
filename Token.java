public class Token {

    final TipoToken tipo;
    final String lexema;
    final Object opcional;

    public Token(TipoToken tipo, String lexema) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.opcional = null;
    }

    public Token(TipoToken tipo, String lexema, Object opcional) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.opcional = opcional;
    }

    public TipoToken getTipo() {
        return tipo;
    }

    public String getLexema() {
        return lexema;
    }

    public Object getOpcional() {
        return opcional;
    }

    public String toString() {
        return "<" + tipo + " " + lexema + (opcional != null ? opcional : "") + " >";
    }
}
