#!/bin/bash

set -e

echo "Java..."
javac Interprete.java Scanner.java Token.java TipoToken.java Expresion.java

echo "Compiled"
java Interprete

