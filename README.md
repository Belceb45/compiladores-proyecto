COMPILADORES PROYECTO

La práctica debe contener lo siguiente:
1.-  REPL (Read, Execute, Print, Loop). El código  que se encuentra en la clase Interprete.Java contiene la estructura de un REPL (parcialmente, se irá complementando en las siguientes prácticas). Un REPL es básicamente un ente interactivo que toma entradas, las ejecuta y muestra los resultados al usuario (por ejemplo una terminal, shell, cmd, etc...). En este REPL se presente un prompt ">>> " donde el usuario (ustedes) introducirán una cadena de texto y presionarán enter. El REPL tomará dicha cadena, generará los tokens mediante el scanner y posteriormente realizará el análisis sintáctico.

2.- Scanner. Se creará un analizador léxico que genere los tokens de: suma, resta, multiplicación, división, módulo, paréntesis, coma, punto y coma, identificador, numero, igual, cadena y nulo (el lexema es null). El analizador léxico al final debe agregar un token "fin de cadena" (el código que les compartí ya lo genera). En caso de haber un error, este debe ser mostrado en pantalla y no continuar con el análisis sintáctico.

3.- Parser. Escribir un analizador sintáctico predictivo, que reciba los tokens generados por el scanner y verificar que la instrucción introducida en el prompt sea válida, en caso contrario indicar el error con un mensaje en pantalla.


Índice:

 -Visión general
 -Archivos del proyecto (qué hacen)
 -Cómo compilar / ejecutar
 -Comportamiento del REPL (Interprete.java)
 -Diseño y comportamiento del Scanner (archivo actualizado)
 -Token y TipoToken — formato y significado
 -Gramática (resumen)
 -Ejemplos de entradas y salidas (casos típicos)
 -Errores y manejo de errores (léxico / sintáctico esperado)
 -Limitaciones actuales y pasos siguientes recomendados
 -Sugerencias de pruebas unitarias y casos de prueba


1. Visión general

Este proyecto implementa la primera capa de un intérprete: lectura de entrada (REPL) y análisis léxico (Scanner).
Objetivo final previsto por la práctica: REPL → Scanner → Parser (predictivo) → ejecución/interpretación. Actualmente solo está implementado REPL + Scanner; el Parser falta por crear.

2. Archivos del proyecto (qué hacen)

Interprete.java
REPL principal. Lee líneas desde stdin, llama al Scanner y (cuando exista) al Parser. Está preparado para no continuar cuando el Scanner reporta error léxico (devuelve null).

Scanner.java (actualizado)
Analizador léxico implementado sin regex (carácter a carácter). Reconoce: NUMBER, IDENTIFIER, NULL, STRING (comillas dobles), operadores + - * / %, ( ) , ; =, y EOF. Marca errores léxicos (carácter inesperado, cadena sin cerrar) y, en caso de error, retorna null.

Token.java
Representa un token: tipo (TipoToken), lexema (texto) y campo opcional (no usado actualmente, pero preparado).

TipoToken.java
Enumeración de tipos de token: LEFT_PAREN, RIGHT_PAREN, COMMA, SEMICOLON, MINUS, PLUS, SLASH, STAR, MOD, BANG, BANG_EQUAL, EQUAL, EQUAL_EQUAL, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL, IDENTIFIER, NUMBER, STRING, NULL, EOF (ajusta si no están las últimas entradas).

Gramatica.pdf
Documento con la gramática formal (producciones). Define STATEMENT, EXPRESSION, ASSIGNMENT, TERM, FACTOR, UNARY, CALL, PRIMARY, ARGUMENTS, etc.

3. Cómo compilar / ejecutar

Desde la carpeta que contiene los .java:

# compilar
javac Interprete.java Scanner.java Token.java TipoToken.java

# ejecutar
java Interprete


El REPL mostrará >>> esperando entrada. Presiona Ctrl+D (o EOF) para salir.

4. Comportamiento del REPL (Interprete.java)

Lee una línea completa con BufferedReader.readLine().

Llama Scanner scanner = new Scanner(source); List<Token> tokens = scanner.scan();

Si tokens == null (el scanner detectó error léxico), no procede: retorna sin intentar parsear.

Si recibe tokens, actualmente imprime cada token (System.out.println(token)).

Reinicia bandera de errores globales entre iteraciones.

Punto importante: al integrar el Parser, el flujo correcto será:

Scanner → si tokens == null ⇒ mostrar error y volver a prompt.

Parser → si error sintáctico ⇒ mostrar mensaje y volver a prompt.

(Opcional) Evaluador/ejecutor si la sintaxis es válida.

5. Diseño y comportamiento del Scanner (archivo actualizado)

Idea general: escanea source carácter por carácter con índices start y current. Genera tokens y los añade a tokens. Si detecta cualquier error léxico, marca state_error = true y al terminar scan() devuelve null.

Variables clave

source — cadena completa recibida desde REPL.

tokens — lista a llenar.

start — índice donde comienza el lexema actual.

current — índice del carácter actual (siguiente por leer).

state_error — boolean; si true, scan() retornará null.

Métodos principales

scan() — bucle principal: mientras no sea fin, start = current; scanToken();. Al final: si state_error → imprime Error lexico y devuelve null; si no → añade EOF y devuelve lista.

isAtEnd() — verifica fin de source.

scanToken() — lee char c = advance() y usa switch para casos simples (símbolos/operadores). Para números/identificadores/strings llama a rutinas auxiliares. Si encuentra char no reconocido, imprime mensaje y pone state_error = true.

advance() — consume y retorna el carácter actual.

peek() — mira el siguiente carácter sin consumir.

addToken(TipoToken tipo) — toma substring desde start hasta current y crea Token.

number() — consume dígitos [0-9]+ y agrega token NUMBER.

identifier() — consume [a-zA-Z_][a-zA-Z0-9_]*. Si lexema == "null" agrega NULL, si no IDENTIFIER.

string() — consume hasta próxima "; si no encuentra cierre marca state_error = true y reporta ERROR: cadena sin cerrar. Si encuentra, consume la comilla final y agrega STRING (incluye las comillas en el lexema).

Reglas léxicas aplicadas

Identificadores: [a-zA-Z_][a-zA-Z0-9_]*

Números: [0-9]+ (solo enteros)

Strings: "[^"]*" (sin escapes)

Palabra reservada: null → token NULL

Espacios (incluyendo \n, \t, \r) son ignorados

Cualquier carácter no contemplado → error léxico (se muestra con Caracter inesperado: X)

6. Token y TipoToken — formato y significado

Token.toString() imprime en formato: <TIPO lexema opcional>
Ejemplos:

<NUMBER 123>

<IDENTIFIER suma>

<STRING "hola">

<NULL null>

<EOF $>

TipoToken (debe contener al menos):

LEFT_PAREN, RIGHT_PAREN, COMMA, SEMICOLON,
MINUS, PLUS, SLASH, STAR, MOD,
EQUAL,
IDENTIFIER, NUMBER, STRING, NULL,
EOF


Ajustar TipoToken si agregas operadores dobles (==, !=, >=, <=) u otros tokens.

7. Gramática (resumen)

Producciones principales (resumidas):

STATEMENT -> EXPRESSION SEMICOLON_OPC
SEMICOLON_OPC -> ; | ε

EXPRESSION -> ASSIGNMENT
ASSIGNMENT -> TERM ASSIGNMENT_OPC
ASSIGNMENT_OPC -> = EXPRESSION | ε

TERM -> FACTOR TERM'
TERM' -> + TERM | - TERM | ε

FACTOR -> UNARY FACTOR'
FACTOR' -> * FACTOR | / FACTOR | % FACTOR | ε

UNARY -> - UNARY | CALL
CALL -> PRIMARY CALL'
CALL' -> ( ARGUMENTS ) | ε

PRIMARY -> null | number | string | id | ( EXPRESSION )

ARGUMENTS -> EXPRESSION ARGUMENTS' | ε
ARGUMENTS' -> , EXPRESSION ARGUMENTS' | ε


Nota: esta gramática establece precedencias: * / % > + - > asignación, admite llamadas id(...), null, paréntesis y literales.

8. Ejemplos de entradas y salidas
Entrada (válida léxicamente)
>>> suma(2, 3);


Scanner → tokens:

<IDENTIFIER suma>
<LEFT_PAREN (>
<NUMBER 2>
<COMMA ,>
<NUMBER 3>
<RIGHT_PAREN )>
<SEMICOLON ;>
<EOF $>


Parser: aún no implementado (a futuro debería aceptar y construir AST).

Entrada con falta de cierre
>>> suma(null,5


Scanner → tokens:

<IDENTIFIER suma> <LEFT_PAREN (>
<NULL null> <COMMA ,> <NUMBER 5> <EOF $>


Parser → debe reportar error sintáctico: falta ) antes de EOF.

Entrada con caracteres inválidos (léxico)
>>> char x='a';


Scanner → salidas:

<IDENTIFIER char> <IDENTIFIER x> <EQUAL => 
Caracter inesperado: '
Caracter inesperado: '
Error lexico


Scanner retorna null; Interprete no intenta parsear; REPL sigue corriendo.

9. Errores y manejo de errores
Errores léxicos (actualmente manejados)

Carácter inesperado: el Scanner imprime Caracter inesperado: X y marca state_error.

Cadena sin cerrar: imprime ERROR: cadena sin cerrar y marca state_error.

Resultado: scan() imprime Error lexico y devuelve null. El REPL (Interprete) detecta tokens == null y no ejecuta el parser.

Errores sintácticos (pendiente)

Debe implementarse en Parser: mensajes del tipo:

Error sintáctico: se esperaba ')' pero se encontró 'EOF'

Error sintáctico: token inesperado IDENTIFIER

Recomendación: usar Interprete.error(linea, mensaje) para reportes con formato uniforme.

10. Limitaciones actuales y pasos siguientes recomendados

Limitaciones

No hay Parser implementado.

Strings no manejan escapes (\", \\, \n).

No hay soporte para comentarios.

number() solo reconoce enteros (no flotantes).

No verificación semántica (ej. asignar a null como LHS).

string() incluye las comillas en el lexema (si quieres sólo el contenido, ajustar).

Siguientes pasos recomendados (en orden)

Implementar Parser predictivo que siga la gramática del PDF (crear Parser.java con métodos: statement(), expression(), assignment(), term(), factor(), unary(), call(), primary(), arguments()).

Conectar Parser al REPL: si tokens != null → Parser parser = new Parser(tokens) → parser.parse(); capturar errores sintácticos y usar Interprete.error(...).

(Opcional) Implementar AST y evaluador/interprete para ejecutar expresiones.

Mejoras al Scanner: escapes en strings, literales flotantes, comentarios // o /* */.

Añadir tests unitarios automatizados (ver sección 11).

11. Pruebas sugeridas (unitarias y de integración)

Casos de prueba del Scanner

2 + 3 * 5; → vérificar tokens: NUMBER, PLUS, NUMBER, STAR, NUMBER, SEMICOLON, EOF.

suma(10, "hola", null); → IDENTIFIER, LPAREN, NUMBER, COMMA, STRING, COMMA, NULL, RPAREN, SEMICOLON, EOF.

xyz_1 = 42 → IDENTIFIER, EQUAL, NUMBER, EOF.

"cadena sin cerrar → debe marcar ERROR: cadena sin cerrar y scan() devuelve null.

@ → Caracter inesperado: @ y scan() devuelve null.

1a → NUMBER(1), IDENTIFIER(a) — correcto según las reglas actuales.

Casos de prueba del Parser (cuando esté)

2 + 3 * 5 → aceptado.

x = 1 + 2 → aceptado.

suma(1,2) → aceptado.

suma(1, → error sintáctico por ) faltante.

null = 3 → sintácticamente aceptado por la gramática (pero semánticamente extraño).
