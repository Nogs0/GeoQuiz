package com.example.geoquiz_v4_sqlite;

import android.database.Cursor;

import com.example.geoquiz_v4_sqlite.Database.QuestoesDbSchema;

import java.util.UUID;

public class Mapper {
    public static Resposta mapResposta(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndex(QuestoesDbSchema.RespostasTbl.Cols.UUID));
        String respostaCorreta = cursor.getString(cursor.getColumnIndex(QuestoesDbSchema.RespostasTbl.Cols.RESPOSTA_CORRETA));
        String respostOferecida = cursor.getString(cursor.getColumnIndex(QuestoesDbSchema.RespostasTbl.Cols.RESPOSTA_OFERECIDA));
        String colou = cursor.getString(cursor.getColumnIndex(QuestoesDbSchema.RespostasTbl.Cols.COLOU));
        String questaoId = cursor.getString(cursor.getColumnIndex(QuestoesDbSchema.RespostasTbl.Cols.QUESTAO_UUID));

        return new Resposta(respostaCorreta.equals("1"), respostOferecida.equals("1"), colou.equals("1"), UUID.fromString(questaoId), UUID.fromString(id));
    }

    public static Questao mapQuestao(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndex(QuestoesDbSchema.QuestoesTbl.Cols.UUID));
        String respostaCorreta = cursor.getString(cursor.getColumnIndex(QuestoesDbSchema.QuestoesTbl.Cols.QUESTAO_CORRETA));
        String texto = cursor.getString(cursor.getColumnIndex(QuestoesDbSchema.QuestoesTbl.Cols.TEXTO_QUESTAO));

        return new Questao(texto, respostaCorreta.equals("1"), UUID.fromString(id));
    }
}
