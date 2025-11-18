package main.java.banco.controller;

import main.java.banco.model.ClienteModel;
import main.java.banco.model.ContaCorrenteModel;
import main.java.banco.model.ContaModel;

import javax.swing.*;
import java.awt.*;

public class TelaManipularContaControler extends JFrame {
    private GerenciadorBancoControler banco;
    private JTextField txtCpf, txtValor;
    private JLabel lblInfoConta;
    private ContaModel contaAtual;

    public TelaManipularContaControler(GerenciadorBancoControler banco) {
        this.banco = banco;

        setTitle("Manipular Conta");
        setSize(500, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel painelBusca = new JPanel(new FlowLayout());
        painelBusca.add(new JLabel("CPF do Cliente:"));
        txtCpf = new JTextField(15);
        painelBusca.add(txtCpf);
        JButton btnBuscar = new JButton("Buscar Conta");
        btnBuscar.addActionListener(e -> buscarConta());
        painelBusca.add(btnBuscar);

        JPanel painelInfo = new JPanel(new BorderLayout());
        painelInfo.setBorder(BorderFactory.createTitledBorder("Informações da Conta"));
        lblInfoConta = new JLabel("Nenhuma conta selecionada", SwingConstants.CENTER);
        lblInfoConta.setFont(new Font("Arial", Font.PLAIN, 14));
        painelInfo.add(lblInfoConta, BorderLayout.CENTER);

        JPanel painelOperacoes = new JPanel(new GridLayout(5, 1, 10, 10));
        painelOperacoes.setBorder(BorderFactory.createTitledBorder("Operações"));

        JPanel painelValor = new JPanel(new FlowLayout());
        painelValor.add(new JLabel("Valor (R$):"));
        txtValor = new JTextField(10);
        painelValor.add(txtValor);
        painelOperacoes.add(painelValor);

        JButton btnDeposito = new JButton("Depositar");
        btnDeposito.addActionListener(e -> realizarDeposito());
        painelOperacoes.add(btnDeposito);

        JButton btnSaque = new JButton("Sacar");
        btnSaque.addActionListener(e -> realizarSaque());
        painelOperacoes.add(btnSaque);

        JButton btnVerSaldo = new JButton("Ver Saldo");
        btnVerSaldo.addActionListener(e -> verSaldo());
        painelOperacoes.add(btnVerSaldo);

        JButton btnRemunerar = new JButton("Remunerar");
        btnRemunerar.addActionListener(e -> remunerar());
        painelOperacoes.add(btnRemunerar);

        painelPrincipal.add(painelBusca, BorderLayout.NORTH);
        painelPrincipal.add(painelInfo, BorderLayout.CENTER);
        painelPrincipal.add(painelOperacoes, BorderLayout.SOUTH);

        setContentPane(painelPrincipal);
    }

    private void buscarConta() {
        String cpf = txtCpf.getText().trim();
        if (cpf.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite um CPF!");
            return;
        }

        contaAtual = banco.buscarConta(cpf);

        if (contaAtual == null) {
            JOptionPane.showMessageDialog(this, "Nenhuma conta encontrada para este CPF!");
            lblInfoConta.setText("Nenhuma conta selecionada");
        } else {
            atualizarInfoConta();
        }
    }

    private void atualizarInfoConta() {
        if (contaAtual != null) {
            String info = "<html><center>" +
                    "Cliente: " + contaAtual.getDono().getNome() + " " +
                    contaAtual.getDono().getSobrenome() + "<br>" +
                    contaAtual.toString() + "</center></html>";
            lblInfoConta.setText(info);
        }
    }

    private void realizarDeposito() {
        if (contaAtual == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma conta primeiro!");
            return;
        }

        try {
            double valor = Double.parseDouble(txtValor.getText());

            if (contaAtual.deposita(valor)) {
                JOptionPane.showMessageDialog(this, "Depósito realizado com sucesso!");
                atualizarInfoConta();
                txtValor.setText("");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Erro: Verifique as regras de depósito desta conta.\n" +
                                "Para Conta Investimento, o valor deve ser maior ou igual ao depósito mínimo.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Digite um valor válido!");
        }
    }

    private void realizarSaque() {
        if (contaAtual == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma conta primeiro!");
            return;
        }

        try {
            double valor = Double.parseDouble(txtValor.getText());

           contaAtual.getSaldo();

            if (!contaAtual.saca(valor)) {
                String mensagem = "Erro: Saque não permitido.\n";
                if (contaAtual instanceof ContaCorrenteModel) {
                    mensagem += "Para Conta Corrente, o saldo não pode ultrapassar o limite.";
                } else {
                    mensagem += "Para Conta Investimento, o saldo deve permanecer acima do montante mínimo.";
                }
                JOptionPane.showMessageDialog(this, mensagem);
                return;
            }
            JOptionPane.showMessageDialog(this, "Saque realizado com sucesso!");
            atualizarInfoConta();
            txtValor.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Digite um valor válido!");
        }
    }

    private void verSaldo() {
        if (contaAtual == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma conta primeiro!");
            return;
        }

        JOptionPane.showMessageDialog(this,
                "Saldo atual: R$ " + String.format("%.2f", contaAtual.getSaldo()));
    }

    private void remunerar() {
        if (contaAtual == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma conta primeiro!");
            return;
        }

        double saldoAnterior = contaAtual.getSaldo();
        contaAtual.remunera();
        double saldoNovo = contaAtual.getSaldo();
        double rendimento = saldoNovo - saldoAnterior;

        JOptionPane.showMessageDialog(this,
                "Remuneração aplicada com sucesso!\n" +
                        "Rendimento: R$ " + String.format("%.2f", rendimento) + "\n" +
                        "Novo saldo: R$ " + String.format("%.2f", saldoNovo));

        atualizarInfoConta();
    }
}
