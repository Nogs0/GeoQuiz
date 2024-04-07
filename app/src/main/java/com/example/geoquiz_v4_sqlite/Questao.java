package com.example.geoquiz_v4_sqlite;

import java.util.UUID;

public class Questao {
    private UUID mId;
    private String mTexto;
    private boolean mRespostaCorreta;

    public Questao(String texto, boolean respostaCorreta) {
        this.mTexto = texto;
        this.mRespostaCorreta = respostaCorreta;
        this.mId = UUID.randomUUID();
    }

    public Questao(String texto, boolean respostaCorreta, UUID id) {
        this.mTexto = texto;
        this.mRespostaCorreta = respostaCorreta;
        this.mId = id;
    }

    UUID getId(){return mId;};

    public String getTexto() {
        return this.mTexto;
    }
    public boolean isRespostaCorreta() {
        return mRespostaCorreta;
    }
}
