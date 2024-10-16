package com.example.androidcalculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;

import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity {
    private TextView primaryTextView;
    private TextView secondaryTextView;
    private boolean isDefaultTextDisplayed = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize TextViews
        primaryTextView = findViewById(R.id.idTVprimary);
        secondaryTextView = findViewById(R.id.idTVSecondary);
        // Set initial text in TextViews
        secondaryTextView.setText("0");
        primaryTextView.setText("0");
        isDefaultTextDisplayed = true;
        setupButtons();
    }

    private void setupButtons() {
        // Array of button IDs to set listeners
        int[] buttonIds = new int[]{
                R.id.b0, R.id.b1, R.id.b2, R.id.b3, R.id.b4, R.id.b5,
                R.id.b6, R.id.b7, R.id.b8, R.id.b9, R.id.bplus,
                R.id.bminus, R.id.bmul, R.id.bdiv, R.id.bdot,
                R.id.bsin, R.id.bcos, R.id.btan, R.id.blog,
                R.id.bln, R.id.bsqrt, R.id.bsquare, R.id.bfact,
                R.id.binv, R.id.bbrac1, R.id.bbrac2, R.id.bpi,
                R.id.bac, R.id.bc, R.id.bequal
        };
        // Listener for button clicks
        View.OnClickListener listener = v -> {
            Button button = (Button) v;
            final String buttonText = button.getText().toString();
            handleButtonClick(button.getId(), buttonText);
        };

        for (int id : buttonIds) {
            findViewById(id).setOnClickListener(listener);
        }
    }

    private void handleButtonClick(int buttonId, String buttonText) {

        if (isDefaultTextDisplayed) {
            primaryTextView.setText("");
            secondaryTextView.setText("");
            isDefaultTextDisplayed = false;
        }

        if (buttonId == R.id.bac) {
            primaryTextView.setText("");
            secondaryTextView.setText("");
        } else if (buttonId == R.id.bc) {

            String currentText = primaryTextView.getText().toString();
            if (!currentText.isEmpty()) {
                primaryTextView.setText(currentText.substring(0, currentText.length() - 1));
            }
            String secondaryText = secondaryTextView.getText().toString();
            if (!secondaryText.isEmpty()) {
                secondaryTextView.setText(secondaryText.substring(0, secondaryText.length() - 1));
            }
        } else if (buttonId == R.id.bpi) {
            primaryTextView.append("pi");
            secondaryTextView.append("π");
        } else if (buttonId == R.id.bsqrt) {
            primaryTextView.append("sqrt(");
            secondaryTextView.append("√(");
        } else if (buttonId == R.id.bfact) {
            primaryTextView.append("fact(");
            secondaryTextView.append("!");
        } else if (buttonId == R.id.bsquare) {
            primaryTextView.append("^2");
            secondaryTextView.append("^2");
        } else if (buttonId == R.id.binv) {
            primaryTextView.append("1/");
            secondaryTextView.append("1/");
        } else if (buttonId == R.id.bplus) {
            primaryTextView.append("+");
            secondaryTextView.append("+");
        } else if (buttonId == R.id.bminus) {
            primaryTextView.append("-");
            secondaryTextView.append("-");
        } else if (buttonId == R.id.bmul) {
            primaryTextView.append("*");
            secondaryTextView.append("×");
        } else if (buttonId == R.id.bdiv) {
            primaryTextView.append("/");
            secondaryTextView.append("÷");
        } else if (buttonId == R.id.bequal) {
            evaluateExpression();
        } else {
            primaryTextView.append(buttonText);
            secondaryTextView.append(buttonText);
        }
    }

    private void evaluateExpression() {
        String expression = primaryTextView.getText().toString();
        try {
            Expression exp = new ExpressionBuilder(expression)
                    .functions(new Function("fact", 1) {
                        @Override
                        public double apply(double... args) {
                            return factorial(args[0]);
                        }
                    })
                    .functions(new Function("sqrt", 1) {
                        @Override
                        public double apply(double... args) {
                            return Math.sqrt(args[0]);
                        }
                    })
                    .functions(new Function("pow", 2) {
                        @Override
                        public double apply(double... args) {
                            BigDecimal base = BigDecimal.valueOf(args[0]);
                            BigDecimal result = base.pow((int) args[1]);
                            return result.doubleValue();
                        }
                    })
                    .functions(new Function("sin", 1) {
                        @Override
                        public double apply(double... args) {
                            return Math.sin(Math.toRadians(args[0]));
                        }
                    })
                    .functions(new Function("cos", 1) {
                        @Override
                        public double apply(double... args) {
                            return Math.cos(Math.toRadians(args[0]));
                        }
                    })
                    .functions(new Function("tan", 1) {
                        @Override
                        public double apply(double... args) {
                            return Math.tan(Math.toRadians(args[0]));
                        }
                    })
                    .functions(new Function("log", 1) {
                        @Override
                        public double apply(double... args) {
                            // Validate input for log10
                            if (args[0] <= 0) {
                                throw new IllegalArgumentException("logarithm is undefined for non-positive values");
                            }
                            return Math.log10(args[0]);
                        }
                    })
                    .functions(new Function("ln", 1) {
                        @Override
                        public double apply(double... args) {
                            // Validate input for ln
                            if (args[0] <= 0) {
                                throw new IllegalArgumentException("logarithm is undefined for non-positive values");
                            }
                            return Math.log(args[0]);
                        }
                    })
                    .build();
            double result = exp.evaluate();

            primaryTextView.setText(formatResult(result));

        } catch (ArithmeticException e) {
            primaryTextView.setText("Overflow");
        } catch (IllegalArgumentException e) {
            primaryTextView.setText(e.getMessage());
        } catch (Exception e) {
            primaryTextView.setText("Error");
        }
    }

    private String formatResult(double result) {
        if (Math.abs(result) >= 1e6 || Math.abs(result) <= 1e-4) {
            return String.format("%e", result);
        }

        if (Math.floor(result) == result) {
            return String.format("%.0f", result);
        }

        return String.format("%.10f", result).replaceAll("\\.?0*$", "");
    }

    private double factorial(double number) {
        if (number < 0) {
            throw new IllegalArgumentException("Negative input not allowed for factorial");
        }
        if (number != Math.floor(number)) {
            throw new IllegalArgumentException("Factorial only supports whole numbers");
        }
        if (number == 0) {
            return 1;
        }
        double fact = 1;
        for (int i = 1; i <= number; i++) {
            fact *= i;
        }
        return fact;
    }
}
