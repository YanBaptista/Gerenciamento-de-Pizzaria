package pizzaria.database;

import pizzaria.model.Cliente;
import pizzaria.model.Endereco;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * Classe DAO para gerenciar as operações CRUD da entidade Cliente.
 * Ela também gerencia a entidade Endereco, pois ela está
 * fortemente ligada ao Cliente (Relacionamento 1:1).
 */
public class ClienteDAO {

    /**
     * Salva um novo Cliente e seu Endereco no banco de dados
     * usando uma TRANSAÇÃO para garantir a integridade.
     */
    // Dentro de pizzaria/database/ClienteDAO.java

    public void salvar(Cliente cliente) {

        // Nossos dois comandos SQL (a ordem das strings não importa)
        String sqlCliente = "INSERT INTO Cliente (CPF, Nome, Telefone, Email) VALUES (?, ?, ?, ?)";
        String sqlEndereco = "INSERT INTO Endereco (Rua, Numero, Cep, Bairro, Cliente_CPF) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmtCliente = null; // Renomeado
        PreparedStatement pstmtEndereco = null; // Renomeado

        try {
            // 1. Obter a conexão
            conn = ConnectionFactory.getConnection();

            // 2. Desligar o Auto-Commit
            conn.setAutoCommit(false);

            // --- [MUDANÇA] PARTE 1: Salvar o Cliente (O PAI) PRIMEIRO ---
            pstmtCliente = conn.prepareStatement(sqlCliente);

            // Preenchemos os dados do Cliente
            pstmtCliente.setString(1, cliente.getCpf());
            pstmtCliente.setString(2, cliente.getNome());
            pstmtCliente.setString(3, cliente.getTelefone());
            pstmtCliente.setString(4, cliente.getEmail());

            pstmtCliente.executeUpdate(); // Executa o 1º INSERT

            // --- [MUDANÇA] PARTE 2: Salvar o Endereço (O FILHO) DEPOIS ---
            pstmtEndereco = conn.prepareStatement(sqlEndereco);

            // Pegamos o objeto Endereco de dentro do Cliente
            Endereco end = cliente.getEndereco();

            // Preenchemos os dados do Endereço
            pstmtEndereco.setString(1, end.getRua());
            pstmtEndereco.setString(2, end.getNumero());
            pstmtEndereco.setString(3, end.getCep());
            pstmtEndereco.setString(4, end.getBairro());
            // A ligação! Agora o CPF '123' JÁ EXISTE na tabela Cliente.
            pstmtEndereco.setString(5, cliente.getCpf());

            pstmtEndereco.executeUpdate(); // Executa o 2º INSERT

            // --- FIM DA TRANSAÇÃO ---
            // 3. Commit!
            conn.commit();

            System.out.println("Cliente e Endereço salvos com sucesso (Transação OK)!");

        } catch (SQLException e) {
            // 4. Rollback!
            System.err.println("Erro ao salvar o cliente. Iniciando Rollback...");
            try {
                if (conn != null) {
                    conn.rollback();
                    System.err.println("Rollback efetuado com sucesso.");
                }
            } catch (SQLException e2) {
                System.err.println("Erro crítico ao tentar executar o rollback: " + e2.getMessage());
            }
            e.printStackTrace();

        } finally {
            // 5. Fechando tudo
            try {
                if (pstmtCliente != null) pstmtCliente.close();
                if (pstmtEndereco != null) pstmtEndereco.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Aqui você adicionaria os outros métodos:
    // public Cliente buscarPorCpf(String cpf) { ... }
    // public void atualizar(Cliente cliente) { ... }
    // public void deletar(String cpf) { ... }
}