import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class GastoApp extends JFrame {
    private JTextField descricaoField;
    private JTextField valorField;
    private JLabel totalLabel;
    private JTable tabela;
    private DefaultTableModel modeloTabela;

    private GastoController controller;

    public GastoApp() {
        double limite = solicitarLimite();

        controller = new GastoController(limite);

        setTitle("Controle de Gastos");
        setSize(500, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel painel = new JPanel(new BorderLayout(5, 5));

        JPanel entrada = new JPanel(new GridLayout(2, 2, 5, 5));
        entrada.add(new JLabel("Descrição:"));
        descricaoField = new JTextField();
        entrada.add(descricaoField);

        entrada.add(new JLabel("Valor (R$):"));
        valorField = new JTextField();
        entrada.add(valorField);

        painel.add(entrada, BorderLayout.NORTH);

        JPanel botoesPanel = new JPanel();
        JButton adicionarBtn = new JButton("Adicionar");
        JButton removerBtn = new JButton("Remover");
        JButton editarBtn = new JButton("Editar");
        botoesPanel.add(adicionarBtn);
        botoesPanel.add(removerBtn);
        botoesPanel.add(editarBtn);
        painel.add(botoesPanel, BorderLayout.SOUTH);

        modeloTabela = new DefaultTableModel(new String[]{"Descrição", "Valor (R$)"}, 0);
        tabela = new JTable(modeloTabela);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);

        totalLabel = new JLabel("Total: R$ 0.00");
        totalLabel.setHorizontalAlignment(SwingConstants.CENTER);
        painel.add(totalLabel, BorderLayout.EAST);

        adicionarBtn.addActionListener(e -> adicionarGasto());
        removerBtn.addActionListener(e -> removerGasto());
        editarBtn.addActionListener(e -> editarGasto());

        add(painel);
        setVisible(true);
    }

    private double solicitarLimite() {
        while (true) {
            String input = JOptionPane.showInputDialog(null, "Digite o limite de gastos (R$):", "Limite", JOptionPane.PLAIN_MESSAGE);
            if (input == null) {
                System.exit(0);
            }

            try {
                double limite = Double.parseDouble(input);
                if (limite > 0) {
                    return limite;
                } else {
                    JOptionPane.showMessageDialog(null, "Digite um valor maior que zero.");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Valor inválido. Tente novamente.");
            }
        }
    }

    private void adicionarGasto() {
        String descricao = descricaoField.getText().trim();
        String valorTexto = valorField.getText().trim();

        if (descricao.isEmpty() || valorTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos.");
            return;
        }

        try {
            double valor = Double.parseDouble(valorTexto);
            Gasto novo = new Gasto(descricao, valor);
            controller.adicionarGasto(novo);

            modeloTabela.addRow(new Object[]{novo.getDescricao(), String.format("R$ %.2f", novo.getValor())});
            atualizarTotal();

            if (controller.isLimiteAtingido()) {
                JOptionPane.showMessageDialog(this, "Atenção: você atingiu ou ultrapassou o limite de gastos!", "Aviso", JOptionPane.WARNING_MESSAGE);
            }

            descricaoField.setText("");
            valorField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Digite um valor numérico válido.");
        }
    }

    private void removerGasto() {
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this, "Deseja remover este gasto?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                controller.removerGasto(linhaSelecionada);
                modeloTabela.removeRow(linhaSelecionada);
                atualizarTotal();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um item para remover.");
        }
    }

    private void editarGasto() {
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada >= 0) {
            Gasto gasto = controller.getGastos().get(linhaSelecionada);

            String novaDescricao = JOptionPane.showInputDialog(this, "Nova descrição:", gasto.getDescricao());
            String novoValorStr = JOptionPane.showInputDialog(this, "Novo valor (R$):", gasto.getValor());

            if (novaDescricao != null && novoValorStr != null) {
                try {
                    double novoValor = Double.parseDouble(novoValorStr);
                    controller.editarGasto(linhaSelecionada, novaDescricao, novoValor);

                    modeloTabela.setValueAt(novaDescricao, linhaSelecionada, 0);
                    modeloTabela.setValueAt(String.format("R$ %.2f", novoValor), linhaSelecionada, 1);
                    atualizarTotal();

                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Valor inválido.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um item para editar.");
        }
    }

    private void atualizarTotal() {
        double total = controller.calcularTotal();
        totalLabel.setText(String.format("Total: R$ %.2f", total));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GastoApp::new);
    }
}
