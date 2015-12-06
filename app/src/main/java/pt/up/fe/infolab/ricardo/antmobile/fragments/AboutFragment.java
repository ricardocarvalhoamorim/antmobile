package pt.up.fe.infolab.ricardo.antmobile.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import pt.up.fe.infolab.ricardo.antmobile.R;

public class AboutFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_about, container, false);

        String about = "<p>O ANT Mobile é uma aplicação para explorar recursos no sistema de informação da Univesridade do Porto. Foi desenvolvida " +
                "por dois programadores entusiastas:</p>\n" +
                "&#8226; <b>Ricardo Amorim (paginas.fe.up.pt/~rcamorim)</b> um programador júnior focado em criar aplicações móveis que rapidamente" +
                " resolvam problemas do dia a dia, com uma pitada de bom design (design não é claramente o seu forte). Tem um fraquinho por chocolate" +
                " negro, e vibra com jantaradas e viagens. <br/>\n" +
                "&#8226; <b>José Devezas</b> Autor da plataforma ANT, o José tem uma grande experiência em processamento de informação, e consegue " +
                "reunir numa pessoa o know-how em diversas áreas desde a geometria até à análise de dados. Enfim um informático canivete suíço, como" +
                " qualquer empresa procura.<br/>\n" +
                "<p>São então estes os dois singelos programadores que decidiram pegar no problema do acesso a informações do SIGARRA (e da " +
                "inexistência de uma aplicação bonita o suficiente para este efeito) e tentar resolve-lo. Ora, o ANT e o ANT Mobile são parte da " +
                "solução, sendo que o ANT representa aqui a fonte de 99% da informação apresentada no ANT Mobile (não, o 1% em falta não é informação" +
                " de tracking. Corresponde apenas à ocupação do parque que vem de outro sítio (confidencial).</p>" +
                "<p>O ANT tem uma rotina que recolhe, processa e indexa os dados disponíveis no sistema de informação (no domínio público) e por isso" +
                " mesmo consegue resultados de pesquisa consideravelmente mais rápido. Além disso, tem mecanismos que procuram reconhecer entidades" +
                " como professor e aluno para encontrar relações entre indivíduos. Para saber mais, nada melhor do que consultar a página do ant " +
                "(ant.fe.up.pt).</p>" +
                "<br/><p> O nosso objetivo é que esta abordagem possa ser aplicada para obter informação a partir de qualquer sistema de informação " +
                "(o SIGARRA foi o exemplo de teste). Claro que não podíamos deixar features valiosas de lado e por isso decidimos incluir cantinas " +
                "e parques.</p>"
                ;
                ((TextView) rootView.findViewById(R.id.text_about)).setText(Html.fromHtml(about));

        return rootView;
    }

}
