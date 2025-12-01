package LadoClient;

import MaquinaRefri.MaquinaRefri;
import MaquinaRefri.Caixa;
import MaquinaRefri.PurchaseResult;
import util.Money;

import java.util.HashMap;
import java.util.Map;

public class LadoClient {
    private final MaquinaRefri maquina;

    public LadoClient(MaquinaRefri maquina) {
        this.maquina = maquina;
    }

    // Efetuar compra: cliente envia mapa de centavos->quantidade e escolhe índice do produto
    public String efetuarCompra(Map<Integer, Integer> insercao, int indiceProduto) {
        PurchaseResult resultado = maquina.comprar(insercao, indiceProduto);
        if (resultado.sucesso) {
            StringBuilder sb = new StringBuilder();
            sb.append("Compra bem sucedida. ").append(resultado.mensagem).append("\n");
            if (resultado.troco != null && !resultado.troco.isEmpty()) {
                sb.append("Troco:\n");
                int totalTroco = 0;
                for (Map.Entry<Integer, Integer> e : resultado.troco.entrySet()) {
                    sb.append(String.format("  %s x %d\n", Money.formatCents(e.getKey()), e.getValue()));
                    totalTroco += e.getKey() * e.getValue();
                }
                sb.append("Total troco: ").append(Money.formatCents(totalTroco)).append("\n");
            }
            return sb.toString();
        }
        return "Falha na compra: " + resultado.mensagem;
    }

    // Helper to construir insercao simples: aceita counts de denominações comuns
    public static Map<Integer, Integer> inserirNotas(int dez, int cinco, int dois, int um, int cinquenta) {
        Map<Integer, Integer> m = new HashMap<>();
        if (dez > 0) m.put(Caixa.TEN_REAL, dez);
        if (cinco > 0) m.put(Caixa.FIVE_REAL, cinco);
        if (dois > 0) m.put(Caixa.TWO_REAL, dois);
        if (um > 0) m.put(Caixa.ONE_REAL, um);
        if (cinquenta > 0) m.put(Caixa.FIFTY_CENT, cinquenta);
        return m;
    }
}
