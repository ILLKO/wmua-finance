package org.intracer.finance.slick

import org.intracer.finance.Grant
import slick.driver.H2Driver.api._

class Grants(tag: Tag) extends Table[Grant](tag, "grant_list") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def url = column[Option[String]]("url")

  def nameIndex = index("grant_name", name, unique = true)

  def * = (id.?, name, url) <> (Grant.tupled, Grant.unapply)

}
