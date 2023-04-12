package io.github.gingerindustries.tesselator

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import org.apache.commons.imaging.ImageFormats
import org.apache.commons.imaging.Imaging

class TesselatorCLI :
        CliktCommand(name = "tesselator", help = "Turn an image into a hexagonal tesselation.") {
    val input by argument().file(mustExist = true, canBeDir = false, mustBeReadable = true)
    val output by argument().file(mustExist = false, canBeDir = false, mustBeWritable = false)
    val width by
            option("-w", "--width", help = "The width of the image in hexagons").int().default(50)

    override fun run() {
        echo("Tesselating ${input.path}...")
        Imaging.writeImage(
                Tesselator(Imaging.getBufferedImage(input)).draw(width),
                output,
                ImageFormats.PNG
        )
        echo("Finished! Output saved to ${output.path}.")
    }
}

fun main(args: Array<String>) = TesselatorCLI().main(args)
