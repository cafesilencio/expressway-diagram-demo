import com.mapbox.geojson.Point
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin

object QuadkeyUtil {

    private const val DEG_TO_RAD = Math.PI / 180.0
    private const val MAX_ZOOM = 25.0

    fun getQuadkey(point: Point, zoom: Int): String {
        val tileId = getTileId(point, zoom)
        return quadKeyFor(tileId)
    }

    private fun getTileId(point: Point, zoom: Int): TileID {
        val zoomTarget = clamp(zoom.toDouble(), 0.0, MAX_ZOOM)
        val n = 2.0.pow(zoomTarget)
        val sin = sin(point.latitude() * DEG_TO_RAD)
        var x = n * (point.longitude() / 360.0 + 0.5)
        var y = n * (0.5 - 0.25 * ln((1 + sin) / (1 - sin)) / Math.PI)
        x %= n
        if (x < 0) {
            x += n
        }
        x = clamp(floor(x), 0.0, Double.MAX_VALUE)
        y = clamp(floor(y), 0.0, Double.MAX_VALUE)
        return TileID(x = x.toLong(), y = y.toLong(), zoom = zoomTarget.toLong())
    }

    private fun quadKeyFor(tileID: TileID): String {
        val sb = StringBuilder()
        if (tileID.zoom == 0L) {
            return sb.toString()
        }
        val x = clamp(tileID.x.toDouble(), 0.0, Double.MAX_VALUE).toLong()
        val y = clamp(tileID.y.toDouble(), 0.0, Double.MAX_VALUE).toLong()
        val z = clamp(tileID.zoom.toDouble(), 1.0, Double.MAX_VALUE).toInt()
        for (i in z downTo 1) {
            var digit = '0'.digitToInt()
            val mask = 1L shl (i - 1)
            if ((x and mask) != 0L) {
                digit++
            }
            if ((y and mask) != 0L) {
                digit += 2
            }
            sb.append(digit.toString(10))
        }
        return sb.toString()
    }

    private fun clamp(value: Double, lower: Double, upper: Double): Double {
        return max(lower, min(upper, value))
    }

    private data class TileID(val x: Long, val y: Long, val zoom: Long)
}