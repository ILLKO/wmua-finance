package org.intracer.finance

import client.finance.GrantItem
import org.intracer.finance.slick.Expenditures

import scala.collection.SortedSet

case class Dictionary(accountMap: Map[Int, Account] = Map.empty,
                      categoryMap: Map[Int, CategoryF] = Map.empty,
                      grantMap: Map[Int, Grant] = Map.empty,
                      grantItemMap: Map[Int, Seq[GrantItem]] = Map.empty,
                      projectMap: Map[Int, Project] = Map.empty,
                      userMap: Map[Int, User] = Map.empty) {
  
  val grantItemsById: Map[Int, GrantItem] = grantItemMap.values.flatten.groupBy(_.id.get).mapValues(_.head)

  def account(id: Int) = accountMap(id)
  def category(id: Int) = categoryMap(id)
  def grant(id: Int) = grantMap(id)
  def grantItem(id: Int) = grantItemsById(id)
  def project(id: Int) = projectMap(id)
  def user(id: Int) = userMap(id)

  def accountsJson: String = {
    accountMap.toSeq.sortBy(_._2.name.toLowerCase).map {
      case (id, account) => s"""{ value: "$id", text: "${account.name}"}"""
    }.mkString(", ")
  }

  def categoriesJson: String = {
    val elems = categoryMap.values.flatMap(_.name.split("/").headOption).toSeq
    val parents = SortedSet(elems: _*)

    parents.map { parent =>
      s"""{text: "$parent", children: [""" +
        categoryMap.toSeq
          .filter(_._2.name.toLowerCase.startsWith(parent.toLowerCase))
          .sortBy(_._2.name.toLowerCase)
          .map {
            case (id, cat) => s"""{ value: "$id", text: "${cat.name}"}"""
          }.mkString(", ") + "]}"
    }.mkString(", ")
  }

  def grantsJson: String = {
    grantMap.toSeq.sortBy(_._2.name.toLowerCase).map {
      case (id, grant) => s"""{ value: "$id", text: "${grant.name}"}"""
    }.mkString(", ")
  }

  def grantItemsJsonMap: Map[Int, String] = {
    grantItemMap.collect {
      case (grantId, items) if items.nonEmpty =>
        grantId -> grantItemsJson(grantId)
    }
  }

  def grantItemsJson(grantId: Int): String = {
    val programs = Seq(
      "Program 1: Outreach", "Program 2: Contests", "Program 3: Community Support", "Administrative costs"
    )
    (1 to 4).map { program =>
      s"""{text: "${programs(program - 1)}", children: [""" +
        grantItemMap
          .getOrElse(grantId, Seq.empty)
          .filter(_.number.startsWith(program.toString))
          .map {
            item => s"""{ value: "${item.id.get}", text: "${item.name}"}"""
          }.mkString(", ") + "]}"
    }.mkString(", ")
  }

  def projectsJson: String = {
    projectMap.toSeq.sortBy(_._2.name.toLowerCase).map {
      case (id, project) => s"""{ value: "$id", text: "${project.name}"}"""
    }.mkString(", ")
  }
}

