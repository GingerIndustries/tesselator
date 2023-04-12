package io.github.gingerindustries.tesselator

import java.awt.Color
import java.awt.Polygon
import java.awt.image.BufferedImage

class Tesselator(val inputImage: BufferedImage) {
    private fun heightForWidth(width: Int) =
            (width * (inputImage.height.toFloat() / inputImage.width.toFloat())).toInt()

    private fun downscaleInput(width: Int) =
            BufferedImage(width, heightForWidth(width), BufferedImage.TYPE_3BYTE_BGR).also {
                it.createGraphics()
                        .drawImage(
                                inputImage.getScaledInstance(
                                        width,
                                        -1,
                                        BufferedImage.SCALE_DEFAULT
                                ),
                                0,
                                0,
                                width,
                                heightForWidth(width),
                                null
                        )
            }

    private fun calculateHexagon(x: Int, y: Int) =
            Polygon().also { polygon ->
                (0..SIDES - 1)
                        .map { i ->
                            arrayOf(
                                            (x * RADIUS * Math.sqrt(0.75) * 2) +
                                                    (RADIUS * Math.sqrt(0.75) * (y % 2)) +
                                                    RADIUS * Math.sin((2 * Math.PI * i / SIDES)),
                                            (y * RADIUS * 1.5) +
                                                    RADIUS * Math.cos((2 * Math.PI * i / SIDES))
                                    )
                                    .map(Math::round)
                                    .map(Long::toInt)
                        }
                        .forEach { polygon.addPoint(it[0], it[1]) }
            }

    fun draw(width: Int): BufferedImage {
        val outputImage =
                BufferedImage(
                        (RADIUS * Math.sqrt(0.75) * 2 * (width - 0.5)).toInt(),
                        ((RADIUS * 1.5 * heightForWidth(width)) - (RADIUS * 1.5)).toInt(),
                        BufferedImage.TYPE_3BYTE_BGR
                )
        val graphics = outputImage.createGraphics()
        graphics.apply {
            clearRect(0, 0, outputImage.width, outputImage.height)
            downscaleInput(width).let {
                it.getRGB(0, 0, it.width, it.height, null, 0, it.width)
                        .toList()
                        .chunked(it.width)
                        .forEachIndexed { y, d ->
                            d.map(::Color).forEachIndexed { x, pixel ->
                                color = pixel
                                fillPolygon(calculateHexagon(x, y))
                            }
                        }
            }
        }
        graphics.dispose()
        return outputImage
    }

    companion object {
        const val RADIUS = 100
        const val SIDES = 6
    }
}