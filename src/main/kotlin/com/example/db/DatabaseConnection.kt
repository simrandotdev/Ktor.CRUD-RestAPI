package com.example.db

import org.ktorm.database.Database

object DatabaseConnection {
    val database = Database.connect(
        url = "jdbc:mysql://dfkpczjgmpvkugnb.cbetxkdyhwsb.us-east-1.rds.amazonaws.com:3306/y7u4pdrx9dhw5t3m",
        driver = "com.mysql.cj.jdbc.Driver",
        user = "srj5burxiy89ilin",
        password = "x7hedgmspaynthlp"
    )
}