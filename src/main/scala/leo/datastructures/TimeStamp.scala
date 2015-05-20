package leo.datastructures

/**
 * A datatype for time instants that are totally comparable.
 *
 * @since 19.05.2015
 */
sealed trait TimeStamp extends Comparable[TimeStamp] with Pretty

object TimeStamp extends Function0[TimeStamp] {
  import java.util.concurrent.atomic.AtomicLong

  /**
   * Gives the current `TimeStamp`, i.e. a timestamp that is stricly smaller
   * than any timestamp retrieved before this call.
   * @return Current `TimeStamp`
   */
  def apply(): TimeStamp = new TimeStampImpl(timeStampCounter.incrementAndGet())


  /// Local implementation
  private val timeStampCounter : AtomicLong = new AtomicLong(0)

  private case class TimeStampImpl(time: Long) extends TimeStamp {
    def compareTo(o: TimeStamp) = o match {
      case TimeStampImpl(oTime) => time.compareTo(oTime)
    }

    def pretty = s"$time"
  }
}