package br.com.tiagocbarbosa.college.sistemasDistribuidos;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Teacher {
    public static void main(String[] args) throws IOException {
        final String HOST = "127.0.0.1";
        final int PORT = 8080;

        String turma = "";
        String status = "";
        String resultado = "";

        System.out.println("Client started.");

        try (
                Socket socket = new Socket(HOST, PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                Scanner in = new Scanner(socket.getInputStream());
                Scanner s = new Scanner(System.in);
        ) {
            while (true) {
                // solicitar LISTAR ou ENCERRAR a chamada
                while ((!status.trim().equals("FIM")) && (!turma.trim().equals(""))) {
                    System.out.print("Digite LISTAR para ver uma listagem dos alunos com presença confirmada ou FIM para encerrar a chamada: ");
                    status = s.nextLine();

                    if (status.equals("LISTAR")) {
                        out.println("PROFESSOR:LISTAR:" + turma);
                        System.out.println(in.nextLine());
                    } else if (status.equals("FIM")) {
                        out.println("PROFESSOR:ENCERRAR:" + turma);
                        System.out.println(in.nextLine());
                    }
                }
                // solicitar o código da turma
                while (turma.trim().equals("")) {
                    System.out.print("Informa o código da sua Turma: ");
                    turma = s.nextLine();
                }
                if (status.equalsIgnoreCase("FIM")) {
                    break;
                }
                out.println("PROFESSOR:INICIAR:" + turma);
                resultado = in.nextLine();
                System.out.println(resultado);

                if (resultado.equals("TURMA-JA-REGISTRADA"))
                    turma = "";
            }
        }
    }
}
