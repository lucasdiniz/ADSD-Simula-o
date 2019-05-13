import java.io.PrintWriter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author lucasdiniz
 * Escalona eventos de chegada/saída de acordo com
 * a especificação do miniteste.
 */
public class Escalonador extends Thread {
    private int tempo;
    private int chegada1, chegada2;
    private int saida;
    private int filaChegada1, filaChegada2;
    private boolean servico1, servico2;
    private PrintWriter writer;

    /**
     * Construtor
     * @param tempo de execucao
     */
    public Escalonador(int tempo) {
        this.tempo = tempo;
        this.chegada1 = 0;
        this.chegada2 = 0;
        this.saida = 0;
        this.filaChegada1 = 0;
        this.filaChegada2 = 0;
        this.servico1 = false;
        this.servico2 = false;
        this.escalonaEventoChegada(0, 1);
        this.escalonaEventoChegada(0, 2);

        try {
            writer = new PrintWriter("../saida/saida", "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Inicia a thread
     */
    @Override
    public void run() {
        int segundos = 0;
        while (segundos < this.tempo) {
            try {
                this.sleep(1);
                segundos++;
                checaEventosCriados(segundos);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        writer.close();
    }

    /**
     * Checa a cada segundo os eventos que devem ser tratados, caso haja algum.
     * @param segundos Tempo atual do experimento
     */
    public void checaEventosCriados(int segundos) {
        // Chegada TIPO 1 (Tem prioridade)
        if (segundos == chegada1) {
            if (!this.servico1 && !this.servico2) {
                escalonaEventoSaida(segundos);
                this.servico1 = true;
            } else {
                this.filaChegada1++;
            }

            handlePrints("Tipo de evento: CHEGADA de fregues TIPO 1 aos " + segundos + "'");
            escalonaEventoChegada(segundos, 1);
        }
        // Chegada TIPO 2
        if (segundos == chegada2) {
            if (!this.servico1 && !this.servico2) {
                escalonaEventoSaida(segundos);
                this.servico2 = true;
            } else {
                this.filaChegada2++;
            }
            handlePrints("Tipo de evento: CHEGADA de fregues TIPO 2 aos " + segundos + "'");
            escalonaEventoChegada(segundos, 2);
        }
        // Saída
        if (segundos == this.saida) {
            if (this.filaChegada1 != 0) {
                this.filaChegada1--;
                this.servico1 = true;
                this.servico2 = false;
                handlePrints("Tipo de evento: SAIDA aos " + segundos + "'");
                this.escalonaEventoSaida(segundos);
            } else if (this.filaChegada2 != 0) {
                this.filaChegada2--;
                this.servico2 = true;
                this.servico1 = false;
                handlePrints("Tipo de evento: SAIDA aos " + segundos + "'");
                this.escalonaEventoSaida(segundos);
            } else {
                this.servico1 = false;
                this.servico2 = false;
            }
        }
    }

    /**
     * Escreve no arquivo de saída o log do experimento no segundo corrente
     * @param stringToPrint string que será escrita no arquivo de saída
     */
    public void handlePrints(String stringToPrint) {
        this.writer.println(stringToPrint);
        this.writer.println("Fregueses tipo 1 na fila: " + this.filaChegada1);
        this.writer.println("Fregueses tipo 2 na fila: " + this.filaChegada2);
        if(this.servico1){
            this.writer.println("Fregues no servico: TIPO 1");
        } else if(this.servico2) {
            this.writer.println("Fregues no servico: TIPO 2");
        } else {
            this.writer.println("Fregues no servico: NENHUM");
        }
        this.writer.println();
    }

    /**
     * Gera valores aleatórios uniformemente distribuídos entre @param(from) e @param(to)
     * @param from Limite inferior
     * @param to Limite superior
     * @return Valor aleatório entre @param(from) e @param(to)
     */
    public int geraAleatorio(int from, int to) {
       int random = ThreadLocalRandom.current().nextInt(from, to + 1);
       // nextInt[from, to) da classe ThreadLocalRandom do Java gera valores uniformemente distribuídos
       return random;
    }

    /**
     * Escalona o próximo evento de chegada.
     * @param segundos Quantidade de tempo no futuro em que acontecerá
     *                 o próximo evento de chegada.
     * @param tipo Tipo de chegada {1, 2}
     */
    public void escalonaEventoChegada(int segundos, int tipo) {
        if(tipo == 1) {
            this.chegada1 = segundos + geraAleatorio(1, 12);
        }
        if(tipo == 2) {
            this.chegada2 = segundos + geraAleatorio(1, 4);
        }
    }

    /**
     * Escalona o próximo evento de saída.
     * @param segundos Quantidade de tempo no futuro em que acontecerá
     *                 o próximo evento de saída.
     */
    public void escalonaEventoSaida(int segundos) {
        this.saida = segundos + geraAleatorio(2, 6);
    }

    // Main
    public static void main(String[] args) {
        int tempoDeEscalonamento = 300; //segundos
        Escalonador escalonador = new Escalonador(tempoDeEscalonamento);
        escalonador.start();
    }
}