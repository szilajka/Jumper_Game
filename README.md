# Jumper Game

A game written by Szilárd Németi.<br/>
This is an application for Programming Technologies (Programozási technológiák).<br/>

# Requirements

For running you need to install [Apache Maven](https://maven.apache.org/download.cgi "Apache Maven Download Page") 3.6.0 or above.<br/>
This game requires [Java](https://openjdk.java.net/ "OpenJDK") 11 or above.

#_**IMPORTANT NOTE!**_
**DO NOT SHARE THE OJDBC DRIVER!**<br/>
**DUE TO ORACLE LICENSE, IT CANNOT BE SHARED, SO ONLY INSTALL IT TO YOUR COMPUTER AND NOT IN OTHER COMPUTERS AND NEVER EVER UPLOAD IT TO ANYWHERE!**


---
## Preparations
-    This game uses a database to store the user's score and datas.
    - If you want to change the db, edit the `jumper.modl.DB` package's files (if needed) and set the correct `persistence.xml` file in the `src/main/resources/META-INF` directory.
		1. Before you run this game, you need to download the _**[Oracle JDBC Driver](https://www.oracle.com/technetwork/database/application-development/jdbc/downloads/index.html "Oracle JDBC download page")**_.
		2. After you downloaded it, you need to install it to your locale maven repository, as you see it:
		```
		mvn install:install-file -Dfile=<path-to-ojdbc8.jar> -DgroupId=com.oracle.jdbc \
		-DartifactId=ojdbc8 -Dversion=18.3.0.0 -Dpackaging=jar
		```
		- If you want to change the version number, ie. to `12.3.0.0`, then write it to the `-Dversion` and change it in the `pom.xml` too.
---
## Run
-    To generate jar packages, run the following commands:
>
```
mvn clean package
java -jar target/jumper_game_{version}-jar-with-dependencies.jar
```

## Generating javadoc
-    To generate reports and javadoc, use the following commands: 
>
```
mvn clean site
```

## Running
-    To run this game with maven, run the following commands: 
>
```
mvn clean compile exec:java
```

---
## Gameplay
It is a 2D platformer game, where the user has to reach the end of each level.

----
### There are two main character types:
-    Player
-    Falling enemies
----

### The player:
-    The Player only has 3 lives.
-    With every hit the user's Y velocity decreases.
-    If the player's Y velocity will be lower than a given amount, then the game ends.
-    If the player's life is zero, the game ends.

----
### The enemy has two type:
-    **Basic**
    - If it hits the user, only the user's Y velocity will decrease.
-    **Spike**
    - This enemy falls faster than the basic enemy.
        If it hits the user, the user's remaining lives will decrease and the Y velocity decreases too.
    - If the user is in the enemy's range, the enemy will stop and the player can stand on it.
