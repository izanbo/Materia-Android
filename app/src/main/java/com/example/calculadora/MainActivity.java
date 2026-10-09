package com.example.calculadora;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private TextView tvDisplay;

    private Button btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btnComa;
    private Button btnC, btnDel, btnPorcentaje, btnDiv, btnMult, btnResta, btnSuma, btnIgual;
    private Button btnMC, btnMR, btnMMinus, btnMPlus;

    private boolean resultadoMostrado = false;
    private double memoria = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvDisplay = findViewById(R.id.display);

        btn0 = findViewById(R.id.button0);
        btn1 = findViewById(R.id.button1);
        btn2 = findViewById(R.id.button2);
        btn3 = findViewById(R.id.button3);
        btn4 = findViewById(R.id.button4);
        btn5 = findViewById(R.id.button5);
        btn6 = findViewById(R.id.button6);
        btn7 = findViewById(R.id.button7);
        btn8 = findViewById(R.id.button8);
        btn9 = findViewById(R.id.button9);
        btnComa = findViewById(R.id.buttonComa);

        btnC = findViewById(R.id.button12);
        btnDel = findViewById(R.id.button13);
        btnPorcentaje = findViewById(R.id.button14);
        btnDiv = findViewById(R.id.button15);
        btnMult = findViewById(R.id.button16);
        btnResta = findViewById(R.id.button17);
        btnSuma = findViewById(R.id.button18);
        btnIgual = findViewById(R.id.button19);

        btnMC = findViewById(R.id.btnMC);
        btnMR = findViewById(R.id.btnMR);
        btnMMinus = findViewById(R.id.btnMMinus);
        btnMPlus = findViewById(R.id.btnMPlus);

        View.OnClickListener listenerNumeros = v -> {
            Button btn = (Button) v;
            String textoBtn = btn.getText().toString();
            String textoActual = tvDisplay.getText().toString();

            if (textoActual.equals("0") || resultadoMostrado) {
                if (textoBtn.equals(",")) {
                    tvDisplay.setText("0,");
                } else {
                    tvDisplay.setText(textoBtn);
                }
                resultadoMostrado = false;
            } else {
                tvDisplay.setText(textoActual + textoBtn);
            }
        };

        Button[] listaNumeros = {btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btnComa};
        for (Button btn : listaNumeros) {
            if (btn != null) btn.setOnClickListener(listenerNumeros);
        }

        View.OnClickListener listenerOperaciones = v -> {
            int id = v.getId();
            Button btn = (Button) v;
            String textoActual = tvDisplay.getText().toString();

            if (id == R.id.button12) { // AC
                tvDisplay.setText("0");
                resultadoMostrado = false;

            } else if (id == R.id.button13) { // Del
                if (textoActual.length() > 1 && !resultadoMostrado) {
                    tvDisplay.setText(textoActual.substring(0, textoActual.length() - 1));
                } else {
                    tvDisplay.setText("0");
                    resultadoMostrado = false;
                }

            } else if (id == R.id.button18 || id == R.id.button17 ||
                    id == R.id.button16 || id == R.id.button15) { // Operadores

                String op = btn.getText().toString();
                if (resultadoMostrado) {
                    resultadoMostrado = false;
                }

                // Evitar poner dos operadores seguidos
                char ultimoChar = textoActual.charAt(textoActual.length() - 1);
                if (esOperador(ultimoChar)) {
                    tvDisplay.setText(textoActual.substring(0, textoActual.length() - 1) + op);
                } else {
                    tvDisplay.setText(textoActual + op);
                }

            } else if (id == R.id.button19) { // =
                calcularExpresion(textoActual);

            } else if (id == R.id.button14) { // %
                try {
                    double val = Double.parseDouble(textoActual.replace(",", "."));
                    mostrarResultado(val / 100);
                } catch (Exception e) {
                    tvDisplay.setText("Error");
                }

            } else if (id == R.id.btnMC) {
                memoria = 0;

            } else if (id == R.id.btnMR) {
                mostrarResultado(memoria);

            } else if (id == R.id.btnMPlus) {
                try {
                    memoria += Double.parseDouble(tvDisplay.getText().toString().replace(",", "."));
                } catch (Exception ignored) {}

            } else if (id == R.id.btnMMinus) {
                try {
                    memoria -= Double.parseDouble(tvDisplay.getText().toString().replace(",", "."));
                } catch (Exception ignored) {}
            }
        };

        Button[] listaOperaciones = {
                btnC, btnDel, btnPorcentaje, btnDiv, btnMult, btnResta, btnSuma, btnIgual,
                btnMC, btnMR, btnMMinus, btnMPlus
        };

        for (Button btn : listaOperaciones) {
            if (btn != null) btn.setOnClickListener(listenerOperaciones);
        }
    }

    private boolean esOperador(char c) {
        return c == '+' || c == '−' || c == '×' || c == '÷';
    }

    private void calcularExpresion(String expresion) {
        try {
            // Normalizar la cadena para procesarla fácilmente
            String expr = expresion.replace(",", ".").replace("×", "*").replace("÷", "/").replace("−", "-");

            // Evaluar tokens simples (números y operadores) de izquierda a derecha
            double resultado = 0;
            char opActual = '+';
            StringBuilder numActual = new StringBuilder();

            for (int i = 0; i < expr.length(); i++) {
                char c = expr.charAt(i);

                if (Character.isDigit(c) || c == '.') {
                    numActual.append(c);
                }

                if (c == '+' || c == '-' || c == '*' || c == '/' || i == expr.length() - 1) {
                    if (numActual.length() > 0) {
                        double val = Double.parseDouble(numActual.toString());
                        switch (opActual) {
                            case '+': resultado += val; break;
                            case '-': resultado -= val; break;
                            case '*': resultado *= val; break;
                            case '/':
                                if (val == 0) {
                                    tvDisplay.setText("Error");
                                    resultadoMostrado = true;
                                    return;
                                }
                                resultado /= val;
                                break;
                        }
                        numActual.setLength(0);
                    }
                    opActual = c;
                }
            }

            mostrarResultado(resultado);
        } catch (Exception e) {
            tvDisplay.setText("Error");
            resultadoMostrado = true;
        }
    }

    private void mostrarResultado(double valor) {
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols(Locale.getDefault());
        simbolos.setDecimalSeparator(',');

        DecimalFormat formato = new DecimalFormat("#,##0.###", simbolos);
        formato.setGroupingUsed(false);

        tvDisplay.setText(formato.format(valor));
        resultadoMostrado = true;
    }
}