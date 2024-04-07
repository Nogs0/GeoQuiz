package com.example.geoquiz_v4_sqlite;

import java.util.UUID;

public class Resposta {
    private UUID mId;
    private boolean mRespostaOferecida;
    private boolean mRespostaCorreta;
    private boolean mColou;
    private UUID mQuestaoId;

    public Resposta(boolean respostaCorreta, boolean respostaOferecida, boolean colou, UUID questaoId) {
        this.mRespostaOferecida = respostaOferecida;
        this.mRespostaCorreta = respostaCorreta;
        this.mColou = colou;
        this.mQuestaoId = questaoId;
        mId = UUID.randomUUID();
    }

    public Resposta(boolean respostaCorreta, boolean respostaOferecida, boolean colou, UUID questaoId, UUID id) {
        this.mRespostaOferecida = respostaOferecida;
        this.mRespostaCorreta = respostaCorreta;
        this.mColou = colou;
        this.mQuestaoId = questaoId;
        this.mId = id;
    }


    UUID getId() {
        return mId;
    }

    UUID getQuestaoId() {
        return mQuestaoId;
    }

    public boolean getRespostaOferecida() {
        return mRespostaOferecida;
    }

    public void setRepostaOferecida(boolean repostaOferecida) {
        mRespostaOferecida = repostaOferecida;
    }

    public boolean isRespostaCorreta() {
        return mRespostaCorreta;
    }

    public boolean isColou() {
        return mColou;
    }

    public void setRespostaCorreta(boolean respostaCorreta) {
        mRespostaCorreta = respostaCorreta;
    }
}
