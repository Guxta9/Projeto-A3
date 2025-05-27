import java.util.ArrayList;

public class GastoController {
    private ArrayList<Gasto> gastos;
    private double limite;

    public GastoController(double limite) {
        this.limite = limite;
        this.gastos = new ArrayList<>();
    }

    public void adicionarGasto(Gasto gasto) {
        gastos.add(gasto);
    }

    public void removerGasto(int index) {
        if (index >= 0 && index < gastos.size()) {
            gastos.remove(index);
        }
    }

    public void editarGasto(int index, String novaDescricao, double novoValor) {
        if (index >= 0 && index < gastos.size()) {
            Gasto gasto = gastos.get(index);
            gasto.setDescricao(novaDescricao);
            gasto.setValor(novoValor);
        }
    }

    public ArrayList<Gasto> getGastos() {
        return gastos;
    }

    public double calcularTotal() {
        return gastos.stream().mapToDouble(Gasto::getValor).sum();
    }

    public boolean isLimiteAtingido() {
        return calcularTotal() >= limite;
    }

    public double getLimite() {
        return limite;
    }
}
