import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interpreter implements Visitor<Object> {

    // Tabla de símbolos para variables
    private final Map<String, Object> variables = new HashMap<>();
    
    // Tabla de funciones predefinidas
    private final Map<String, FuncionPredefinida> funciones = new HashMap<>();

    public Interpreter() {
        // Registrar funciones predefinidas
        funciones.put("rand", new FuncionPredefinida(0) {
            @Override
            public Object llamar(List<Object> argumentos) {
                return Math.random();
            }
        });

        funciones.put("sin", new FuncionPredefinida(1) {
            @Override
            public Object llamar(List<Object> argumentos) {
                verificarNumero(argumentos.get(0), "sin");
                return Math.sin((Double) argumentos.get(0));
            }
        });

        funciones.put("cos", new FuncionPredefinida(1) {
            @Override
            public Object llamar(List<Object> argumentos) {
                verificarNumero(argumentos.get(0), "cos");
                return Math.cos((Double) argumentos.get(0));
            }
        });

        funciones.put("sqrt", new FuncionPredefinida(1) {
            @Override
            public Object llamar(List<Object> argumentos) {
                verificarNumero(argumentos.get(0), "sqrt");
                return Math.sqrt((Double) argumentos.get(0));
            }
        });

        funciones.put("pow", new FuncionPredefinida(2) {
            @Override
            public Object llamar(List<Object> argumentos) {
                verificarNumero(argumentos.get(0), "pow");
                verificarNumero(argumentos.get(1), "pow");
                return Math.pow((Double) argumentos.get(0), (Double) argumentos.get(1));
            }
        });
    }

    // Método principal para interpretar una expresión
    public Object interpretar(Expresion expresion) {
        try {
            return evaluar(expresion);
        } catch (RuntimeError error) {
            System.err.println("[Error de ejecución] " + error.getMessage());
            return null;
        }
    }

    private Object evaluar(Expresion expr) {
        return expr.aceptar(this);
    }

    // ---------------------------------------------------------
    // Implementación del Visitor
    // ---------------------------------------------------------

    public Object visitarExpresionLiteral(Literal expr) {
        return expr.valor;
    }

    public Object visitarExpresionUnaria(Unaria expr) {
        Object derecha = evaluar(expr.derecha);

        switch (expr.operador.tipo) {
            case MINUS:
                verificarNumero(derecha, "operador unario '-'");
                return -(Double) derecha;
        }

        return null;
    }

    public Object visitarExpresionBinaria(Binaria expr) {
        Object izquierda = evaluar(expr.izquierda);
        Object derecha = evaluar(expr.derecha);

        switch (expr.operador.tipo) {
            case MINUS:
                verificarNumeros(izquierda, derecha, "-");
                return (Double) izquierda - (Double) derecha;
            case PLUS:
                // Permite suma de números o concatenación de strings
                if (izquierda instanceof Double && derecha instanceof Double) {
                    return (Double) izquierda + (Double) derecha;
                }
                if (izquierda instanceof String || derecha instanceof String) {
                    return stringify(izquierda) + stringify(derecha);
                }
                throw new RuntimeError("Operandos incompatibles para '+'.");
            case SLASH:
                verificarNumeros(izquierda, derecha, "/");
                if ((Double) derecha == 0) {
                    throw new RuntimeError("División por cero.");
                }
                return (Double) izquierda / (Double) derecha;
            case STAR:
                verificarNumeros(izquierda, derecha, "*");
                return (Double) izquierda * (Double) derecha;
            case MOD:
                verificarNumeros(izquierda, derecha, "%");
                if ((Double) derecha == 0) {
                    throw new RuntimeError("Módulo por cero.");
                }
                return (Double) izquierda % (Double) derecha;
        }

        return null;
    }

    public Object visitarExpresionAgrupacion(Agrupacion expr) {
        return evaluar(expr.expresion);
    }

    public Object visitarExpresionVariable(Variable expr) {
        String nombre = expr.nombre.lexema;
        if (!variables.containsKey(nombre)) {
            throw new RuntimeError("Variable no definida: '" + nombre + "'.");
        }
        return variables.get(nombre);
    }

    public Object visitarExpresionAsignacion(Asignacion expr) {
        Object valor = evaluar(expr.valor);
        variables.put(expr.nombre.lexema, valor);
        return valor;
    }

    public Object visitarExpresionLlamada(Llamada expr) {
        // El llamado debe ser una variable (nombre de función)
        if (!(expr.llamado instanceof Variable)) {
            throw new RuntimeError("Solo se puede llamar a funciones, no a '" + 
                                 stringify(evaluar(expr.llamado)) + "'.");
        }

        String nombreFuncion = ((Variable) expr.llamado).nombre.lexema;

        // Verificar que la función existe
        if (!funciones.containsKey(nombreFuncion)) {
            throw new RuntimeError("Función no definida: '" + nombreFuncion + "'.");
        }

        FuncionPredefinida funcion = funciones.get(nombreFuncion);

        // Evaluar argumentos
        List<Expresion> argumentosExpr = expr.argumentos;
        java.util.ArrayList<Object> argumentos = new java.util.ArrayList<>();
        for (Expresion argExpr : argumentosExpr) {
            argumentos.add(evaluar(argExpr));
        }

        // Verificar número de argumentos
        if (argumentos.size() != funcion.aridad) {
            throw new RuntimeError("La función '" + nombreFuncion + 
                                 "' espera " + funcion.aridad + 
                                 " argumento(s), pero se recibieron " + argumentos.size() + ".");
        }

        // Llamar a la función
        return funcion.llamar(argumentos);
    }

    // ---------------------------------------------------------
    // Métodos auxiliares
    // ---------------------------------------------------------

    private void verificarNumero(Object operando, String operador) {
        if (operando instanceof Double) return;
        throw new RuntimeError("El operando de '" + operador + "' debe ser un número.");
    }

    private void verificarNumeros(Object izq, Object der, String operador) {
        if (izq instanceof Double && der instanceof Double) return;
        throw new RuntimeError("Los operandos de '" + operador + "' deben ser números.");
    }

    private String stringify(Object objeto) {
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

    // ---------------------------------------------------------
    // Clases auxiliares
    // ---------------------------------------------------------

    // Clase base para funciones predefinidas
    private abstract static class FuncionPredefinida {
        final int aridad;

        FuncionPredefinida(int aridad) {
            this.aridad = aridad;
        }

        abstract Object llamar(List<Object> argumentos);
    }

    // Excepción para errores de ejecución
    private static class RuntimeError extends RuntimeException {
        RuntimeError(String mensaje) {
            super(mensaje);
        }
    }
}