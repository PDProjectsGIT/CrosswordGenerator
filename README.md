# Crossword Generator in Java 

## Description
This project aims to create a crossword puzzle generator in Java. It consists of two main model packages: 

### 1) Crossword
- Implements expandable arrays and matrices for efficient crossword creation.
- Utilizes algorithms for crossword generation based on a dynamic matrix.
- Provides public interfaces for accessing crossword functionality.
- Includes a factory class for creating crosswords and returning appropriate interfaces. 

### 2) SJPGameParser
- Contains a class for parsing data (words and definitions) from the Polish language dictionary website sjp.pl.
- Allows for the selection of random words and their definitions.
- Assists in testing the crossword generator. 

## Key Features
- Dynamic matrix implementation for efficient crossword creation.
- Crossword generation algorithms inspired by [source](https://www.baeldung.com/cs/generate-crossword-puzzle)
- Parsing functionality for obtaining words and definitions. 

## Status
This project is currently in progress. I am currently working on parallelizing part of the algorithm. 

## Usage
- Clone the repository to your local machine.
- Compile and run the Java code.
- Use the provided interfaces for generating crosswords and accessing word definitions.
