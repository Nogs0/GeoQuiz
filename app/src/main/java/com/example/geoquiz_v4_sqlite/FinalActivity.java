package com.example.geoquiz_v4_sqlite;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class FinalActivity extends AppCompatActivity {

    private TextView mTextViewFinal;
    private Button mButtonBeleza;

    @Override
    protected void onCreate(Bundle instanciaSalva) {
        super.onCreate(instanciaSalva);
        setContentView(R.layout.activity_final);

        mTextViewFinal = (TextView) findViewById(R.id.view_texto_final);
        mTextViewFinal.setText(R.string.acabou);

        mButtonBeleza = (Button) findViewById(R.id.botao_voltar);
        mButtonBeleza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FinalActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
