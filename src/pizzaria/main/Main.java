package pizzaria.main;

import pizzaria.model.*;
import pizzaria.service.GerenciadorDeArquivos;
import pizzaria.service.PreparoPedido; // Supondo que esta classe exista
import javax.swing.JOptionPane;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import pizzaria.database.ClienteDAO;


public class Main {
    public static void main(String[] args) {

        // --- PREPARAÇÃO DAS PIZZAS E CARDÁPIO ---
        Pizza p1 = new PizzaTradicional("Frango", 30, "");
        Pizza p2 = new PizzaEspecial("Marguerita", 35, "");
        Pizza p3 = new PizzaTradicional("Calabresa", 30, "");
        Pizza p4 = new PizzaEspecial("Da casa", 35,"");

        // Calcula os preços iniciais
        p1.calcularPreco();
        p2.calcularPreco(); // Aplica 15%
        p3.calcularPreco();
        p4.calcularPreco(); // Aplica 15%

        Cardapio.pizzasDisponiveis.add(p1);
        Cardapio.pizzasDisponiveis.add(p2);
        Cardapio.pizzasDisponiveis.add(p3);
        Cardapio.pizzasDisponiveis.add(p4);

        // --- PREPARAÇÃO DOS CLIENTES (MODO CORRETO) ---

// 1. Primeiro, crie os objetos Endereco
        Endereco end1 = new Endereco("Rua Inatel", "123", "37540-000", "Centro");
        Endereco end2 = new Endereco("Rua Vinte", "321", "37540-000", "Vila Nova");
        Endereco end3 = new Endereco("Avenida CBF", "10", "11060-909", "Vila Belmiro");
        Endereco end4 = new Endereco("Rua dos Bobos", "0", "31270-901", "Pampulha");

// 2. Agora, crie os Clientes usando os endereços
// Formato: Cliente(cpf, nome, telefone, email, endereco)
        Cliente c1 = new Cliente("Wagner", "125","11999990001", "wagner@email.com", end1);
        Cliente c2 = new Cliente("Yan", "789", "11999990002", "yan@email.com", end2);
        Cliente c3 = new Cliente("Neymar", "785", "11999990003", "neymar@email.com", end3);
        Cliente c4 = new Cliente("Gabigol", "525", "11999990004", "gabi@email.com", end4);

        // --- [NOVO BLOCO] SALVANDO CLIENTES NO BANCO DE DADOS ---
        System.out.println("\n--- INICIANDO REGISTRO DE CLIENTES NO BANCO ---");

// 1. Instancia o DAO
        ClienteDAO clienteDAO = new ClienteDAO();

// 2. Tenta salvar cada cliente
// O métod salvar() já imprime suas próprias mensagens
// de sucesso ou falha (com rollback)
        clienteDAO.salvar(c1);
        clienteDAO.salvar(c2);
        clienteDAO.salvar(c3);
        clienteDAO.salvar(c4);

        System.out.println("--- FIM DO REGISTRO DE CLIENTES ---\n");

        // --- CRIAÇÃO DOS PEDIDOS E ADIÇÃO A UMA LISTA ---
        List<Pedido> todosOsPedidos = new ArrayList<>();

        // Pedido 1
        Pedido meuPedido = new Pedido(1, LocalDate.now(), c1);
        meuPedido.addPizza("Frango");
        meuPedido.addPizza("Marguerita");
        todosOsPedidos.add(meuPedido);

        // Pedido 2
        Pedido meuPedido2 = new Pedido(2, LocalDate.now(), c2);
        meuPedido2.addPizza("Calabresa");
        meuPedido2.addPizza("Da casa");
        todosOsPedidos.add(meuPedido2);

        // Pedido 3
        Pedido meuPedido3 = new Pedido(3, LocalDate.now(), c3);
        meuPedido3.addPizza("Frango");
        meuPedido3.addPizza("Da casa");
        meuPedido3.addPizza("Calabresa");
        todosOsPedidos.add(meuPedido3);

        // Pedido 4 (Vazio para teste)
        Pedido meuPedido4 = new Pedido(4, LocalDate.now(), c4);
        todosOsPedidos.add(meuPedido4);


        // --- PROCESSAMENTO INDIVIDUAL DE CADA PEDIDO NO LOOP ---
        System.out.println("\n--- INICIANDO PROCESSAMENTO DE TODOS OS PEDIDOS ---");

        for (Pedido pedidoAtual : todosOsPedidos) {
            System.out.println("\n----- Verificando Pedido Nº " + pedidoAtual.getNumero() + " -----");

            try {
                // 1. Lógica de negócio aplicada ANTES de finalizar
                pedidoAtual.aplicaPromocao();
                pedidoAtual.calculototal();
                pedidoAtual.mostradetalhes();

                // 2. Tenta finalizar o pedido. Se for vazio, lança exceção.
                pedidoAtual.finalizar();

                // 3. Se o pedido for VÁLIDO, ele será salvo no BANCO DE DADOS
                // (Certifique-se de importar pizzaria.database.PedidoDAO lá em cima)
                pizzaria.database.PedidoDAO pedidoDAO = new pizzaria.database.PedidoDAO();
                pedidoDAO.salvar(pedidoAtual);

                // 4. Inicia o preparo em uma thread (supondo que PreparoPedido exista)
                PreparoPedido preparo = new PreparoPedido(pedidoAtual);
                Thread threadCozinha = new Thread(preparo);
                threadCozinha.start();
                System.out.println(">>> Pedido Nº " + pedidoAtual.getNumero() + " válido e enviado para preparo.");

            } catch (PedidoVazioException e) {
                // Se a exceção ocorrer, mostra o pop-up de erro
                JOptionPane.showMessageDialog(null, "Erro no Pedido Nº " + pedidoAtual.getNumero() + ": " + e.getMessage(), "Pedido Inválido", JOptionPane.WARNING_MESSAGE);
            }
        }

        System.out.println("\n-------------------------------------------------");
        System.out.println("Processamento de todos os pedidos finalizado.");
        System.out.println("Total de pedidos criados hoje: " + Pedido.getTotalPedidos());
    }
}