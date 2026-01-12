COMPILADORES PROYECTO

La práctica debe contener lo siguiente:
1.-  REPL (Read, Execute, Print, Loop). El código  que se encuentra en la clase Interprete.Java contiene la estructura de un REPL (parcialmente, se irá complementando en las siguientes prácticas). Un REPL es básicamente un ente interactivo que toma entradas, las ejecuta y muestra los resultados al usuario (por ejemplo una terminal, shell, cmd, etc...). En este REPL se presente un prompt ">>> " donde el usuario (ustedes) introducirán una cadena de texto y presionarán enter. El REPL tomará dicha cadena, generará los tokens mediante el scanner y posteriormente realizará el análisis sintáctico.

2.- Scanner. Se creará un analizador léxico que genere los tokens de: suma, resta, multiplicación, división, módulo, paréntesis, coma, punto y coma, identificador, numero, igual, cadena y nulo (el lexema es null). El analizador léxico al final debe agregar un token "fin de cadena" (el código que les compartí ya lo genera). En caso de haber un error, este debe ser mostrado en pantalla y no continuar con el análisis sintáctico.

3.- Parser. Escribir un analizador sintáctico predictivo, que reciba los tokens generados por el scanner y verificar que la instrucción introducida en el prompt sea válida, en caso contrario indicar el error con un mensaje en pantalla.


## Índice

- [Visión general](#visión-general)  
- [Archivos del proyecto](#archivos-del-proyecto)  
- [Cómo compilar y ejecutar](#cómo-compilar-y-ejecutar)  
- [Comportamiento del REPL](#comportamiento-del-repl)  
- [Diseño y comportamiento del Scanner](#diseño-y-comportamiento-del-scanner)  
- [Tokens y TipoToken](#tokens-y-tipotoken)  
- [Resumen de la gramática](#resumen-de-la-gramática)  
- [Ejemplos de entrada y salida](#ejemplos-de-entrada-y-salida)  
- [Errores y manejo de errores](#errores-y-manejo-de-errores)  
- [Limitaciones y siguientes pasos](#limitaciones-y-siguientes-pasos)  
- [Casos de prueba sugeridos](#casos-de-prueba-sugeridos)

---

## Visión general

Actualmente están implementados:

- REPL (`Interprete.java`) — lee entradas interactivas.  
- Scanner (`Scanner.java`) — análisis léxico sin regex (carácter a carácter).

La siguiente tarea es implementar el **Parser predictivo** basado en la gramática (`Gramatica.pdf`).

---

## Archivos del proyecto

- **Interprete.java**  
  REPL principal. Lee líneas, llama al Scanner y muestra tokens. Si el Scanner detecta error léxico (devuelve `null`), no continúa.

- **Scanner.java** (actualizado)  
  Analizador léxico que reconoce: `NUMBER`, `IDENTIFIER`, `NULL`, `STRING`, operadores (`+ - * / %`), símbolos `(` `)` `,` `;` `=`, y `EOF`. Marca errores léxicos y devuelve `null` si los hay.

- **Token.java**  
  Clase que representa un token: tipo (`TipoToken`), lexema (texto) y campo opcional.

- **TipoToken.java**  
  Enumeración con los tipos de token (ej.: `LEFT_PAREN`, `NUMBER`, `IDENTIFIER`, `STRING`, `NULL`, `EOF`, etc.).

- **Gramatica.pdf**  
  Gramática formal del lenguaje que se utilizará para construir el Parser.

---

## Cómo compilar y ejecutar

```bash
# Compilar (desde la carpeta con los .java)
javac Interprete.java Scanner.java Token.java TipoToken.java Expresion.java

# Ejecutar REPL
java Interprete

