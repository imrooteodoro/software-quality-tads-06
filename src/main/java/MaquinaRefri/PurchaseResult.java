package MaquinaRefri;

import java.util.Map;

public class PurchaseResult {
    public final boolean sucesso;
    public final String mensagem;
    public final Map<Integer, Integer> troco; // denom->count
    public final int recebidoCentavos;

    public PurchaseResult(boolean sucesso, String mensagem, Map<Integer, Integer> troco, int recebidoCentavos) {
        this.sucesso = sucesso;
        this.mensagem = mensagem;
        this.troco = troco;
        this.recebidoCentavos = recebidoCentavos;
    }
}
