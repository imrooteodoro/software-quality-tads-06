package MaquinaRefri;

import java.util.Map;
import java.util.TreeMap;

public class Caixa {
    // store counts of denominations (value in cents)
    private final TreeMap<Integer, Integer> moedas = new TreeMap<>();

    // supported denominations in cents: 100, 50, 200, 500, 1000
    public static final int ONE_REAL = 100;
    public static final int FIFTY_CENT = 50;
    public static final int TWO_REAL = 200;
    public static final int FIVE_REAL = 500;
    public static final int TEN_REAL = 1000;

    public Caixa() {
        // initialize counts to zero
        moedas.put(TEN_REAL, 0);
        moedas.put(FIVE_REAL, 0);
        moedas.put(TWO_REAL, 0);
        moedas.put(ONE_REAL, 0);
        moedas.put(FIFTY_CENT, 0);
    }

    public void adicionarNota(int valorCentavos, int quantidade) {
        moedas.put(valorCentavos, moedas.getOrDefault(valorCentavos, 0) + quantidade);
    }

    public Map<Integer, Integer> getSaldo() {
        return moedas.descendingMap();
    }

    public int getTotalCentavos() {
        int total = 0;
        for (Map.Entry<Integer, Integer> e : moedas.entrySet()) {
            total += e.getKey() * e.getValue();
        }
        return total;
    }

    // Try to make change for amount (in cents). Returns map denom->count or null if impossible.
    public Map<Integer, Integer> calcularTroco(int valorCentavos) {
        if (valorCentavos < 0) return null;
        java.util.NavigableMap<Integer, Integer> restante = moedas.descendingMap();
        Map<Integer, Integer> usado = new TreeMap<>();

        int faltante = valorCentavos;
        for (Integer denom : restante.keySet()) {
            int disponivel = moedas.getOrDefault(denom, 0);
            int usar = Math.min(disponivel, faltante / denom);
            if (usar > 0) {
                usado.put(denom, usar);
                faltante -= usar * denom;
            }
        }

        if (faltante == 0) return usado;
        return null; // não há troco suficiente
    }

    // remove coins according to map (assumes possible)
    public void retirarTroco(Map<Integer, Integer> troco) {
        for (Map.Entry<Integer, Integer> e : troco.entrySet()) {
            int denom = e.getKey();
            int qtd = e.getValue();
            moedas.put(denom, moedas.getOrDefault(denom, 0) - qtd);
        }
    }
}
