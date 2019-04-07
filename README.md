# Jumper Game

This is an application for Programozási technológiák (programming technologies).<br/>
A game written in java where you jump with a rectangle.

For running you need to install [Apache Maven](https://maven.apache.org/download.cgi "Apache Maven Download Page") 3.6.0 or above.<br/>
This game requires [Java](https://openjdk.java.net/ "OpenJDK") 11 or above.

---
## Run:
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

## Running:
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