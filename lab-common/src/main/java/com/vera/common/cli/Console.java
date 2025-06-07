package com.vera.common.cli;


import java.util.Scanner;

/**
 * Interface of Console for entering command and displaying the result
 * @author ren1kron
 */
public interface Console {
    void print(Object obj);
    void println(Object obj);
    void printError(Object obj);
    String readln();
    boolean isCanReadln();
    void prompt();
    String getPrompt();
    void selectFileScanner(Scanner obj);
    void selectConsoleScanner();
}