package com.example.geoquiz_v4_sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.geoquiz_v4_sqlite.Database.QuestoesDBHelper;
import com.example.geoquiz_v4_sqlite.Database.QuestoesDbSchema;

public class RespostaDB {
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public RespostaDB(Context contexto){
        mContext = contexto.getApplicationContext();
        mDatabase = new QuestoesDBHelper(mContext).getWritableDatabase();
    }

    private static ContentValues getValoresConteudo(Resposta r){
        ContentValues valores = new ContentValues();

        // pares chave-valor: nomes das colunas - valores
        valores.put(QuestoesDbSchema.RespostasTbl.Cols.UUID, r.getId().toString());
        valores.put(QuestoesDbSchema.RespostasTbl.Cols.RESPOSTA_OFERECIDA, r.getRespostaOferecida());
        valores.put(QuestoesDbSchema.RespostasTbl.Cols.RESPOSTA_CORRETA, r.isRespostaCorreta());
        valores.put(QuestoesDbSchema.RespostasTbl.Cols.COLOU, r.isColou());
        valores.put(QuestoesDbSchema.RespostasTbl.Cols.QUESTAO_UUID, r.getQuestaoId().toString());
        return valores;
    }
    public void addResposta(Resposta r){
        ContentValues valores = getValoresConteudo(r);
        mDatabase.insert(QuestoesDbSchema.RespostasTbl.NOME, null, valores);
    }

//    public void updateResposta(Resposta r){
//        String uuidString = r.getId().toString();
//        ContentValues valores = getValoresConteudo(r);
//        mDatabase.update(QuestoesDbSchema.RespostasTbl.NOME, valores, QuestoesDbSchema.RespostasTbl.Cols.UUID +" = ?",
//                new String[] {uuidString});
//    }

    public Cursor queryResposta(String clausulaWhere, String[] argsWhere){
        Cursor cursor = mDatabase.query(QuestoesDbSchema.RespostasTbl.NOME,
                null,  // todas as colunas
                clausulaWhere,
                argsWhere,
                null, // sem group by
                null, // sem having
                null  // sem order by
        );

        return cursor;
    }

    void removeRespostas(){
        int delete;
        delete = mDatabase.delete(
                QuestoesDbSchema.RespostasTbl.NOME,
                null, null);
    }
}
