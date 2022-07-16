package grimorio.t20.configs.comando.comandos;

import grimorio.t20.configs.comando.IComando;
import grimorio.t20.configs.comando.IComandoContext;
import grimorio.t20.database.IDatabaseGerenciar;
import grimorio.t20.struct.Errata;
import grimorio.t20.struct.Padroes;
import grimorio.t20.struct.Update;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;
import java.util.Map;

public class ComandoUpdate implements IComando {

    public static final String NOME = "Update";

    @Override
    public void gerenciar(IComandoContext context) {
        List<String> args = context.getArgs();
        TextChannel canal = context.getChannel();
        Member member = context.getMember();

        String id = "";

        if (args.size() > 0) {
            id = args.get(0);
        }

        Map<Integer, Update>  mapUpdates = IDatabaseGerenciar.INSTANCE.consultaUpdate(id);

        if (mapUpdates.size() <= 0) {
            canal.sendMessageEmbeds(
                    Padroes.getMensagemErro(
                            "Update n�o encontrado",
                            String.format("_N�o existem atualiza��es com o �ndice `%s`.\n" +
                                            "O c�dice est� funcional._\n\n" +
                                            "(n�o foi poss�vel encontrar um update com o id `%s`)"
                                    , id, id)
                    ).build()
            ).queue();
            return;
        }

        Update update = new Update();
        for (Map.Entry<Integer, Update> e : mapUpdates.entrySet()) {
            update = e.getValue();
        }

        canal.sendMessageEmbeds(
                Padroes.getMensagemAjuda(
                        "Update " + update.getId(),
                        String.format("%s", update.getDescricao()))
                .setFooter(update.getData() + " [" + update.getId() + "]")
                .build()
            ).queue();

    }

    @Override
    public String getNome() { return NOME; }

    @Override
    public String getAjuda(boolean mostrarAliases) {
        return "_Exibe a lista de atualiza��es as quais eu fui submetido pelo arquimago das pedras._\n\n" +
                "(exibe uma lista com as atualiza��es do bot)\n" +
                "Uso: `%s"+NOME.toLowerCase()+" [id]`\n" +
                (mostrarAliases && getAliasesToString().length() > 0 ? "Tente tamb�m: " + getAliasesToString() : "");
    }

    @Override
    public String getResumoComando() {
        return "\n`%s" + NOME.toLowerCase() + " [id]`\nExibe o detalhamento do update informado ou do �ltimo update, caso nenhum " +
                "id seja informado.\n";
    }

    @Override
    public boolean isAdministrativo() { return false; }

    @Override
    public boolean isRestritoDesenvolvedor() { return false; }
}
