

trait  MyApp{
  println("I'm my app")
  def main(args: Array[String]) {
    args.foreach(x =>    println(s"${System.currentTimeMillis()},$x"))
  }
}
/**
  * For gig in gig
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2018/8/7
  */
object MyTest extends MyApp{
  println("I'm Test")
  MyTest.main(Array("hello","world"))
  println(s"${System.currentTimeMillis()},in My Test")

}
