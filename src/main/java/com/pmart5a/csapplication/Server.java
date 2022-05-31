package com.pmart5a.csapplication;

import com.pmart5a.myclasses.FibonacciNumbers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Server {
    public static final int PORT = 24321;

    private static void log(String message) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        System.out.printf("[%s] %s\n", dtf.format(LocalDateTime.now()), message);
    }

    private static String formResponse(String clientMessage) {
        try {
            int serialNumber = Integer.parseInt(clientMessage);
            if (serialNumber <= 0) {
                return String.format("Введённое значение '%s' меньше или равно 0.", clientMessage);
            } else {
                BigInteger[] nFib = FibonacciNumbers.calculateTermFibonacciSeries(serialNumber);
                return String.format("%s член ряда Фибоначчи: для ряда, начинающегося с 0 равен %d; для ряда," +
                                " начинающегося с 1 равен %d.", clientMessage, nFib[0], nFib[1]);
            }
        } catch (NumberFormatException e) {
            return String.format("Введённое значение '%s' некорректно.", clientMessage);
        }
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            log("Server start");
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                    log("Client connected: " + clientSocket.getRemoteSocketAddress());
                    out.println("Вас приветствует сервис 'тяжёлых' вычислений N-го члена ряда Фибоначчи.");
                    String clientMessage;
                    while (true) {
                        out.println("Введите число N > 0 или 'выход' для выхода:");
                        clientMessage = in.readLine();
                        if (clientMessage.equals("выход") || clientMessage == null) {
                            break;
                        } else {
                            out.println(formResponse(clientMessage));
                        }
                    }
                    log("Client disconnected: " + clientSocket.getRemoteSocketAddress());
                } catch (IOException e) {
                    e.printStackTrace(System.out);
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }
}