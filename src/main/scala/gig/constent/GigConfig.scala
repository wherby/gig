package gig.constent

import java.io.File
import com.typesafe.config.{Config, ConfigFactory}

/**
  * For gig.constent in gig
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2018/8/20
  */
object GigConfig {
  var _gigConfig: Option[Config] = None

  def load(): Config = {
    _gigConfig match {
      case None =>
        val gigConfigLocate: String = scala.util.Properties.envOrElse("gigconfig", "gig1.conf")
        _gigConfig = Some(ConfigFactory.parseFile(new File(gigConfigLocate))
          .withFallback(ConfigFactory.load()).resolve())
        _gigConfig.get
      case _ => _gigConfig.get
    }
  }
}
