package controllers

import org.intracer.finance.{Expenditure, User}
import org.intracer.finance.slick.{ExpenditureDao, UserDao}
import org.specs2.mock.Mockito
import play.api.libs.Codecs

trait DaoUtil extends Mockito {
  val defaultEmail = "dev@dot.com"
  val defaultPassword = "1234"
  val defaultUserId = 12
  val defaultUser = User(Some(defaultUserId), "Dev", defaultEmail, password = Some(defaultPassword))

  def mockUserDao(user: User = defaultUser): UserDao = {
    val userDao = mock[UserDao]

    userDao.count returns 1
    userDao.login(user.email, user.password.get) returns Some(withSha1(user))
    userDao.byEmail(user.email) returns Some(withSha1(user))
    userDao
  }

  def withSha1(user: User) = {
    user.copy(password = user.password.map(s => Codecs.sha1(s.getBytes)))
  }

  def mockExpenditureDao(list: Seq[Expenditure] = Nil): ExpenditureDao = {
    val expenditureDao = mock[ExpenditureDao]
    expenditureDao.list returns list
    expenditureDao
  }

}
