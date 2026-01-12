import java.util.List;

abstract class Expresion {
    abstract <R> R aceptar(Visitor<R> visitor);
}

interface Visitor<R> {
    R visitarExpresionLiteral(Literal expr);
    R visitarExpresionUnaria(Unaria expr);
    R visitarExpresionBinaria(Binaria expr);
    R visitarExpresionAgrupacion(Agrupacion expr);
    R visitarExpresionVariable(Variable expr);
    R visitarExpresionAsignacion(Asignacion expr);
    R visitarExpresionLlamada(Llamada expr);
}

class Literal extends Expresion {
    final Object valor;

    Literal(Object valor) {
        this.valor = valor;
    }

    @Override
    <R> R aceptar(Visitor<R> visitor) {
        return visitor.visitarExpresionLiteral(this);
    }
}

class Unaria extends Expresion {
    final Token operador;
    final Expresion derecha;

    Unaria(Token operador, Expresion derecha) {
        this.operador = operador;
        this.derecha = derecha;
    }

    @Override
    <R> R aceptar(Visitor<R> visitor) {
        return visitor.visitarExpresionUnaria(this);
    }
}

class Binaria extends Expresion {
    final Expresion izquierda;
    final Token operador;
    final Expresion derecha;

    Binaria(Expresion izquierda, Token operador, Expresion derecha) {
        this.izquierda = izquierda;
        this.operador = operador;
        this.derecha = derecha;
    }

    @Override
    <R> R aceptar(Visitor<R> visitor) {
        return visitor.visitarExpresionBinaria(this);
    }
}

class Agrupacion extends Expresion {
    final Expresion expresion;

    Agrupacion(Expresion expresion) {
        this.expresion = expresion;
    }

    @Override
    <R> R aceptar(Visitor<R> visitor) {
        return visitor.visitarExpresionAgrupacion(this);
    }
}

class Variable extends Expresion {
    final Token nombre;

    Variable(Token nombre) {
        this.nombre = nombre;
    }

    @Override
    <R> R aceptar(Visitor<R> visitor) {
        return visitor.visitarExpresionVariable(this);
    }
}

class Asignacion extends Expresion {
    final Token nombre;
    final Expresion valor;

    Asignacion(Token nombre, Expresion valor) {
        this.nombre = nombre;
        this.valor = valor;
    }

    @Override
    <R> R aceptar(Visitor<R> visitor) {
        return visitor.visitarExpresionAsignacion(this);
    }
}

class Llamada extends Expresion {
    final Expresion llamado;
    final Token parentesis;
    final List<Expresion> argumentos;

    Llamada(Expresion llamado, Token parentesis, List<Expresion> argumentos) {
        this.llamado = llamado;
        this.parentesis = parentesis;
        this.argumentos = argumentos;
    }

    @Override
    <R> R aceptar(Visitor<R> visitor) {
        return visitor.visitarExpresionLlamada(this);
    }
}