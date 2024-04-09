package com.example.geoquiz_v4_sqlite.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class QuestoesDBHelper extends SQLiteOpenHelper {
    private static final int VERSAO = 1;
    private static final String NOME_DATABASE = "questoesDB";

    public QuestoesDBHelper(Context context) {
        super(context, NOME_DATABASE, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + QuestoesDbSchema.QuestoesTbl.NOME);
        db.execSQL("DROP TABLE IF EXISTS " + QuestoesDbSchema.RespostasTbl.NOME);
        db.execSQL("CREATE TABLE " + QuestoesDbSchema.QuestoesTbl.NOME + "(" +
                "_id integer PRIMARY KEY autoincrement, " +
                QuestoesDbSchema.QuestoesTbl.Cols.UUID + ", " +
                QuestoesDbSchema.QuestoesTbl.Cols.QUESTAO_CORRETA + " integer, " +
                QuestoesDbSchema.QuestoesTbl.Cols.TEXTO_QUESTAO + ")");

        db.execSQL("CREATE TABLE " + QuestoesDbSchema.RespostasTbl.NOME + "(" +
                "_id integer PRIMARY KEY autoincrement, " +
                QuestoesDbSchema.RespostasTbl.Cols.UUID + ", " +
                QuestoesDbSchema.RespostasTbl.Cols.RESPOSTA_CORRETA + " integer, " +
                QuestoesDbSchema.RespostasTbl.Cols.RESPOSTA_OFERECIDA + " integer, " +
                QuestoesDbSchema.RespostasTbl.Cols.COLOU + " integer, " +
                QuestoesDbSchema.RespostasTbl.Cols.QUESTAO_UUID + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versaoAntiga, int novaVersao) {
        onCreate(db);
    }
}