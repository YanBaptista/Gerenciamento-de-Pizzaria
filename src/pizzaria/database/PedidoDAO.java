package pizzaria.database;

import pizzaria.model.Pedido;
import pizzaria.model.Pizza;

import java.sql.Connection;
import java.sql.Date; // Importante para converter datas
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PedidoDAO {

    public void salvar(Pedido pedido) {
        // SQL para a tabela principal
        String sqlPedido = "INSERT INTO Pedido (Numero, Data, ValorTotal, Cliente_CPF) VALUES (?, ?, ?, ?)";

        // SQL para a tabela de ligação (N:M)
        String sqlPedidoPizza = "INSERT INTO Pedido_has_Pizza (Pedido_Numero, Pizza_Nome) VALUES (?, ?)";

        Connection conn = null;
        PreparedStatement pstmtPedido = null;
        PreparedStatement pstmtJoin = null;

        try {
            conn = ConnectionFactory.getConnection();

            // 1. Iniciar Transação (Desligar Auto-Commit)
            // Isso garante que ou salva TUDO (pedido + pizzas) ou não salva NADA.
            conn.setAutoCommit(false);

            // --- ETAPA 1: Salvar o Pedido (Pai) ---
            pstmtPedido = conn.prepareStatement(sqlPedido);

            pstmtPedido.setInt(1, pedido.getNumero());
            // Convertendo LocalDate (Java) para Date (SQL)
            pstmtPedido.setDate(2, Date.valueOf(pedido.getData()));
            pstmtPedido.setDouble(3, pedido.getValorTotalPedido());
            // Pegamos o CPF de dentro do objeto Cliente que está dentro do Pedido
            pstmtPedido.setString(4, pedido.getCliente().getCpf());

            pstmtPedido.executeUpdate();

            // --- ETAPA 2: Salvar as Pizzas (Filhos/Ligação) ---
            pstmtJoin = conn.prepareStatement(sqlPedidoPizza);

            // Percorremos a lista de pizzas do pedido
            for (Pizza pizza : pedido.getPizzas()) {
                pstmtJoin.setInt(1, pedido.getNumero()); // FK do Pedido
                pstmtJoin.setString(2, pizza.getNome()); // FK da Pizza

                pstmtJoin.executeUpdate(); // Salva essa ligação
            }

            // --- ETAPA 3: Confirmar Transação ---
            conn.commit();
            System.out.println("Pedido Nº " + pedido.getNumero() + " salvo com sucesso no banco!");

        } catch (SQLException e) {
            // Em caso de erro, desfazemos tudo
            try {
                if (conn != null) {
                    conn.rollback();
                    System.err.println("Erro ao salvar pedido! Rollback realizado.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            // Fechar conexões
            try {
                if (pstmtPedido != null) pstmtPedido.close();
                if (pstmtJoin != null) pstmtJoin.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
