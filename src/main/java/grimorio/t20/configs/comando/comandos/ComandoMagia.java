package grimorio.t20.configs.comando.comandos;

import grimorio.t20.configs.comando.ComandoContext;
import grimorio.t20.configs.comando.IComando;
import grimorio.t20.database.IDatabaseGerenciar;
import grimorio.t20.struct.Magia;
import grimorio.t20.struct.Padroes;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;
import java.util.Map;

public class ComandoMagia implements IComando {

    public static final String NOME = "magia";

    @Override
    public void gerenciar(ComandoContext context) {
        List<String> args = context.getArgs();
        TextChannel canal = context.getChannel();

        String consultaMagia = String.join(" ", args);

        Map<Integer, Magia> mapMagias = IDatabaseGerenciar.INSTANCE.consultaMagia(consultaMagia);

        if (mapMagias.size() <= 0) {
            canal.sendMessageEmbeds(
                    Padroes.getMensagemErro(
                            "Magia n�o encontrada",
                            String.format("_Meu acervo n�o disp�e de nenhum feiti�o similar a `%s`.\n" +
                                    "Tem certeza que � isso que buscavas?_\n\n" +
                                    "(n�o foi poss�vel encontrar uma magia que contenha `%s` em seu nome)"
                            , consultaMagia, consultaMagia)
                    ).build()
            ).queue();
            return;
        }

        if (mapMagias.size() == 1) {
            Magia magia = (Magia) mapMagias.values().toArray()[0];
            canal.sendMessageEmbeds(
                    Padroes.getMensagemMagia(magia).build()
            ).queue();
            return;
        }

        String s = "";
    }

    @Override
    public String getNome() {
        return NOME;
    }

    @Override
    public String getAjuda() {
        return "_Vamos, mortal. Diga-me qual feiti�o procuras e eu lhe enaltecerei com conheicmento._\n\n" +
                "(consulta uma magia)\n" +
                "Uso: `%s"+NOME+" <parte_do_nome_da_magia>`\n" +
                "_Dica_: n�o � preciso colocar aspas para nomes que contenham espa�os.";
    }

    @Override
    public List<String> getAliases() {
        return List.of("m", "ma", "feitico", "feiti�o");
    }
}
