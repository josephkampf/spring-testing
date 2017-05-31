import groovy.sql.*


def sql = Sql.newInstance('jdbc:mysql://mysql:3306/spring_testing', 'root', 'password', 'com.mysql.jdbc.Driver')  
    sql.eachRow('show tables'){ row ->  
        println row[0]  
    }  

sql.close()
