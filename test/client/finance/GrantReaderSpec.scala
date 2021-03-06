package client.finance

import com.github.tototoshi.csv.CSVReader
import org.intracer.finance.Grant
import org.specs2.mutable.Specification

import scala.io.{Codec, Source}

class GrantReaderSpec extends Specification {

  def getResource(resource: String): Source = {
    val is = getClass.getResourceAsStream("/" + resource)
    Source.fromInputStream(is)(Codec.UTF8)
  }

  def resourceAsString(resource: String): String =
    getResource(resource).mkString


  def readCsv(file: String, grantId: Int): Seq[GrantItem] = {
    val reader = CSVReader.open(getResource(file))
    val lines = reader.all()
    reader.close()

    lines.map { l =>
      GrantItem(
        number = l.head,
        description = l(1),
        totalCost = BigDecimal(l(2)),
        grantId = Some(grantId)
      )
    }
  }

  "Grant reader" should {
    "read 2016 WMUA budget" in {
      val grantId = 13
      val grantName = "APG_2015-2016 round1_WMUA"
      val text = resourceAsString(grantName + ".wiki")
      val grant = Grant(Some(grantId), grantName)

      val items = GrantReader.parseBudget(grant, "Grants:" + grantName, text)
      items.nonEmpty === true
      items.flatMap(_.grantId).toSet === Set(grantId)
      items.size === 75

      val csv = readCsv(grantName + ".csv", grantId)
      items.map(_.copy(notes = None)) === csv
    }

    "read 2017 WMUA budget" in {
      val grantId = 15
      val grantName = "APG_2016-2017 round1_WMUA"
      val text = resourceAsString(grantName + ".wiki")
      val grant = Grant(Some(grantId), grantName)

      val items = GrantReader.parseBudget(grant, "Grants:" + grantName, text)
      items.nonEmpty === true
      items.flatMap(_.grantId).toSet === Set(grantId)
      items.size === 71

      val csv = readCsv(grantName + ".csv", grantId)
      items.map(_.copy(notes = None)) === csv
    }
  }
}
