package pizzaria.model;

/**
 * Esta classe é o "Modelo" que representa a tabela 'Endereco' do banco de dados.
 * É um POJO (Plain Old Java Object).
 */
public class Endereco {

    // --- Atributos (Espelhando as colunas do banco) ---

    private int idEndereco; // Chave Primária (será útil para ler/atualizar)
    private String rua;
    private String numero;  // VARCHAR é bom (ex: "10A", "S/N")
    private String cep;
    private String bairro;

    // --- Construtores ---

    /**
     * Construtor vazio.
     * Boa prática para quando formos ler dados do banco.
     */
    public Endereco() {
    }

    /**
     * Construtor para criar um novo endereço (sem o ID,
     * pois o banco de dados geralmente o gera).
     */
    public Endereco(String rua, String numero, String cep, String bairro) {
        this.rua = rua;
        this.numero = numero;
        this.cep = cep;
        this.bairro = bairro;
    }

    // --- Getters e Setters ---
    // (Necessários para o DAO e outras partes do Java lerem e definirem os valores)

    public int getIdEndereco() {
        return idEndereco;
    }

    public void setIdEndereco(int idEndereco) {
        this.idEndereco = idEndereco;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }
}