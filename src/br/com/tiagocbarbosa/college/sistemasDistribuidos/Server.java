package br.com.tiagocbarbosa.college.sistemasDistribuidos;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) throws IOException {
        ArrayList<ArrayList<String>> presencas = new ArrayList<ArrayList<String>>();

        final int PORT = 8080;
        ServerSocket serverSocket = new ServerSocket(PORT);

        System.out.println("Server started...");
        System.out.println("Waiting for clients...");

        while (true) {
            Socket clientSocket = serverSocket.accept();

            Thread t = new Thread() {
                int position = -1;
                String presencasConfirmadas = "";
                boolean status;

                public void run() {
                    try (
                            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                            Scanner in = new Scanner(clientSocket.getInputStream());
                    ) {
                        while (in.hasNextLine()) {
                            String input = in.nextLine();
                            if (input.equalsIgnoreCase("exit")) {
                                break;
                            }

                            String[] request = input.split(":");

                            // análises com o PROFESSOR
                            if (request[0].equals("PROFESSOR")) {
                                if (request[1].equals("INICIAR")) {
                                    position = findPosition(presencas, request[2]);

                                    // exibir a abertura da chamada pelo professor
                                    if (position == -1) {
                                        presencas.add(new ArrayList<String>());
                                        presencas.get(presencas.size() - 1).add(request[2]);
                                        String formattedDateTime = getDataHoraAtual();

                                        out.println(String.format("%s, aberta a chamada da turma: %s", formattedDateTime, request[2]));
                                        System.out.println(String.format("%s, aberta a chamada da turma: %s", formattedDateTime, request[2]));
                                    }

                                    // exibir mensagem de turma existente
                                    else {
                                        out.println("TURMA-JA-REGISTRADA");
                                        System.out.println("A turma: " + request[2] + " já existe!");
                                    }
                                }

                                // LISTAR as presenças confirmadas
                                else if (request[1].equals("LISTAR")) {
                                    position = findPosition(presencas, request[2]);
                                    presencasConfirmadas = listarPresentes(presencas, position);

                                    out.println("Turma: " + request[2] + ". Presenças confirmadas: " + presencasConfirmadas);
                                    System.out.println("Turma: " + request[2] + ". Presenças confirmadas: " + presencasConfirmadas);
                                }

                                // ENCERRAR a chamada aberta
                                else if (request[1].equals("ENCERRAR")) {
                                    position = findPosition(presencas, request[2]);
                                    presencasConfirmadas = listarPresentes(presencas, position);
                                    String formattedDateTime = getDataHoraAtual();

                                    out.println(String.format("%s, encerrada a chamada da turma: %s. Presenças confirmadas: %s", formattedDateTime, request[2], presencasConfirmadas));
                                    System.out.println(String.format("%s, encerrada a chamada da turma: %s. Presenças confirmadas: %s", formattedDateTime, request[2], presencasConfirmadas));

                                    // remove a chamada da lista de chamadas/presenças
                                    presencas.remove(position);
                                }
                            }

                            // análises com o ALUNO
                            else if (request[0].equals("ALUNO")) {
                                position = findPosition(presencas, request[2]);

                                if (position != -1) {
                                    status = confirmarPresenca(presencas, position, request[1]);

                                    // exibir mensagem caso possuir presença para a matrícula
                                    if(!status) {
                                        out.println("Confirmação de presença: " + input + " status: JÁ EXISTE PRESENÇA PARA ESSA MATRÍCULA!");
                                        System.out.println("Confirmação de presença: " + input + " status: JÁ EXISTE PRESENÇA PARA ESSA MATRÍCULA!");
                                    }

                                    // exibir mensagem de presença
                                    else {
                                        String fomattedDateTime = getDataHoraAtual();

                                        out.println(String.format("%s, confirmação de presença %s status: %s", fomattedDateTime, input, status));
                                        System.out.println(String.format("%s, confirmação de presença %s status: %s", fomattedDateTime, input, status));
                                    }
                                }

                                // exibir mensagem caso a turma não exista
                                else {
                                    String fomattedDateTime = getDataHoraAtual();

                                    out.println(String.format("%s, confirmação de presença %s status: TURMA NÃO ENCONTRADA!", fomattedDateTime, input));
                                    System.out.println(String.format("%s, confirmação de presença %s status: TURMA NÃO ENCONTRADA!", fomattedDateTime, input));
                                }
                            }
                            System.out.println(presencas);
                        }
                    } catch (IOException e) {
                        System.out.println(e);
                    }
                }

                // busca POSIÇÃO da turma na lista
                private int findPosition(ArrayList<ArrayList<String>> presencas, String turma) {
                    int position = -1;

                    for (int cont = 0; cont < presencas.size(); cont++)
                        if (turma.equals(presencas.get(cont).get(0)))
                            position = cont;

                    return position;
                }

                // LISTAR as presenças das matrículas
                private String listarPresentes(ArrayList<ArrayList<String>> presencas, int position) {
                    ArrayList<String> presentes = presencas.get(position);
                    StringBuilder alunosPresentes = new StringBuilder();

                    for (int cont = 1; cont < presentes.size(); cont++) {
                        alunosPresentes.append(presentes.get(cont)).append("; ");
                    }

                    return alunosPresentes.toString();
                }

                // analisa se existe a PRESENÇA na chamada
                private boolean confirmarPresenca(ArrayList<ArrayList<String>> presencas, int position, String matricula) {
                    ArrayList<String> presentes = presencas.get(position);
                    boolean exists = false;

                    for (int cont = 1; cont < presentes.size(); cont++) {
                        if (matricula.equals(presentes.get(cont))) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists)
                        return presencas.get(position).add(matricula);
                    return false;
                }

                // retorna DATA e HORA atual
                private String getDataHoraAtual() {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

                    return LocalDateTime.now().format(formatter);
                }
            };
            t.start();
        }
    }
}
