package net.x123dev.ratclient

import java.awt.*
import java.awt.image.BufferedImage
import javax.swing.JPanel


class ScalablePane @JvmOverloads constructor(private val master: Image?, toFit: Boolean = true) : JPanel() {
    var isToFit = false
        set(value) {
            if (value != isToFit) {
                field = value
                invalidate()
            }
        }
    private var scaled: Image? = null

    init {
        isToFit = toFit
    }

    override fun getPreferredSize(): Dimension {
        return if (master == null) super.getPreferredSize() else Dimension(
            master.getWidth(this),
            master.getHeight(this)
        )
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        var toDraw: Image? = null
        if (scaled != null) {
            toDraw = scaled
        } else if (master != null) {
            toDraw = master
        }
        if (toDraw != null) {
            val x = (width - toDraw.getWidth(this)) / 2
            val y = (height - toDraw.getHeight(this)) / 2
            g.drawImage(toDraw, x, y, this)
        }
    }

    override fun invalidate() {
        generateScaledInstance()
        super.invalidate()
    }

    protected fun generateScaledInstance() {
        scaled = null
        scaled = if (isToFit) {
            getScaledInstanceToFit(master, size)
        } else {
            getScaledInstanceToFill(master, size)
        }
    }

    protected fun toBufferedImage(master: Image?): BufferedImage {
        val masterSize = Dimension(master!!.getWidth(this), master.getHeight(this))
        val image = createCompatibleImage(masterSize)
        val g2d = image.createGraphics()
        g2d.drawImage(master, 0, 0, this)
        g2d.dispose()
        return image
    }

    fun getScaledInstanceToFit(master: Image?, size: Dimension?): Image? {
        val masterSize = Dimension(master!!.getWidth(this), master.getHeight(this))
        return getScaledInstance(
            toBufferedImage(master),
            getScaleFactorToFit(masterSize, size),
            RenderingHints.VALUE_INTERPOLATION_BILINEAR,
            true
        )
    }

    fun getScaledInstanceToFill(master: Image?, size: Dimension): Image? {
        val masterSize = Dimension(master!!.getWidth(this), master.getHeight(this))
        return getScaledInstance(
            toBufferedImage(master),
            getScaleFactorToFill(masterSize, size),
            RenderingHints.VALUE_INTERPOLATION_BILINEAR,
            true
        )
    }

    fun getSizeToFit(original: Dimension?, toFit: Dimension?): Dimension {
        val factor = getScaleFactorToFit(original, toFit)
        val size = Dimension(original)
        size.width *= factor.toInt()
        size.height *= factor.toInt()
        return size
    }

    fun getSizeToFill(original: Dimension, toFit: Dimension): Dimension {
        val factor = getScaleFactorToFill(original, toFit)
        val size = Dimension(original)
        size.width *= factor.toInt()
        size.height *= factor.toInt()
        return size
    }

    fun getScaleFactor(iMasterSize: Int, iTargetSize: Int): Double {
        return iTargetSize.toDouble() / iMasterSize.toDouble()
    }

    fun getScaleFactorToFit(original: Dimension?, toFit: Dimension?): Double {
        var dScale = 1.0
        if (original != null && toFit != null) {
            val dScaleWidth = getScaleFactor(original.width, toFit.width)
            val dScaleHeight = getScaleFactor(original.height, toFit.height)
            dScale = Math.min(dScaleHeight, dScaleWidth)
        }
        return dScale
    }

    fun getScaleFactorToFill(masterSize: Dimension, targetSize: Dimension): Double {
        val dScaleWidth = getScaleFactor(masterSize.width, targetSize.width)
        val dScaleHeight = getScaleFactor(masterSize.height, targetSize.height)
        return Math.max(dScaleHeight, dScaleWidth)
    }

    fun createCompatibleImage(size: Dimension): BufferedImage {
        return createCompatibleImage(size.width, size.height)
    }

    fun createCompatibleImage(width: Int, height: Int): BufferedImage {
        var gc = graphicsConfiguration
        if (gc == null) {
            gc = GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.defaultConfiguration
        }
        val image = gc!!.createCompatibleImage(width, height, Transparency.TRANSLUCENT)
        image.coerceData(true)
        return image
    }

    protected fun getScaledInstance(
        img: BufferedImage,
        dScaleFactor: Double,
        hint: Any?,
        bHighQuality: Boolean
    ): BufferedImage? {
        var imgScale: BufferedImage? = img
        val iImageWidth = Math.round(img.width * dScaleFactor).toInt()
        val iImageHeight = Math.round(img.height * dScaleFactor).toInt()
        imgScale = if (dScaleFactor <= 1.0) {
            getScaledDownInstance(img, iImageWidth, iImageHeight, hint, bHighQuality)
        } else {
            getScaledUpInstance(img, iImageWidth, iImageHeight, hint, bHighQuality)
        }
        return imgScale
    }

    protected fun getScaledDownInstance(
        img: BufferedImage,
        targetWidth: Int,
        targetHeight: Int,
        hint: Any?,
        higherQuality: Boolean
    ): BufferedImage? {
        val type =
            if (img.transparency == Transparency.OPAQUE) BufferedImage.TYPE_INT_RGB else BufferedImage.TYPE_INT_ARGB
        var ret: BufferedImage? = img
        if (targetHeight > 0 || targetWidth > 0) {
            var w: Int
            var h: Int
            if (higherQuality) {
                // Use multi-step technique: start with original size, then
                // scale down in multiple passes with drawImage()
                // until the target size is reached
                w = img.width
                h = img.height
            } else {
                // Use one-step technique: scale directly from original
                // size to target size with a single drawImage() call
                w = targetWidth
                h = targetHeight
            }
            do {
                if (higherQuality && w > targetWidth) {
                    w /= 2
                    if (w < targetWidth) {
                        w = targetWidth
                    }
                }
                if (higherQuality && h > targetHeight) {
                    h /= 2
                    if (h < targetHeight) {
                        h = targetHeight
                    }
                }
                val tmp = BufferedImage(Math.max(w, 1), Math.max(h, 1), type)
                val g2 = tmp.createGraphics()
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint)
                g2.drawImage(ret, 0, 0, w, h, null)
                g2.dispose()
                ret = tmp
            } while (w != targetWidth || h != targetHeight)
        } else {
            ret = BufferedImage(1, 1, type)
        }
        return ret
    }

    protected fun getScaledUpInstance(
        img: BufferedImage,
        targetWidth: Int,
        targetHeight: Int,
        hint: Any?,
        higherQuality: Boolean
    ): BufferedImage? {
        val type = BufferedImage.TYPE_INT_ARGB
        var ret: BufferedImage? = img
        var w: Int
        var h: Int
        if (higherQuality) {
            // Use multi-step technique: start with original size, then
            // scale down in multiple passes with drawImage()
            // until the target size is reached
            w = img.width
            h = img.height
        } else {
            // Use one-step technique: scale directly from original
            // size to target size with a single drawImage() call
            w = targetWidth
            h = targetHeight
        }
        do {
            if (higherQuality && w < targetWidth) {
                w *= 2
                if (w > targetWidth) {
                    w = targetWidth
                }
            }
            if (higherQuality && h < targetHeight) {
                h *= 2
                if (h > targetHeight) {
                    h = targetHeight
                }
            }
            var tmp: BufferedImage? = BufferedImage(w, h, type)
            val g2 = tmp!!.createGraphics()
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint)
            g2.drawImage(ret, 0, 0, w, h, null)
            g2.dispose()
            ret = tmp
            tmp = null
        } while (w != targetWidth || h != targetHeight)
        return ret
    }
}