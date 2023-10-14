package com.example.calculadora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculadora.ui.theme.CalculadoraTheme
import java.text.DecimalFormat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculadoraTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Calculadora()
                }
            }
        }
    }
}

val rows = listOf(
    listOf(
        ButtonSpec(text = " C ", color = ColoresBotones.COLOR_GENERAL),
        ButtonSpec(text = "/", color = ColoresBotones.VARIANTE1_COLOR),
        ButtonSpec(text = "%", color = ColoresBotones.VARIANTE1_COLOR),
        ButtonSpec(text = "⌫ ", color = ColoresBotones.VARIANTE1_COLOR),
    ),
    listOf(
        ButtonSpec(text = "7", color = ColoresBotones.COLOR_GENERAL),
        ButtonSpec(text = "8", color = ColoresBotones.COLOR_GENERAL),
        ButtonSpec(text = "9", color = ColoresBotones.COLOR_GENERAL),
        ButtonSpec(text = "X", color = ColoresBotones.VARIANTE1_COLOR),
    ),
    listOf(
        ButtonSpec(text = "4", color = ColoresBotones.COLOR_GENERAL),
        ButtonSpec(text = "5", color = ColoresBotones.COLOR_GENERAL),
        ButtonSpec(text = "6", color = ColoresBotones.COLOR_GENERAL),
        ButtonSpec(text = "-", color = ColoresBotones.VARIANTE1_COLOR),
    ),
    listOf(
        ButtonSpec(text = "1", color = ColoresBotones.COLOR_GENERAL),
        ButtonSpec(text = "2", color = ColoresBotones.COLOR_GENERAL),
        ButtonSpec(text = "3", color = ColoresBotones.COLOR_GENERAL),
        ButtonSpec(text = "+", color = ColoresBotones.VARIANTE1_COLOR),
    ),
    listOf(
        ButtonSpec(text = "+/-", color = ColoresBotones.COLOR_GENERAL),
        ButtonSpec(text = "0", color = ColoresBotones.COLOR_GENERAL),
        ButtonSpec(text = ".", color = ColoresBotones.COLOR_GENERAL),
        ButtonSpec(text = "=", color = ColoresBotones.VARIANTE2_COLOR),
    ),
)

data class ButtonSpec(
    val text: String,
    val color: ColoresBotones
)

enum class ColoresBotones {
    COLOR_GENERAL,
    VARIANTE1_COLOR,
    VARIANTE2_COLOR
}

private const val MAX_DIGITS = 10


@Preview(
    showSystemUi = true
)
@Composable
fun Calculadora() {
    // Inicializar las variables de estado con remember
    val valorActual = remember { mutableStateOf("0") }
    val expresion = remember { mutableStateOf<String>("") }
    val operacionActual = remember { mutableStateOf("") }

    val operando1 = remember {
        mutableStateOf(0.0)
    }

    val operando2 = remember {
        mutableStateOf(0.0)
    }

    val operadorActual = remember { mutableStateOf<String?>(null) }
    val valorAcumulado = remember { mutableStateOf(0.0) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        PanEntrada(valorActual)
        D_Principal()
        C_Grid(
            valorActual,
            expresion,
            operacionActual,
            operando1,
            operando2,
            operadorActual,
            valorAcumulado
        )
    }
}

@Composable
fun PanEntrada(valorActual: MutableState<String>) {
    Text(
        text = valorActual.value,
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        textAlign = TextAlign.End,
        fontSize = 56.sp
    )
}

@Composable
fun BotonCalculadora(
    buttonSpec: ButtonSpec,
    expresion: MutableState<String>,
    valorActual: MutableState<String>,
    operacionActual: MutableState<String>,
    operando1: MutableState<Double>,
    operando2: MutableState<Double>,
    operadorActual: MutableState<String?>,
    valorAcumulado: MutableState<Double>
) {
    val decimalFormat = DecimalFormat("#.###")

    OutlinedButton(
        onClick = {
            when (buttonSpec.text) {
                " C " -> {
                    valorActual.value = "0"
                    operacionActual.value = ""
                    operadorActual.value = null
                    valorAcumulado.value = 0.0
                    expresion.value = ""
                }

                "⌫ " -> {
                    if (operacionActual.value == "=") {
                        return@OutlinedButton
                    }
                    valorActual.value =
                        if (valorActual.value.length > 1) valorActual.value.dropLast(1) else "0"
                    expresion.value =
                        if (expresion.value.isNotEmpty()) expresion.value.dropLast(1) else ""
                }

                "%" -> {
                    if (operadorActual.value != null) {
                        operando2.value = decimalFormat.parse(valorActual.value)?.toDouble() ?: 0.0
                        valorAcumulado.value = when (operadorActual.value) {
                            "+" -> operando1.value + (operando1.value * (operando2.value / 100))
                            "-" -> operando1.value - (operando1.value * (operando2.value / 100))
                            "X" -> operando1.value * (operando2.value / 100)
                            "/" -> operando1.value / (operando2.value / 100)
                            else -> valorAcumulado.value
                        }
                        valorActual.value = decimalFormat.format(valorAcumulado.value)
                        operando1.value = valorAcumulado.value
                        operacionActual.value = ""
                        operadorActual.value = null
                        expresion.value = ""
                    }
                }

                "+/-" -> {
                    if (valorActual.value != "0") {
                        valorActual.value = if (valorActual.value.startsWith("-")) {
                            valorActual.value.removePrefix("-")
                        } else {
                            "-${valorActual.value}"
                        }
                    }
                }

                "=" -> {
                    if (operadorActual.value != null) {
                        operando2.value = decimalFormat.parse(valorActual.value)?.toDouble() ?: 0.0
                        if (operando2.value == 0.0 && operadorActual.value == "/") {
                            valorActual.value = "Syntax.Error"
                        } else {
                            valorAcumulado.value = when (operadorActual.value) {
                                "+" -> operando1.value + operando2.value
                                "-" -> operando1.value - operando2.value
                                "X" -> operando1.value * operando2.value
                                "/" -> operando1.value / operando2.value
                                else -> valorAcumulado.value
                            }
                            valorActual.value = decimalFormat.format(valorAcumulado.value)
                            operando1.value = valorAcumulado.value
                            operacionActual.value = ""
                            operadorActual.value = null
                            expresion.value = ""
                        }
                    }
                }

                else -> {
                    if (buttonSpec.text.matches(Regex("[0-9.]"))) {
                        if (operacionActual.value == "=") {
                            operando1.value =
                                decimalFormat.parse(valorActual.value)?.toDouble() ?: 0.0
                            valorActual.value = buttonSpec.text
                            operacionActual.value = ""
                            expresion.value = operando1.value.toString()
                        } else {
                            if (buttonSpec.text == "." && valorActual.value.contains(".")) {
                                return@OutlinedButton
                            }

                            if (valorActual.value == "0") {
                                valorActual.value = buttonSpec.text
                            } else if (valorActual.value.length < MAX_DIGITS) {
                                valorActual.value += buttonSpec.text
                            }
                            expresion.value += buttonSpec.text
                        }
                    } else {
                        if (operadorActual.value != null) {
                            operando2.value =
                                decimalFormat.parse(valorActual.value)?.toDouble() ?: 0.0
                            valorAcumulado.value = when (operadorActual.value) {
                                "+" -> operando1.value + operando2.value
                                "-" -> operando1.value - operando2.value
                                "X" -> operando1.value * operando2.value
                                "/" -> operando1.value / operando2.value
                                else -> valorAcumulado.value
                            }
                            valorActual.value = decimalFormat.format(valorAcumulado.value)
                            operando1.value = valorAcumulado.value
                        }
                        operadorActual.value = buttonSpec.text
                        operacionActual.value = "="
                        expresion.value += " ${buttonSpec.text} "
                    }
                }
            }
        },
        shape = CircleShape,
        border = BorderStroke(1.dp, Color.Transparent),
        contentPadding = PaddingValues(20.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = buttonSpec.getBackgroundColor()
        )
    ) {
        Text(
            text = buttonSpec.text,
            color = buttonSpec.getTextColor(),
            fontSize = 22.sp
        )
    }
}


@Composable
fun FilaCalculadora(
    values: List<ButtonSpec>,
    expresion: MutableState<String>,
    valorActual: MutableState<String>,
    operacionActual: MutableState<String>,
    operando1: MutableState<Double>,
    operando2: MutableState<Double>,
    operadorActual: MutableState<String?>,
    valorAcumulado: MutableState<Double>
) {
    Row(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        values.map { ButtonSpec ->
            BotonCalculadora(
                ButtonSpec,
                expresion,
                valorActual,
                operacionActual,
                operando1,
                operando2,
                operadorActual,
                valorAcumulado
            )
        }
    }
}

@Composable
fun C_Grid(
    valorActual: MutableState<String>,
    expresion: MutableState<String>,
    operacionActual: MutableState<String>,
    operando1: MutableState<Double>,
    operando2: MutableState<Double>,
    operadorActual: MutableState<String?>,
    valorAcumulado: MutableState<Double>
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        rows.map { ButtonSpec ->
            FilaCalculadora(
                ButtonSpec,
                expresion,
                valorActual,
                operacionActual,
                operando1,
                operando2,
                operadorActual,
                valorAcumulado
            )
        }
    }
}

@Composable
fun D_Principal() {
    Divider(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        color = Color.Black,
        thickness = 0.4.dp
    )
}

private fun ButtonSpec.getBackgroundColor(): Color {
    return when (color) {
        ColoresBotones.COLOR_GENERAL,
        ColoresBotones.VARIANTE1_COLOR -> {
            Color.hsl(0.88f, 0.4f, 0.9f)
        }

        ColoresBotones.VARIANTE2_COLOR -> Color(0xff18678d)
    }
}

private fun ButtonSpec.getTextColor(): Color {
    return when (color) {
        ColoresBotones.COLOR_GENERAL -> {
            Color.hsl(0.99f, 0.29f, 0.47f)
        }

        ColoresBotones.VARIANTE1_COLOR -> Color(0xff18678d)
        ColoresBotones.VARIANTE2_COLOR -> Color.White
    }
}