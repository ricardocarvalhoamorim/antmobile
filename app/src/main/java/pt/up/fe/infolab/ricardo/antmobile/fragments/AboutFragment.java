package pt.up.fe.infolab.ricardo.antmobile.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

        rootView.findViewById(R.id.bt_rate_us).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
                }
            }
        });
        String about = "<p>O ANT Mobile é uma aplicação para explorar recursos no sistema de informação da Universidade do Porto. Foi desenvolvida " +
                "por dois programadores: Ricardo Amorim e José Devezas. Trabalhamos atualmente no laboratório de sistemas de informação (InfoLab), na " +
                "sala I123 e tivemos originalmente a ideia de criar uma aplicação para facilitar a pesquisa de informação para os novos alunos. Esta aplicação é" +
                " um projeto pessoal e conta por isso com todo o nosso empenho, sempre que os restantes projetos a que estamos alocados o permitam." +
                "<br /><br/>   &#8226; O <b>ANT</b> é a plataforma que fornece toda a informação aqui apresentada. Está a ser desenvolvida pelo José Devezas, sob uma bolsa" +
                "de investigação com o objetivo de procurar formas de acelerar motores de pesquisa com base na indexação de informação. Neste caso particular, o" +
                "ANT recolhe dados disponíveis no SIGARRA (de forma pública), indexa-os e trata-os para futura apresentação. As diferenças em tempos de resposta" +
                "são muito competitivas, e o motor semântico permite identificar relações entre elementos pesquisados. Por exemplo se pesquisar-mos" +
                " Ricardo Amorim e José Devezas o ANT acrescenta dados que os relacionem, neste caso o local de trabalho. Para saberes mais sobre este projeto: ant.fe.up.pt" +
                "<br /><br/>   &#8226; O <b>ANT Mobile</b> por sua vez permite pesquisar de forma similar, numa interface móvel. Além disso procuramos incluir outras fontes" +
                " de informação que têm sido recentemente requisitadas, como a ocupação dos parques de estacionamento e a ementa das cantinas (sempre que disponível, e " +
                "integralmente dependente da infraestrutura da Faculdade de Engenharia)";

               // ((TextView) rootView.findViewById(R.id.text_about)).setText(Html.fromHtml(about));

        return rootView;
    }

}
