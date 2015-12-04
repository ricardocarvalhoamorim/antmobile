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

        String about = "<p>O ANT Mobile é uma aplicação para explorar recursos no sistema de informação da Univesridade do Porto. Foi desenvolvida por Ricardo Amorim (paginas.fe.up.pt/~rcamorim) no seu tempo livre como forma" +
                "de se manter a par das novidades do mundo da programação móvel" +
                "Foi desenvolvido para permitir o acesso simplificado à informação disponibilizada nos vários sistemas de informação da universidade.</p>" +
                "<p>Toda a informação apresentada é extraída a partir da plataforma ANT (ant.fe.up.pt). Esta plataforma é o fruto de uma bolsa de investigação a " +
                "cargo de José Devezas (josedevezas.com). Atualmente, é utilizado como caso de estudo central o sistema de informação SIGARRA, sendo, contudo, o " +
                "nosso objetivo evoluir para um sistema modular e mais genérico, criando ramificações em várias subáreas de investigação, permitindo contribuições adicionais" +
                " aplicadas ao nosso caso de estudo em particular, mas também experimentações diretamente suportadas pela nossa plataforma (o ANT Mobile é um exemplo concreto " +
                "destas experimentações).</p>";
                ((TextView) rootView.findViewById(R.id.text_about)).setText(Html.fromHtml(about));

        return rootView;
    }

}
