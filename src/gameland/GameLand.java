package gameland;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Formatter;
import java.util.Scanner;

/**
 *
 * @author Nuno Costa 1171584
 */
public class GameLand {

    private final static int MAX_PARTICIPANTES = 30;
    private final static int N_CAMPOS_INFO = 4;
    private final static int N_JOGOS = 6;
    private final static int MAX_LINHAS_PAGINA = 3;
    private final static String SEPARADOR_DADOS_FICH = ";";
    private final static Scanner in = new Scanner(System.in);

    private static int menu() {
        String texto = "\nMENU:"
                + "\n1- Ler Jogos"
                + "\n2- Ver Jogos"
                + "\n3- Ler Equipa"
                + "\n4- Ver Participantes"
                + "\n5- Alterar Dados"
                + "\n6- Ler Resultados"
                + "\n7- Guardar Participantes e Pontos"
                + "\n8- Calcular Prémios"
                + "\n9- Remover Equipa"
                + "\n10- Ver Prémios de Equipa"
                + "\n11- Ver Estatísticas por Jogo"
                + "\n12- Gravar Dados"
                + "\n0- FIM"
                + "\nQual a sua opção?";
        System.out.printf("%n%s%n", texto);
        int op = in.nextInt();
        in.nextLine();
        return op;
    }

    public static void main(String[] args) throws FileNotFoundException {
        int nParticipantes = 0, escolha;
        String nomeFich, data = null, gameID, equipa;
        String[] jogos = new String[N_JOGOS];
        String[][] participantes = new String[MAX_PARTICIPANTES][N_CAMPOS_INFO];
        int[][] pontos = new int[MAX_PARTICIPANTES][N_JOGOS];
        double[][] prémios = new double[MAX_PARTICIPANTES][N_JOGOS];
        int op;
        do {
            op = menu();
            switch (op) {
                case 1:
                    System.out.println("Qual a data do evento (AAAAMMDD)?");
                    nomeFich = in.nextLine();
                    if (carregarJogosDoEvento(nomeFich + ".txt", jogos)) {
                        System.out.println("Jogos carregados com sucesso");
                    } else {
                        System.out.println("Erro no carregamento dos jogos. Verifique ficheiro");
                    }
                    break;
                case 2:
                    visualizarInfoJogos(jogos);
                    break;
                case 3:
                    System.out.println("Qual o nome do ficheiro?");
                    nomeFich = in.nextLine();
                    int i = nParticipantes;
                    nParticipantes = lerInfoFicheiro(nomeFich + ".txt", participantes, nParticipantes);
                    if (i != nParticipantes) {
                        System.out.println("Equipa carregada com sucesso");
                    } else {
                        System.out.println("Erro no carregamento da equipa. Verifique ficheiro");
                    }
                    break;
                case 4:
                    listagemPaginada(participantes, nParticipantes);
                    break;
                case 5:
                    System.out.println("Qual o e-mail do participante?");
                    String email = in.nextLine();
                    if (actualizarInfoParticipante(email, participantes, nParticipantes)) {
                        System.out.println("Operação bem sucedida.");
                    }
                    break;
                case 6:
                    System.out.println("Qual a data do evento (AAAAMMDD)?");
                    data = in.nextLine();
                    System.out.println("Qual o código de jogo?");
                    gameID = in.nextLine();
                    guardarResultadosJogo(data, gameID, pontos, jogos, participantes, nParticipantes);
                    break;
                case 7:
                    guardarDadosPP(participantes, pontos, nParticipantes);
                    break;
                case 8:
                    if (nParticipantes > 0) {
                        calcularPrémios(pontos, jogos, prémios, nParticipantes, participantes, data);
                        System.out.println("Sucesso");
                    } else {
                        System.out.println("Não existem dados necessários");
                    }
                case 9:
                    System.out.println("Insira nome da equipa a eliminar");
                    equipa = in.nextLine();
                    nParticipantes = removerEquipa(equipa, pontos, prémios, participantes, nParticipantes);
                    break;
                case 10:
                    System.out.println("Insira nome da equipa");
                    equipa = in.nextLine();
                    listarEquipa(equipa, participantes, prémios, nParticipantes);
                    break;
                case 11:
                    info(nParticipantes, pontos, prémios);
                    break;
                case 12:
                    do {
                        System.out.println("Prima '1' se quiser listar para o ecrã. Prima '2' se quiser listar para um ficheiro");
                        escolha = in.nextInt();
                    } while (escolha != 1 && escolha != 2);
                    in.nextLine();
                    listagemPremios(nParticipantes, participantes, prémios, escolha);
                    break;
                case 0:
                    System.out.println("Já fez todas as gravações necessárias? Confirma terminar(s/n)?");
                    char resp = (in.next()).charAt(0);
                    if (resp != 's' && resp != 'S') {
                        op = 1;
                    }
                    break;
                default:
                    System.out.println("Opção incorreta. Repita");
                    break;
            }
        } while (op != 0);
    }

    private static boolean carregarJogosDoEvento(String nomeFichJogos, String[] jogos)
            throws FileNotFoundException {
        Scanner fInput = new Scanner(new File(nomeFichJogos));
        int i = 0;
        while (fInput.hasNextLine() && i < N_JOGOS) {
            String linha = fInput.nextLine();
            if ((linha.trim()).length() > 0) {
                jogos[i] = linha;
                i++;
            }
        }
        fInput.close();
        return i == N_JOGOS;
    }

    private static void visualizarInfoJogos(String[] jogos) {
        System.out.println("Jogos do evento");
        System.out.printf("%15s%15s%15s%n", "ID do jogo", "Tipo de jogo", "Max. de pontos");
        for (int i = 0; i < jogos.length; i++) {
            String[] temp = jogos[i].split("-");
            System.out.printf("%15s%15s%15s%n", temp[0], temp[1], temp[2]);
        }
    }

    /**
     * Carrega informação do ficheiro de texto para memória
     *
     * @param nomeFich - nome do ficheiro que contem a informação a guardar
     * @param info - matriz de strings para guardar a info do ficheiro
     * @param nElems - número de elementos já existentes na matriz
     * @return o número final de elementos na matriz
     * @throws FileNotFoundException
     */
    public static int lerInfoFicheiro(String nomeFich, String[][] info, int nElems) throws
            FileNotFoundException {
        Scanner fInput = new Scanner(new File(nomeFich));
        int nElemsInic = nElems;
        while (fInput.hasNext() && nElems < MAX_PARTICIPANTES) {
            String linha = fInput.nextLine();
            // Verifica se linha não está em branco
            if (linha.trim().length() > 0) {
                nElems = guardarDados(linha, info, nElems);
            }
        }
        fInput.close();
        if (nElems - nElemsInic != 3) {
            nElems = nElemsInic;
        }
        return nElems;
    }

    /**
     * Acede à informação de uma linha do ficheiro e guarda-a na estrutura dados
     * se ainda não existe linha com aquele valor no 1º elemento
     *
     * @param linha - String com o conteúdo de uma linha do ficheiro
     * @param info - matriz de strings com a informação lida do ficheiro
     * @param nElems - número de elementos existentes na matriz
     * @param return - o novo número de elementos da matriz
     */
    private static int guardarDados(String linha, String[][] info, int nElems) {
        String[] temp = linha.split(SEPARADOR_DADOS_FICH);
        if (temp.length == N_CAMPOS_INFO) {
            String num = temp[0].trim();
            int pos = pesquisarElemento(num, info, nElems);
            if (pos == -1) {
                for (int i = 0; i < N_CAMPOS_INFO; i++) {
                    info[nElems][i] = temp[i].trim();
                }
                nElems++;
            }
        }
        return nElems;
    }

    /**
     * Pesquisar linha de matriz por primeiro elemento da linha
     *
     * @param valor - elemento a pesquisar
     * @param nEl - nº de elementos da matriz
     * @param mat - matriz com a informação
     * @return -1 se não existe linha com esse valor ou o nº da linha cujo
     * primeiro elemento é esse valor
     */
    public static int pesquisarElemento(String valor, String[][] mat, int nEl) {
        for (int i = 0; i < nEl; i++) {
            if (mat[i][0].equalsIgnoreCase(valor)) {
                return i;
            }
        }
        return -1;
    }

    private static void listagemPaginada(String[][] matriz, int nEl) {
        int contPaginas = 0;
        for (int i = 0; i < nEl; i++) {
            if (i % MAX_LINHAS_PAGINA == 0) {
                if (contPaginas > 0) {
                    pausa();
                }
                contPaginas++;
                System.out.println("\nPÁGINA: " + contPaginas);
                cabecalho();
            }
            for (int j = 0; j < N_CAMPOS_INFO; j++) {
                if (j == 1) {
                    System.out.printf("%30s", matriz[i][j]);
                } else {
                    System.out.printf("%10s", matriz[i][j]);
                }
            }
            System.out.println("");
        }
        pausa();
    }

    private static void cabecalho() {
        System.out.printf("%50s%n", "PARTICIPANTES");
        System.out.printf("%75s%n", "==================================================");
    }

    private static void pausa() {
        System.out.println("\n\nPara continuar digite ENTER\n");
        in.nextLine();
    }

    /**
     * Atualiza informação alterável de um participante
     *
     * @param eMail – mail do participante
     * @param matriz - com toda a informação dos participantes
     * @param nElems - número de elementos
     * @return false se o eMail não foi encontrado ou true se foi encontrado e
     * atualizado
     */
    public static boolean actualizarInfoParticipante(String eMail, String[][] matriz, int nElems) {
        int pos;
        if ((pos = pesquisarElemento(eMail, matriz, nElems)) > -1) {
            int op;
            do {
                Formatter out = new Formatter(System.out);
                mostrarParticipante(out, matriz[pos]);
                op = menuInfoParticipante();
                switch (op) {
                    case 1:
                        System.out.println("Novo E-Mail:");
                        matriz[pos][0] = in.nextLine();
                        break;
                    case 2:
                        System.out.println("Novo nome:");
                        matriz[pos][1] = in.nextLine();
                        break;
                    case 3:
                        System.out.println("Nova data nascimento:");
                        matriz[pos][2] = in.nextLine();
                        break;
                    case 0:
                        System.out.println("FIM");
                        break;
                    default:
                        System.out.println("Opção incorreta");
                        break;
                }
            } while (op != 0);
            return true;
        }
        System.out.printf("O participante %s não foi encontrado!", eMail);
        return false;
    }

    private static void mostrarParticipante(Formatter out, String[] participante) {
        for (int j = 0; j < N_CAMPOS_INFO; j++) {
            if (j == 1) {
                out.format("%30s;", participante[j]);
            } else {
                out.format("%12s;", participante[j]);
            }
        }
    }

    private static int menuInfoParticipante() {
        String texto = "ATUALIZAR INFORMAÇÃO DE PARTICIPANTE"
                + "\n 1 - E-Mail"
                + "\n 2 - Nome"
                + "\n 3 - Data de nascimento"
                + "\n 0 - Terminar"
                + "\n\nQual a sua opção?";
        System.out.printf("%n%s%n", texto);
        int op = in.nextInt();
        in.nextLine();
        return op;
    }

    /**
     * Guardar resultados de um jogo num ficheiro para o array de pontos
     *
     * @param data - data do evento
     * @param jogoID - id do Jogo
     * @param pontos - array de pontos por participante por jogo
     * @param nParticipantes - número de participantes
     * @throws FileNotFoundException
     */
    private static void guardarResultadosJogo(String data, String jogoID, int[][] pontos, String[] jogos, String[][] participantes, int nParticipantes) throws FileNotFoundException {
        String[][] info = new String[MAX_PARTICIPANTES][2];
        Scanner fInput = new Scanner(new File(data + "_" + jogoID + ".txt"));
        int nID = 0, i, nJogadores = 0, pontosJogo = 0;
        String email;
        boolean erro = false;
        for (i = 0; i < N_JOGOS; i++) {
            if ((jogos[i].split("-"))[0].equals(jogoID)) {
                nID = i;
                pontosJogo = Integer.parseInt((jogos[i].trim().split("-"))[2]);
            }
        }

        while (fInput.hasNext() && nJogadores < nParticipantes) {
            String linha = fInput.nextLine();
            linha = linha.trim();
            // Verifica se linha não está em branco
            if (linha.length() > 0 && (pesquisarElemento((linha.split(";"))[0], participantes, nParticipantes) != -1)) {
                info[nJogadores][0] = linha.split(";")[0];
                info[nJogadores][1] = linha.split(";")[1];
                if (Integer.parseInt(info[nJogadores][1].trim()) > pontosJogo && Integer.parseInt(info[nJogadores][1].trim()) < 0) {
                    erro = true;
                }
                nJogadores++;
            }
        }
        fInput.close();

        if (!erro) {
            for (i = 0; i < nParticipantes; i++) {
                erro = true;
                for (int j = 0; j < nJogadores; j++) {
                    if (participantes[i][0].trim().equalsIgnoreCase(info[j][0].trim())) {
                        pontos[i][nID] = Integer.parseInt(info[j][1].trim());
                        erro = false;
                    }
                }
                if (erro) {
                    pontos[i][nID] = 0;
                }
            }
            System.out.println("Dados guardados.");
        } else {
            System.out.println("Erro ao processar ficheiro.");
        }
    }

    private static void guardarDadosPP(String[][] participantes, int[][] pontos, int nParticipantes) throws FileNotFoundException {
        Formatter fOut = new Formatter(new File("backup.txt"));
        String print;
        for (int i = 0; i < nParticipantes; i++) {
            print = "";
            for (int j = 0; j < N_CAMPOS_INFO; j++) {
                print += participantes[i][j] + SEPARADOR_DADOS_FICH;
            }
            for (int h = 0; h < N_JOGOS; h++) {
                print += pontos[i][h] + SEPARADOR_DADOS_FICH;
            }
            fOut.format(print + "\n");
        }
        fOut.close();
    }

    public static void calcularPrémios(int[][] pontos, String[] jogos, double[][] prémios, int nParticipantes, String[][] participantes, String data) {
        for (int jogo = 0; jogo < N_JOGOS; jogo++) {
            int maxP = maxPontos(pontos, nParticipantes, jogo);
            int maxPos = maxPossivel(jogos, jogo);
            int maxEq = maxEquipa(pontos, nParticipantes, jogo);
            if (maxP > 0 && maxEq > 0) {
                for (int i = 0; i < nParticipantes; i++) {
                    if (pontos[i][jogo] == maxP) {
                        prémios[i][jogo] += (double) maxP / maxPos * 100;
                    }
                    if (i % 3 == 0) {
                        if (calcPontosEquipa(pontos, jogo, i) == maxEq) {
                            prémios[i][jogo] += 50;
                            prémios[i + 1][jogo] += 50;
                            prémios[i + 2][jogo] += 50;
                        }
                    }
                }
            }
            premioAniversario(nParticipantes, prémios, participantes, jogo, data);
            maxP = 0;
            maxPos = 0;
            maxEq = 0;
        }
    }

    public static int maxPontos(int[][] pontos, int nParticipantes, int jogo) {
        int max = 0;
        for (int i = 0; i < nParticipantes; i++) {
            if (pontos[i][jogo] > max) {
                max = pontos[i][jogo];
            }
        }
        return max;
    }

    public static int maxPossivel(String[] jogos, int jogo) {
        String[] temp = jogos[jogo].split("-");
        temp[2] = temp[2].trim();
        int max = Integer.parseInt(temp[2]);
        return max;
    }

    public static int maxEquipa(int[][] pontos, int nParticipantes, int jogo) {
        int max = 0;
        for (int i = 0; i < nParticipantes; i = i + 3) {
            int eq = pontos[i][jogo] + pontos[i + 1][jogo] + pontos[i + 2][jogo];
            if (eq > max) {
                max = eq;
            }
        }
        return max;
    }

    public static int calcPontosEquipa(int[][] pontos, int col, int linha) {
        int pontosEq = 0;
        for (int i = linha; i < linha + 3; i++) {
            pontosEq = pontosEq + pontos[i][col];
        }
        return pontosEq;
    }

    public static void premioAniversario(int nParticipantes, double[][] premios, String[][] participantes, int col, String dataEvento) {
        for (int i = 0; i < nParticipantes; i++) {
            if (premios[i][col] > 0.001) {
                String data = participantes[i][2].trim();
                String[] temp = data.split("/");
                for (int j = 0; j < temp.length; j++) {
                    temp[j] = temp[j].trim();
                }
                data = temp[1] + temp[0];
                int anoP = Integer.parseInt(temp[2]);
                String help = dataEvento.substring(0, 4);
                int anoE = Integer.parseInt(help);
                if (data.equals(dataEvento.substring(4))) {
                    double acrescimo = (double) (anoE - anoP) * 2;
                    premios[i][col] += acrescimo;
                }
            }
        }
    }

    private static int removerEquipa(String equipa, int[][] pontos, double[][] prémios, String[][] participantes, int nParticipantes) {
        int cont = 0, i;
        while (cont < 3) {
            for (i = 0; i < nParticipantes; i++) {
                if (equipa.equalsIgnoreCase(participantes[i][3])) {
                    for (int j = i; j < nParticipantes - i - 1; j++) {
                        for (int h = 0; h < N_CAMPOS_INFO; h++) {
                            participantes[j][h] = participantes[j + 1][h];
                        }
                        for (int h = 0; h < N_JOGOS; h++) {
                            pontos[j][h] = pontos[j + 1][h];
                        }
                        for (int h = 0; h < N_JOGOS; h++) {
                            prémios[j][h] = prémios[j + 1][h];
                        }
                    }
                    i--;
                    nParticipantes--;
                    cont++;
                }
            }
            if (cont == 0) {
                System.out.println("Erro ao eliminar equipa");
                return nParticipantes + 3;
            }
        }
        System.out.println("Equipa eliminada com sucesso");
        return nParticipantes;
    }

    private static void listarEquipa(String equipa, String[][] participantes, double[][] prémios, int nParticipantes) {
        String imprimir = "Equipa " + equipa.trim() + "\n";
        int[] membros = new int[3];
        int totalpontos, j = 0, i, aux;
        for (i = 0; i < nParticipantes; i++) {
            if (participantes[i][3].trim().equals(equipa.trim())) {
                membros[j] = i;
                j++;
            }
        }
        if (j == 3) {
            if (participantes[membros[0]][2].charAt(0) < participantes[membros[1]][2].charAt(0)) {
                aux = membros[0];
                membros[0] = membros[1];
                membros[1] = aux;
            }
            if (participantes[membros[0]][2].charAt(0) < participantes[membros[2]][2].charAt(0)) {
                aux = membros[0];
                membros[0] = membros[2];
                membros[2] = aux;
            }
            if (participantes[membros[1]][2].charAt(0) < participantes[membros[2]][2].charAt(0)) {
                aux = membros[1];
                membros[1] = membros[2];
                membros[2] = aux;
            }
            for (j = 0; j < 3; j++) {
                totalpontos = 0;
                imprimir += participantes[membros[j]][1] + ": ";
                for (i = 0; i < N_JOGOS; i++) {
                    totalpontos += prémios[membros[j]][i];
                }
                imprimir += totalpontos + " pontos\n";
            }
        }
        System.out.println(imprimir);
    }

    public static void info(int nParticipantes, int[][] pontos, double[][] premios) {
        double media;
        int soma = 0, cont = 0, contn = 0;
        for (int i = 0; i < N_JOGOS; i++) {
            for (int j = 0; j < nParticipantes; j++) {
                if (pontos[j][i] > 0) {
                    cont++;
                    soma = soma + pontos[j][i];
                } else {
                    contn++;
                }
            }
            if (soma == 0) {
                System.out.println("Não há dados para o jogo " + (i + 1) + "\n\n");
                System.out.print("Enter para avançar");
                in.nextLine();
                System.out.println("\n---------------------------------------------\n");
                continue;
            }
            media = (double) soma / nParticipantes;
            double perc = contn / nParticipantes * 100;
            double totalp = 0;
            for (int j = 0; j < nParticipantes; j++) {
                if (premios[j][i] > 0) {
                    totalp = totalp + premios[j][i];
                }
            }
            System.out.println("Jogo " + (i + 1) + "\n");
            System.out.println("Pontuação média obtida pelos participantes = " + media);
            System.out.println("Percentagem de jogadores que não participou ou desistiu = " + perc);
            System.out.println("Valor total de prémios atribuídos = " + totalp);
            System.out.print("\nEnter para avançar\n");
            in.nextLine();
        }
    }

    public static void listagemPremios(int nParticipantes, String[][] participantes, double[][] premios, int caso) throws FileNotFoundException {
        String[][] matriz = new String[nParticipantes][4];
        double somaP;
        for (int i = 0; i < nParticipantes; i++) {
            matriz[i][0] = participantes[i][0].trim();
            matriz[i][1] = nomePessoa(participantes, i);
            matriz[i][2] = participantes[i][3].trim();
            somaP = somaPremios(premios, i);
            matriz[i][3] = String.valueOf(somaP);
        }
        ordenarMatriz(matriz, nParticipantes);
        if (caso == 1) {
            System.out.printf("%20s%20s%20s%20s%n", "Email", "Nome", "Equipa", "Total de Prémios");
            for (int i = 0; i < matriz.length; i++) {
                for (int j = 0; j < matriz[0].length; j++) {
                    System.out.printf("%20s", matriz[i][j]);
                }
                System.out.println();
            }
        }
        if (caso == 2) {
            Formatter fOut = new Formatter(new File("Premios.txt"));
            fOut.format("%20s%20s%20s%20s%n", "Email", "Nome", "Equipa", "Total de Prémios");
            for (int i = 0; i < matriz.length; i++) {
                fOut.format("%20s%20s%20s%20s%n", matriz[i][0], matriz[i][1], matriz[i][2], matriz[i][3]);
            }
            fOut.close();
            System.out.println("Enviado para o ficheiro");
        }
    }

    public static String nomePessoa(String[][] participantes, int i) {
        String[] temp = participantes[i][1].trim().split(" ");
        String nome = temp[temp.length - 1] + " " + temp[0].substring(0, 1) + ".";
        return nome;
    }

    public static double somaPremios(double[][] premios, int i) {
        double soma = 0;
        for (int j = 0; j < N_JOGOS; j++) {
            soma = soma + premios[i][j];
        }
        return soma;
    }

    public static void ordenarMatriz(String[][] matriz, int n) {
        for (int i = 0; i < n - 1; i++) {
            if (matriz[i][2].compareTo(matriz[i + 1][2]) < 0) {
                for (int j = 0; j < matriz[0].length; j++) {
                    String x = matriz[i][j];
                    matriz[i][j] = matriz[i + 1][j];
                    matriz[i + 1][j] = x;
                }
                i = -1;
            }
        }
    }
}
