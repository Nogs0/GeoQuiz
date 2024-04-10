package com.example.geoquiz_v4_sqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/*
  Modelo de projeto para a Atividade 1.
  Será preciso adicionar o cadastro das respostas do usuário ao Quiz, conforme
  definido no Canvas.

  GitHub: https://github.com/udofritzke/GeoQuiz
 */

public class MainActivity extends AppCompatActivity {
    private Button mBotaoVerdadeiro;
    private Button mBotaoFalso;
    private Button mBotaoProximo;
    private Button mBotaoMostra;
    private Button mBotaoDeleta;

    private Button mBotaoCola;

    private TextView mTextViewQuestao;
    private TextView mTextViewRespostasArmazenadas;

    private TextView mTextViewTextoPontuacao;
    private TextView mTextViewPontuacao;

    private static final String TAG = "QuizActivity";
    private static final String CHAVE_INDICE = "INDICE";
    private static final int CODIGO_REQUISICAO_COLA = 0;

    public int pontuacaoAtual = 0;
    private final Questao[] mBancoDeQuestoes = new Questao[]{
            new Questao("Palmeiras tem mundial?", false),
            new Questao("O nome real do Boca é João Boca?", false),
            new Questao("O relâmpago é visto antes de ser ouvido porque a luz viaja mais rápido que o som.", true),
            new Questao("A Cidade do Vaticano é um país.", true),
            new Questao("Melbourne é a capital da Austrália", false),
            new Questao("A penicilina foi descoberta no Vietnã para tratar a malária.", false),
            new Questao("O Monte Fuji é a montanha mais alta do Japão.", true),
            new Questao("Brócolis contém mais vitamina C do que limões.", true),
            new Questao("O crânio é o osso mais forte do corpo humano.", false),
            new Questao("O Google foi inicialmente chamado de BackRub.", true),
            new Questao("A caixa preta em um avião é preta.", false),
            new Questao("Tomates são frutas.", true),
            new Questao("A atmosfera de Mercúrio é composta de dióxido de carbono.", false),
            new Questao("A depressão é a principal causa de incapacidade em todo o mundo.", true),
            new Questao("Cleópatra era descendente de egípcios.", false),
            new Questao("Você pode espirrar enquanto dorme.", false),
            new Questao("É impossível espirrar enquanto você abre os olhos.", true),
            new Questao("Bananas são bagas.", true),
            new Questao("Se você somar os dois números dos lados opostos dos dados, a resposta será sempre 7", true),
            new Questao("As vieiras não podem ver", false),
            new Questao("A construção da Torre Eiffel foi concluída em 31 de março de 1887", false)
    };

    QuestaoDB mQuestoesDb;

    RespostaDB mRespostasDb;

    private Integer mIndiceAtual = 0;

    private boolean mEhColador;

    @Override
    protected void onCreate(Bundle instanciaSalva) {
        super.onCreate(instanciaSalva);
        setContentView(R.layout.activity_main);

        // Acesso SQLite
        if (mQuestoesDb == null)
            mQuestoesDb = new QuestaoDB(getBaseContext());

        if (mRespostasDb == null)
            mRespostasDb = new RespostaDB(getBaseContext());

        if (instanciaSalva != null) {
            mIndiceAtual = instanciaSalva.getInt(CHAVE_INDICE, 0);
        }

        for (Questao questao : mBancoDeQuestoes) {
            mQuestoesDb.addQuestao(questao);
        }

        mTextViewTextoPontuacao = (TextView) findViewById(R.id.view_texto_pontuacao);
        mTextViewTextoPontuacao.setText(R.string.texto_pontuacao);
        mTextViewPontuacao = (TextView) findViewById(R.id.view_pontuacao);
        atualizaPontuacao();

        mTextViewQuestao = (TextView) findViewById(R.id.view_texto_da_questao);
        atualizaQuestao();

        mBotaoVerdadeiro = (Button) findViewById(R.id.botao_verdadeiro);
        // utilização de classe anônima interna
        mBotaoVerdadeiro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificaResposta(true);
            }
        });

        mBotaoFalso = (Button) findViewById(R.id.botao_falso);
        mBotaoFalso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificaResposta(false);
            }
        });
        mBotaoProximo = (Button) findViewById(R.id.botao_proximo);
        mBotaoProximo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEhColador = false;
                atualizaQuestao();
            }
        });

        mBotaoCola = (Button) findViewById(R.id.botao_cola);
        mBotaoCola.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // inicia ColaActivity
                // Intent intent = new Intent(MainActivity.this, ColaActivity.class);
                boolean respostaEVerdadeira = mBancoDeQuestoes[mIndiceAtual].isRespostaCorreta();
                Intent intent = ColaActivity.novoIntent(MainActivity.this, respostaEVerdadeira);
                //startActivity(intent);
                startActivityForResult(intent, CODIGO_REQUISICAO_COLA);
            }
        });

        mBotaoMostra = (Button) findViewById(R.id.botao_mostra_questoes);
        mBotaoMostra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                  Acesso ao SQLite
                */
                if (mRespostasDb == null) return;
                if (mQuestoesDb == null) return;

                if (mTextViewRespostasArmazenadas == null) {
                    mTextViewRespostasArmazenadas = (TextView) findViewById(R.id.texto_respostas_a_apresentar);
                } else {
                    mTextViewRespostasArmazenadas.setText("");
                }

                Cursor cursorRespostas = mRespostasDb.queryResposta(null, null);
                if (cursorRespostas != null) {
                    if (cursorRespostas.getCount() == 0) {
                        mTextViewRespostasArmazenadas.setText("Nenhuma resposta foi dada!");
                        Log.i("MSGS", "Nenhum resultado");
                    }
                    try {
                        cursorRespostas.moveToFirst();
                        while (!cursorRespostas.isAfterLast()) {
                            Resposta resposta = Mapper.mapResposta(cursorRespostas);
                            Cursor cursorQuestao = mQuestoesDb.queryQuestao("uuid = ?", new String[]{resposta.getQuestaoId().toString()});
                            try {
                                cursorQuestao.moveToFirst();
                                Questao questao = Mapper.mapQuestao(cursorQuestao);

                                mTextViewRespostasArmazenadas.append("\n Questão: " + questao.getTexto());
                                mTextViewRespostasArmazenadas.append("\n Colador: " + (resposta.isColou() ? "Sim" : "Não"));
                                mTextViewRespostasArmazenadas.append("\n Resposta Correta: " + (questao.isRespostaCorreta() ? "Verdadeiro" : "Falso"));
                                mTextViewRespostasArmazenadas.append("\n Resposta oferecida: " + (resposta.getRespostaOferecida() ? "Verdadeiro" : "Falso"));
                                mTextViewRespostasArmazenadas.append("\n------------------------------------------------------------------------------");
                            } finally {
                                cursorQuestao.close();
                            }
                            cursorRespostas.moveToNext();
                        }
                    } finally {
                        cursorRespostas.close();
                    }
                } else
                    Log.i("MSGS", "cursor nulo!");
            }
        });
        mBotaoDeleta = (Button) findViewById(R.id.botao_deleta);
        mBotaoDeleta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                  Acesso ao SQLite
                */
                if (mRespostasDb != null) {
                    mRespostasDb.removeRespostas();
                    atualizaPontuacao();
                    if (mTextViewRespostasArmazenadas == null) {
                        mTextViewRespostasArmazenadas = (TextView) findViewById(R.id.texto_respostas_a_apresentar);
                    }
                    mTextViewRespostasArmazenadas.setText("");
                }
            }
        });

    }

    private void atualizaQuestao() {
        if (mIndiceAtual == mBancoDeQuestoes.length - 1) {
            mRespostasDb.removeRespostas();
            mIndiceAtual = 0;
            Intent intent = new Intent(MainActivity.this, FinalActivity.class);
            startActivity(intent);
        } else
            mIndiceAtual = (mIndiceAtual + 1) % mBancoDeQuestoes.length;
        Questao questao = mBancoDeQuestoes[mIndiceAtual];
        mTextViewQuestao.setText(questao.getTexto());
    }

    private void verificaResposta(boolean respostaPressionada) {
        boolean respostaCorreta = mBancoDeQuestoes[mIndiceAtual].isRespostaCorreta();

        if (mTextViewQuestao.getText() == "Correto!" || mTextViewQuestao.getText() == "Incorreto!" || mTextViewQuestao.getText() == "Você deve avançar para a próxima pergunta!" || mTextViewQuestao.getText() == "Não vale! Você colou!"){
            mTextViewQuestao.setText("Você deve avançar para a próxima pergunta!");
            return;
        }

        if (mEhColador) {
            mTextViewQuestao.setText("Não vale! Você colou!");
            mRespostasDb.addResposta(new Resposta(respostaCorreta, false, true, mBancoDeQuestoes[mIndiceAtual].getId()));
            mEhColador = false;
        } else {
            if (respostaPressionada == respostaCorreta)
                mTextViewQuestao.setText("Correto!");
            else mTextViewQuestao.setText("Incorreto!");

            mRespostasDb.addResposta(new Resposta(respostaPressionada == respostaCorreta, respostaPressionada, false, mBancoDeQuestoes[mIndiceAtual].getId()));
            atualizaPontuacao();
        }
    }

    private void atualizaPontuacao() {
        if (mRespostasDb != null) {
            Cursor cursorResposta = mRespostasDb.queryResposta("resposta_correta = 1 and colou = 0", null);
            pontuacaoAtual = cursorResposta.getCount();

            mTextViewPontuacao.setText(String.valueOf(pontuacaoAtual));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle instanciaSalva) {
        super.onSaveInstanceState(instanciaSalva);
        Log.i(TAG, "onSaveInstanceState()");
        instanciaSalva.putInt(CHAVE_INDICE, mIndiceAtual);
    }

    @Override
    protected void onActivityResult(int codigoRequisicao, int codigoResultado, Intent dados) {
        super.onActivityResult(codigoRequisicao, codigoResultado, dados);
        if (codigoResultado != Activity.RESULT_OK) {
            return;
        }
        if (codigoRequisicao == CODIGO_REQUISICAO_COLA) {
            if (dados == null) {
                return;
            }
            mEhColador = ColaActivity.foiMostradaResposta(dados);
        }
    }
}