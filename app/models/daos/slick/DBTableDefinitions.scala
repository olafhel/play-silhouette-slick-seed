package models.daos.slick

import scala.slick.driver.MySQLDriver.simple._
import play.Logger
import scala.slick.jdbc.meta.MTable

object DBTableDefinitions {

  case class DBUser (
    userID: String,
    firstName: Option[String],
    lastName: Option[String],
    fullName: Option[String],
    email: Option[String],
    avatarURL: Option[String]
  )

  class Users(tag: Tag) extends Table[DBUser](tag, "user") {
    def id = column[String]("userID", O.PrimaryKey)
    def firstName = column[Option[String]]("firstName")
    def lastName = column[Option[String]]("lastName")
    def fullName = column[Option[String]]("fullName")
    def email = column[Option[String]]("email")
    def avatarURL = column[Option[String]]("avatarURL")
    def * = (id, firstName, lastName, fullName, email, avatarURL) <> (DBUser.tupled, DBUser.unapply _)
  }
  
  case class DBLoginInfo (
    id: Option[Long],
    providerID: String,
    providerKey: String
  )
  
  class LoginInfos(tag: Tag) extends Table[DBLoginInfo](tag, "logininfo") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def providerID = column[String]("providerID")
    def providerKey = column[String]("providerKey")
    def * = (id.?, providerID, providerKey) <> (DBLoginInfo.tupled, DBLoginInfo.unapply _)
  }
  
  case class DBUserLoginInfo (
    userID: String,
    loginInfoId: Long
  )
  
  class UserLoginInfos(tag: Tag) extends Table[DBUserLoginInfo](tag, "userlogininfo") {
    def userID = column[String]("userID", O.NotNull)
    def loginInfoId = column[Long]("loginInfoId", O.NotNull)
    def * = (userID, loginInfoId) <> (DBUserLoginInfo.tupled, DBUserLoginInfo.unapply _)
  }
  
  case class DBPasswordInfo (
    hasher: String,
    password: String,
    salt: Option[String],
    loginInfoId: Long
  )
  
  class PasswordInfos(tag: Tag) extends Table[DBPasswordInfo](tag, "passwordinfo") {
    def hasher = column[String]("hasher")
    def password = column[String]("password")
    def salt = column[Option[String]]("salt")
    def loginInfoId = column[Long]("loginInfoId")
    def * = (hasher, password, salt, loginInfoId) <> (DBPasswordInfo.tupled, DBPasswordInfo.unapply _)
  }
  
  val slickUsers = TableQuery[Users]
  val slickLoginInfos = TableQuery[LoginInfos]
  val slickUserLoginInfos = TableQuery[UserLoginInfos]
  val slickPasswordInfos = TableQuery[PasswordInfos]
  
  val db = Database.forConfig("db.default")
  
  def createTable(table: TableQuery[_ <: Table[_]]) {
    db withSession { implicit session => 
      try {
        Logger.debug("Attempting to create table $table ...")
        table.ddl.create
        Logger.debug("... done.")
      } catch {
        case _ => Logger.debug("Could not create schema for table `" + table.baseTableRow.tableName + "`. Maybe it already exists?")
      }
    }
  }
  
  createTable(slickUsers)
  createTable(slickLoginInfos)
  createTable(slickUserLoginInfos)
  createTable(slickPasswordInfos)
}
