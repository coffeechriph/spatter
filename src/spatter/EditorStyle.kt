package spatter

import org.joml.Vector4f
import rain.api.gui.v2.*

lateinit var editorSkin: Skin

fun setupEditorStyle() {
    val editorPanelStyle = PanelStyle(
        true,
        1,
        Vector4f(rgb2float(236, 240, 241), 1.0f),
        Vector4f(rgb2float(189, 195, 199), 1.0f),
        Shape.RECT
    )

    val editorButtonStyle = ButtonStyle(
        1,
        Vector4f(rgb2float(236, 240, 241), 1.0f),
        Vector4f(rgb2float(149, 165, 166), 1.0f),
        Vector4f(rgb2float(189, 195, 199), 1.0f),
        Vector4f(rgb2float(189, 195, 199), 1.0f),
        Vector4f(rgb2float(52, 73, 94), 1.0f),
        TextAlign.CENTER,
        Shape.RECT
    )

    val editorLabelStyle = LabelStyle(
        0,
        Vector4f(rgb2float(236, 240, 241), 1.0f),
        Vector4f(rgb2float(149, 165, 166), 1.0f),
        Vector4f(rgb2float(189, 195, 199), 1.0f),
        Vector4f(rgb2float(189, 195, 199), 1.0f),
        Vector4f(rgb2float(52, 73, 94), 1.0f),
        TextAlign.CENTER
    )

    val editorTextfieldStyle = TextFieldStyle(
        1,
        Vector4f(rgb2float(236, 240, 241), 1.0f),
        Vector4f(rgb2float(149, 165, 166), 1.0f),
        Vector4f(rgb2float(189, 195, 199), 1.0f),
        Vector4f(rgb2float(189, 195, 199), 1.0f),
        Vector4f(rgb2float(52, 73, 94), 1.0f),
        TextAlign.LEFT,
        Shape.RECT
    )

    editorSkin = Skin(
        editorPanelStyle,
        editorButtonStyle,
        DEFAULT_SLIDER_STYLE,
        editorTextfieldStyle,
        editorLabelStyle
    )
}