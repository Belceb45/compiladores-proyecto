COMPILADORES PROYECTO

La práctica debe contener lo siguiente:
1.-  REPL (Read, Execute, Print, Loop). El código  que se encuentra en la clase Interprete.Java contiene la estructura de un REPL (parcialmente, se irá complementando en las siguientes prácticas). Un REPL es básicamente un ente interactivo que toma entradas, las ejecuta y muestra los resultados al usuario (por ejemplo una terminal, shell, cmd, etc...). En este REPL se presente un prompt ">>> " donde el usuario (ustedes) introducirán una cadena de texto y presionarán enter. El REPL tomará dicha cadena, generará los tokens mediante el scanner y posteriormente realizará el análisis sintáctico.

2.- Scanner. Se creará un analizador léxico que genere los tokens de: suma, resta, multiplicación, división, módulo, paréntesis, coma, punto y coma, identificador, numero, igual, cadena y nulo (el lexema es null). El analizador léxico al final debe agregar un token "fin de cadena" (el código que les compartí ya lo genera). En caso de haber un error, este debe ser mostrado en pantalla y no continuar con el análisis sintáctico.

3.- Parser. Escribir un analizador sintáctico predictivo, que reciba los tokens generados por el scanner y verificar que la instrucción introducida en el prompt sea válida, en caso contrario indicar el error con un mensaje en pantalla.
