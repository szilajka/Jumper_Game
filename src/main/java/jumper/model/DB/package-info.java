/**
 * This package contains the classes those are used to represent the database tables.
 * <p>
 * It contains an {@link jumper.model.DB.AllTime}, a {@link jumper.model.DB.Score} and
 * a {@link jumper.model.DB.User} class.
 * The first two classes are linked to the {@code user} class.
 * The first class contains the time that the user spent in this game.
 * The {@code Score} class contains the score that the user achieved with some gameplay information.
 * The {@code User} clas contains the user's name, the hashed password and the salt, these are used
 * to authenticate and to search in the database.
 */
package jumper.model.DB;