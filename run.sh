#!/bin/bash

# Define the classpath including the dependencies and the application jar
CLASSPATH="./target/dependency/*:./target/springsalad-0.0.1-SNAPSHOT.jar"

# Define the main class
MAIN_CLASS="org.springsalad.langevinsetup.MainGUI"

# Run the application with the specified VM argument
java --add-exports java.desktop/sun.awt=ALL-UNNAMED -cp $CLASSPATH $MAIN_CLASS
