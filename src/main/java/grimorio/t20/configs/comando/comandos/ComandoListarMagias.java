package grimorio.t20.configs.comando.comandos;

import grimorio.t20.configs.comando.IComando;
import grimorio.t20.configs.comando.IComandoContext;
import grimorio.t20.database.IDatabaseGerenciar;
import grimorio.t20.struct.Magia;
import grimorio.t20.struct.Padroes;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ComandoListarMagias implements IComando {

    public static final String NOME = "ListarMagias";

    @Override
    public void gerenciar(IComandoContext context) {
        List<String> args = context.getArgs();
        TextChannel canal = context.getChannel();
        List<String> listEscolas = new ArrayList<>();
        List<Integer> listNiveis = new ArrayList<>();
        boolean isArcana = false, isDivina = false;
        String parm;

        for (int i = 0; i < args.size(); i++) {
            parm = args.get(i);
            // Se tiver s� dois caracteres, verifica se � um par�metro v�lido, se n�o, considera uma escola da busca
            if (parm.length() == 2) {
                // Se iniciar com o sinal de menos "-" ent�o � qualificado para ser um par�metro, se n�o, considera uma escola da busca
                if (parm.charAt(0) == '-') {
                    // Se for um d�gito, o par�metro informado � um n�vel de magia
                    if (Character.isDigit(parm.charAt(1))) {
                        listNiveis.add(Integer.parseInt(String.valueOf(parm.charAt(1))));
                    } else
                        switch (parm.charAt(1)) {
                            case 'a':
                            case 'A':
                                isArcana = true;
                                break;
                            case 'd':
                            case 'D':
                                isDivina = true;
                                break;
                        }
                } else
                    listEscolas.add(args.get(i));
            } else
                listEscolas.add(args.get(i));
        }

        Map<Integer, Magia> mapMagias = IDatabaseGerenciar.INSTANCE.ListarMagias(listEscolas, listNiveis, isArcana, isDivina);

        if (mapMagias.size() <= 0) {
            canal.sendMessageEmbeds(
                    Padroes.getMensagemErro(
                            "Magias n�o encontradas",
                            String.format("_Meu acervo n�o disp�e de nenhum feiti�o similar com os par�metros " +
                                            "informados.\nTem certeza que buscaste o que desejavas corretamente?_\n\n" +
                                            "(n�o foi poss�vel encontrar magias com os par�metros informados)")
                    ).build()
            ).queue();
            return;
        }

        List<Magia> listMagiasArcanas = new ArrayList<>(),
                    listMagiasDivinas = new ArrayList<>();

        for (Magia magia: mapMagias.values()) {
            if (magia.isArcana() && (isArcana || (!isArcana && !isDivina)))
                listMagiasArcanas.add(magia);
            if (magia.isDivina() && (isDivina || (!isArcana && !isDivina)))
                listMagiasDivinas.add(magia);
        }

        if (listMagiasArcanas.size() > 0)
            canal.sendMessageEmbeds(
                    Padroes.getMensagemListaMagia(
                                    listMagiasArcanas,
                                    true,
                                    false)
                            .build()
            ).queue();

        if (listMagiasDivinas.size() > 0)
            canal.sendMessageEmbeds(
                    Padroes.getMensagemListaMagia(
                                    listMagiasDivinas,
                                    false,
                                    true)
                            .build()
            ).queue();
    }

    @Override
    public String getNome() {
        return NOME;
    }

    @Override
    public String getAjuda(boolean mostrarAliases) {
        String ajuda = "_Vamos, mortal. Diga-me quais tipos de feiti�o procuras e eu lhe enaltecerei com conheicmento._\n\n" +
                "(consulta uma lista de magias baseado nos par�metros informados)\n" +
                "Uso: `%s"+NOME.toLowerCase()+" <parametros>`\n\n";

        if (mostrarAliases) {
            ajuda += "_Par�metros dispon�veis_:\n" +
                    "`-1`, `-2`, `-3`, `-4` e `-5` filtram os c�rculos das magias que ser�o listadas. N�o informar um " +
                    "c�rculo listar� magias de todos os c�rculos que cumpram o restante das condi��es. � poss�vel informar " +
                    "mais de um c�rculo ao mesmo tempo, por exemplo: `%s" + NOME.toLowerCase() + " -2 -5` lista todas as magias " +
                    "arcanas e divinas de 2� e 5� c�rculo.\n\n" +
                    "`-a` define que apenas magias arcanas devem ser exibidas.\n" +
                    "`-d` define que apenas magias divinas devem ser exibidas.\n" +
                    "Informar `-a` e `-d` lista tanto as magias divinas como as magias arcanas, que � o mesmo que n�o " +
                    "informar qualquer um deles.\n\n" +
                    "`<parte_do_nome_da_escola>` filtra as escolas das magias. Voc� pode informar mais de uma, separando-as " +
                    "com espa�os. Se nenhuma for informada, todas que cumpram as outras condi��es ser�o exibidas. Por " +
                    "exemplo: `%s" + NOME.toLowerCase() + " ilusao adiv -2` exibe todas as magias de ilus�o e adivinha��o de " +
                    "2� c�rculo, arcanas e divinas.\n\n" +
                    (mostrarAliases && getAliasesToString().length() > 0 ? "Tente tamb�m: " + getAliasesToString() + "\n" : "");
        } else {
            ajuda += "_Par�metros_:\n" +
                    "\u2022 niveis: Os valores aceitos s�o `-1`, `-2`, `-3`, `-4` e `-5`.\nOs valores representam os c�rculos " +
                    "das magias que ser�o listadas.\nN�o informar um c�rculo listar� magias de todos os c�rculos que " +
                    "cumpram o restante das condi��es.\n� poss�vel informar mais de um c�rculo ao mesmo tempo, por exemplo: " +
                    "`-2 -5` lista todas as de 2� e 5� c�rculo que cumpram o restante das condi��es.\n\n" +

                    "\u2022 escolas: filtra as magias pelas suas escolas.\nVoc� pode informar mais de uma, separando-as " +
                    "com espa�os.\nSe nenhuma for informada, todas que cumpram as outras condi��es ser�o exibidas, por " +
                    "exemplo: `ilusao adiv -2` exibe todas as magias de ilus�o e adivinha��o de 2� c�rculo, arcanas e divinas.\n\n" +

                    "\u2022 origem: Define se as magias listadas ser�o arcanas, divinas ou de qualquer tipo.\n" +
                    "Magias `Universais` aparecem independente da op��o selecionada.\n\n";
        }
        ajuda += "_Dica_: n�o � preciso colocar o nome das escolas por completo. \"ilu\" j� suficiente para encontrar magias " +
                "de ilus�o, por exemplo.";
        return ajuda;
    }

    @Override
    public String getResumoComando() {
        return "\n`%s" + NOME.toLowerCase() + " <par�metros>`\nConsulta uma lista de magias baseado nos par�metros " +
                "informados, separando-as por n�vel, escola e origem (arcana ou divina).\n";
    }

    @Override
    public boolean isAdministrativo() {
        return false;
    }

    @Override
    public boolean isRestritoDesenvolvedor() {
        return false;
    }

    @Override
    public List<String> getAliases() {
        return List.of("lm", "listm", "listmag");
    }

}
