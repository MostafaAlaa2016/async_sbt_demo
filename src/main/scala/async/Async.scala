package async

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Async {

  /**
    * Transforms a successful asynchronous `Int` computation
    * into a `Boolean` indicating whether the number was even or not.
    * In case the given `Future` value failed, this method
    * should return a failed `Future` with the same error.
    */
  def transformSuccess(eventuallyX: Future[Int]): Future[Boolean] =
    eventuallyX map (x => {
      if (x % 2 == 0) true else false
    })


  /**
    * Transforms a failed asynchronous `Int` computation into a
    * successful one returning `-1`.
    * Any non-fatal failure should be recovered.
    * In case the given `Future` value was successful, this method
    * should return a successful `Future` with the same value.
    */
  def recoverFailure(eventuallyX: Future[Int]): Future[Int] =
    eventuallyX.recover {
      case e: Throwable => print("we recovered")
        -1
    }

  /**
    * Perform two asynchronous computation, one after the other. `makeAsyncComputation2`
    * should start ''after'' the `Future` returned by `makeAsyncComputation1` has
    * completed.
    * In case the first asynchronous computation failed, the second one should not even
    * be started.
    * The returned `Future` value should contain the successful result of the first and
    * second asynchronous computations, paired together.
    */
  def sequenceComputations[A, B](
                                  makeAsyncComputation1: () => Future[A],
                                  makeAsyncComputation2: () => Future[B]
                                ): Future[(A, B)] =
    for {
      work1 <- makeAsyncComputation1()

      work2 <- makeAsyncComputation2()
    } yield (work1, work2)

  /**
    * Concurrently perform two asynchronous computations and pair their successful
    * result together.
    * The two computations should be started independently of each other.
    * If one of them fails, this method should return the failure.
    */
  def concurrentComputations[A, B](
                                    makeAsyncComputation1: () => Future[A],
                                    makeAsyncComputation2: () => Future[B]
                                  ): Future[(A, B)] = {
    val async1 = makeAsyncComputation1()
    val async2 = makeAsyncComputation2()
    for {
      s <- async1
      s2 <- async2
    } yield (s, s2)

  }


  /**
    * Attempt to perform an asynchronous computation.
    * In case of failure this method should try again to make
    * the asynchronous computation so that at most `maxAttempts`
    * are eventually performed.
    */
  def insist[A](makeAsyncComputation: () => Future[A], maxAttempts: Int): Future[A] = {
    if (maxAttempts > 0) {
      makeAsyncComputation().recoverWith {
        case e: Throwable => insist(makeAsyncComputation, maxAttempts - 1)
      }
    }
    else
      throw new IllegalArgumentException("arg 1 was wrong...");

  }
}
