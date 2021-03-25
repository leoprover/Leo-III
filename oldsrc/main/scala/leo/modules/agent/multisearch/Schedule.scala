package leo.modules.agent.multisearch

import leo.modules.prover.RunStrategy

/**
  * Capsules a scheduling of run strategies for parallel execution.
  */
trait Schedule {
  /**
    * @return True, iff there exist new schedules
    */
  def hasNext : Boolean

  /**
    *
    * Given the remaining time and an amount of
    * strategies to schedule, this method returns
    * up to `amount` strategies paried with an individual timeout.
    *
    * @param remainingTime the time remaining to reach the timeout
    * @param amount the number of schedules started in parallel
    * @return up to `amount` strategies with their choosen timeouts
    */
  def next(remainingTime : Int, amount : Int) : Seq[(RunStrategy, Int)]
}

/**
  * Schedules a set of strategies parallel,
  * with the conditions, that all momentarily choosen strategies run all the same amount of time.
  *
  * @param allStrategies All strategies that could be scheduled
  */
class EquiScheduleImpl(allStrategies : Seq[RunStrategy]) extends Schedule {

  private var remainingStrats = allStrategies
  private val MIN_TIME : Int = 60

  private var aloneQueue : Seq[RunStrategy] = Seq()

  override def hasNext: Boolean = synchronized(remainingStrats.nonEmpty || aloneQueue.nonEmpty)

  override def next(remainingTime: Int, amount: Int): Seq[(RunStrategy, Int)] = synchronized{
    if(aloneQueue.nonEmpty) {
      val h = aloneQueue.head
      aloneQueue = aloneQueue.tail
      val remainingWeight: Float = remainingStrats.foldLeft(0f)((w, s) => w + s.share)+
        aloneQueue.foldLeft(0f)((w,s) => w + s.share)
      val time = Math.max((MIN_TIME * h.share).toInt, ((remainingTime * h.share) / (h.share + remainingWeight)).toInt)
      Seq((h, time))
    } else {
      var take = Seq[RunStrategy]()
      while(remainingStrats.nonEmpty && take.size < amount){
        val n = remainingStrats.head
        remainingStrats = remainingStrats.tail
        if(n.runStandandalone) aloneQueue = n +: aloneQueue
        else take = n +: take
      }

      val remainingWeight: Float = remainingStrats.foldLeft(0: Float)((w, s) => w + s.share)+
        aloneQueue.foldLeft(0f)((w,s) => w + s.share)
      val sumChoosen: Float = take.foldLeft(0: Float)((w, s) => w + s.share)
      val maxChoosen : Float = take.maxBy(r => r.share).share
      val time = Math.max((MIN_TIME * maxChoosen).toInt, ((remainingTime * sumChoosen) / (sumChoosen + remainingWeight)).toInt)
      take map (s => (s, time))
    }
  }
}

/**
  * Schedules a set of strategies parallel,
  * here every strategies obtains its own timeout
  *
  * @param allStrategies All strategies, that could be scheduled
  */
class IndividualScheduleImpl(allStrategies : Seq[RunStrategy]) extends Schedule {

  private var remainingStrats = allStrategies
  private val MIN_TIME : Int = 10

  override def hasNext: Boolean = synchronized(remainingStrats.nonEmpty)

  override def next(remainingTime: Int, amount: Int): Seq[(RunStrategy, Int)] = synchronized{
    val consider = remainingStrats.take(amount)
    remainingStrats = remainingStrats.drop(amount)

    // TODO Grouped by the amount taken (mean of weight)??? Otherwise the first processes always get lower results
    val remainingWeight : Float = remainingStrats.foldLeft(0 : Float)((w, s) => w+s.share) / amount
    consider map {s =>
      val time = Math.max(MIN_TIME, ((remainingTime * s.share) / (s.share + remainingWeight)).toInt)
      (s, time)}
  }
}
