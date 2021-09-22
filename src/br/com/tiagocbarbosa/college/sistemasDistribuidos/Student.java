package br.com.tiagocbarbosa.college.sistemasDistribuidos;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Student {
    public static void main(String[] args) throws IOException {
        final String HOST = "127.0.0.1";
        final int PORT = 8080;

        String matricula = "";
        String turma = "";
        String status = "";

        System.out.println("Client started.");

        try (
                Socket socket = new Socket(HOST, PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                Scanner in = new Scanner(socket.getInputStream());
                Scanner s = new Scanner(System.in);
        ) {
            while (true) {
                while (matricula.trim().equals("")) {
                    System.out.print("Informa o número da sua Matrícula: ");
                    matricula = s.nextLine();
                }
                while (turma.trim().equals("")) {
                    System.out.print("Informa o código da sua Turma: ");
                    turma = s.nextLine();
                }
                out.println("ALUNO:" + matricula + ":" + turma);
                System.out.println(in.nextLine());
                break;
            }
        }
    }
}
