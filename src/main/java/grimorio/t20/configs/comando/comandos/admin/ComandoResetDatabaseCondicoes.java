package grimorio.t20.configs.comando.comandos.admin;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import grimorio.t20.configs.Config;
import grimorio.t20.configs.comando.ComandoContext;
import grimorio.t20.configs.comando.IComando;
import grimorio.t20.database.IDatabaseGerenciar;
import grimorio.t20.struct.Aprimoramento;
import grimorio.t20.struct.Condicao;
import grimorio.t20.struct.Magia;
import grimorio.t20.struct.Padroes;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ComandoResetDatabaseCondicoes implements IComando {

    public static final String NOME = "resetDatabaseCondicoes";

    @Override
    public void gerenciar(ComandoContext context) {
        final TextChannel canal = context.getChannel();
        final List<String> args = context.getArgs();
        final Member member = context.getMember();

        if (member.getIdLong() != Long.parseLong(Config.get("owner_id"))) {
            canal.sendMessageEmbeds(
                    Padroes.getMensagemErro(
                        "Quem voc� pensa que �?",
                        "_Voc� acha que pode me enganar?\n" +
                                "Voc� n�o � o meu mestre, mortal._\n\n" +
                                "(voc� n�o tem permiss�o para executar esse comando)"
                ).build()
            ).queue();
            return;
        }
        canal.sendMessageEmbeds(
                Padroes.getMensagemSucesso(
                        "Seja bem-vindo",
                        "_Que bom ver que voc� retornou, mestre.\n" +
                                "Todos esses mortais me cansam com seus problemas e efermidades.\n" +
                                "Por favor, diga-me que voc� trouxe conhecimento novo._\n\n" +
                                "(o banco de dados de condi��es est� sendo atualizado. Isso pode levar algum tempo, aguardem)"
                ).build()
        ).queue();

        ArrayList<Condicao> listaCondicoes = null;

        try {
            BufferedReader json = new BufferedReader(new InputStreamReader(new FileInputStream("raw/condicoes.json")));
            listaCondicoes = new Gson().fromJson(json, new TypeToken<List<Condicao>>(){}.getType());
            json.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (listaCondicoes == null) {
            canal.sendMessageEmbeds(
                    Padroes.getMensagemErro(
                            "N�o entendi",
                            "_Perdoe-me grande Arquimago das Pedras, mas eu n�o consegui compreender " +
                                    "o conheicmento que trouxeste at� mim._\n\n" +
                                    "(o banco de dados de condi��es **n�o** foi atualizado. O arquivo estava incorreto)"
                    ).build()
            ).queue();
            return;
        }

        IDatabaseGerenciar.INSTANCE.addListaCondicao(listaCondicoes);

        canal.sendMessageEmbeds(
                Padroes.getMensagemSucesso(
                        "At� breve",
                        "_Obigado por me enaltecer com este acervo de efermidades, Arquimago das Pedras.\n" +
                                "Retorne quando houver mais conhecimento para compartilhar comigo, mestre._\n\n" +
                                "(o banco de dados de condi��es foi atualizado com sucesso)"
                ).build()
        ).queue();
    }

    @Override
    public String getNome() {
        return NOME;
    }

    @Override
    public String getAjuda() {
        return "_Este poderoso ritual s� pode ser invocado pelo meu mestre, o grande Arquimago das Pedras.\n" +
                "Mortais podem apenas fazer perguntas incesantes e rituais p�fios._\n\n" +
                "(atualiza o banco de dados de condi��es do bot)\n" +
                "Uso: `%s" + NOME.toLowerCase() + "`\n" +
                (getAliasesToString().length() > 0 ? "Tente tamb�m: " + getAliasesToString() : "");
    }

    @Override
    public String getResumoComando() {
        return "\n`%s" + NOME.toLowerCase() + "`\nReseta o banco de dados de condi��es e importa todas do arquivo base.\n";
    }

    @Override
    public boolean isAdministrativo() {
        return true;
    }
}
