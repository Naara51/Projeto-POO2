import java.util.ArrayList;
import java.util.List;

public class Atividade {
    // O que a classe SABE (Atributos privados para garantir encapsulamento)
    private String titulo;
    private String tipo;
    private String periodo;
    private int capacidadeMaxima;
    private String regrasPresenca;
    
    // Lista para contabilizar as inscrições recebidas
    private List<Inscricao> inscricoes;

    // Construtor com as Invariantes (Validações e "ifs" de segurança)
    public Atividade(String titulo, String tipo, String periodo, int capacidadeMaxima, String regrasPresenca) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("O título da atividade não pode ser vazio.");
        }
        if (capacidadeMaxima <= 0) {
            throw new IllegalArgumentException("A capacidade máxima deve ser maior que zero.");
        }
        
        this.titulo = titulo;
        this.tipo = tipo;
        this.periodo = periodo;
        this.capacidadeMaxima = capacidadeMaxima;
        this.regrasPresenca = regrasPresenca;
        this.inscricoes = new ArrayList<>();
    }

    // O que a classe FAZ (Comportamento)
    
    // Calcula por conta própria se ainda existem vagas disponíveis
    public int calcularVagasDisponiveis() {
        return this.capacidadeMaxima - this.inscricoes.size();
    }
    
    public boolean aceitarInscricao(Inscricao novaInscricao) {
        if (calcularVagasDisponiveis() > 0) {
            this.inscricoes.add(novaInscricao);
            return true;
        }
        return false; // Bloqueia a inscrição se não houver vagas
    }

    // Métodos pendentes conforme o Diagrama de Classes
    public String calcularSituacaoPresenca() {
        // Lógica futura baseada na regra configurada
        return "Pendente";
    }

    public String gerarQRCode() {
        // Lógica futura para gerar código legível
        return "QR-CODE-GERADO";
    }

    // Getters apenas para leitura de dados necessários (Evitar Setters indiscriminados)
    public String getTitulo() {
        return titulo;
    }
}
