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

    private static final String TAG = "QuizActivity";
    private static final String CHAVE_INDICE = "INDICE";
    private static final int CODIGO_REQUISICAO_COLA = 0;

    private Questao[] mBancoDeQuestoes = new Questao[]{
            new Questao("Palmeiras tem mundial?", false),
            new Questao("O nome real do Boca é João Boca?", false),
            new Questao("A capital da Alemanha é Berlim?", true)
    };

    QuestaoDB mQuestoesDb;

    RespostaDB mRespostasDb;

    private int mIndiceAtual = 0;

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
                mIndiceAtual = (mIndiceAtual + 1) % mBancoDeQuestoes.length;
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
                                mTextViewRespostasArmazenadas.append("\n Resposta Correta: " + (questao.isRespostaCorreta() ? "Verdadeiro" : "Falso"));
                                mTextViewRespostasArmazenadas.append("\n Resposta oferecida: " + (resposta.getRespostaOferecida() ? "Verdadeiro" : "Falso"));
                                mTextViewRespostasArmazenadas.append("\n-------------------------------------------------------");
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
                    if (mTextViewRespostasArmazenadas == null) {
                        mTextViewRespostasArmazenadas = (TextView) findViewById(R.id.texto_respostas_a_apresentar);
                    }
                    mTextViewRespostasArmazenadas.setText("");
                }
            }
        });

    }

    private void atualizaQuestao() {
        Questao questao = mBancoDeQuestoes[mIndiceAtual];
        mTextViewQuestao.setText(questao.getTexto());
    }

    private void verificaResposta(boolean respostaPressionada) {
        boolean respostaCorreta = mBancoDeQuestoes[mIndiceAtual].isRespostaCorreta();
        int idMensagemResposta = 0;

        if (mEhColador) {
            idMensagemResposta = R.string.toast_julgamento;
            mRespostasDb.addResposta(new Resposta(respostaCorreta, false, true, mBancoDeQuestoes[mIndiceAtual].getId()));
        } else {
            if (respostaPressionada == respostaCorreta) {
                idMensagemResposta = R.string.toast_correto;
                mRespostasDb.addResposta(new Resposta(true, respostaPressionada, false, mBancoDeQuestoes[mIndiceAtual].getId()));
            } else {
                idMensagemResposta = R.string.toast_incorreto;
                mRespostasDb.addResposta(new Resposta(false, respostaPressionada, false, mBancoDeQuestoes[mIndiceAtual].getId()));
            }
        }
        Toast.makeText(this, idMensagemResposta, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveInstanceState(Bundle instanciaSalva) {
        super.onSaveInstanceState(instanciaSalva);
        Log.i(TAG, "onSaveInstanceState()");
        instanciaSalva.putInt(CHAVE_INDICE, mIndiceAtual);
    }

    @Override
    protected void onActivityResult(int codigoRequisicao, int codigoResultado, Intent dados) {
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