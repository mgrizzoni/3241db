package osu.cse3241;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * <h1>CSE3241 Introduction to Database Systems - Sample Java application.</h1>
 * 
 * <p>Sample app to be used as guidance and a foundation for students of 
 * CSE3241 Introduction to Database Systems at 
 * The Ohio State University.</p>
 * 
 * <h2>!!! - Vulnerable to SQL injection - !!!</h2>
 * <p>Correct the code so that it is not vulnerable to a SQL injection attack. ("Parameter substitution" is the usual way to do this.)</p>
 * 
 * <p>Class is written in Java SE 8 and in a procedural style. Implement a constructor if you build this app out in OOP style.</p>
 * <p>Modify and extend this app as necessary for your project.</p>
 *
 * <h2>Language Documentation:</h2>
 * <ul>
 * <li><a href="https://docs.oracle.com/javase/8/docs/">Java SE 8</a></li>
 * <li><a href="https://docs.oracle.com/javase/8/docs/api/">Java SE 8 API</a></li>
 * <li><a href="https://docs.oracle.com/javase/8/docs/technotes/guides/jdbc/">Java JDBC API</a></li>
 * <li><a href="https://www.sqlite.org/docs.html">SQLite</a></li>
 * <li><a href="http://www.sqlitetutorial.net/sqlite-java/">SQLite Java Tutorial</a></li>
 * </ul>
 *
 * <h2>MIT License</h2>
 *
 * <em>Copyright (c) 2019 Leon J. Madrid, Jeff Hachtel</em>
 * 
 * <p>Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.</p>
 *
 * 
 * @author Leon J. Madrid (madrid.1), Jeff Hachtel (hachtel.5)
 * 
 */

public class CSE3241app {
    
	/**
	 *  The database file name.
	 *  
	 *  Make sure the database file is in the root folder of the project if you only provide the name and extension.
	 *  
	 *  Otherwise, you will need to provide an absolute path from your C: drive or a relative path from the folder this class is in.
	 */
	private static String DATABASE = "3241DB.db";
	
	/**
	 *  The query statement to be executed.
	 *  
	 *  Remember to include the semicolon at the end of the statement string.
	 *  (Not all programming languages and/or packages require the semicolon (e.g., Python's SQLite3 library))
	 */
	private static String sqlStatement = "SELECT * FROM PERSON;";
	
    /**
     * Connects to the database if it exists, creates it if it does not, and returns the connection object.
     * 
     * @param databaseFileName the database file name
     * @return a connection object to the designated database
     */
    public static Connection initializeDB(String databaseFileName) {
    	/**
    	 * The "Connection String" or "Connection URL".
    	 * 
    	 * "jdbc:sqlite:" is the "subprotocol".
    	 * (If this were a SQL Server database it would be "jdbc:sqlserver:".)
    	 */
        String url = "jdbc:sqlite:" + databaseFileName;
        Connection conn = null; // If you create this variable inside the Try block it will be out of scope
        try {
            conn = DriverManager.getConnection(url);
            if (conn != null) {
            	// Provides some positive assurance the connection and/or creation was successful.
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("The connection to the database was successful.");
            } else {
            	// Provides some feedback in case the connection failed but did not throw an exception.
            	System.out.println("Null Connection");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("There was a problem connecting to the database.");
        }
        return conn;
    }
    
    /**
     * Queries the database and prints the results.
     * 
     * @param conn a connection object
     * @param sql a SQL statement that returns rows
     */
    public static void sqlQuery(Connection conn, String sql){
        try {
        	Statement stmt = conn.createStatement();
        	ResultSet rs = stmt.executeQuery(sql);
        	ResultSetMetaData rsmd = rs.getMetaData();
        	int columnCount = rsmd.getColumnCount();
        	for (int i = 1; i <= columnCount; i++) {
        		String value = rsmd.getColumnName(i);
        		System.out.print(value);
        		if (i < columnCount) System.out.print(",  ");
        	}
			System.out.print("\n");
        	while (rs.next()) {
        		for (int i = 1; i <= columnCount; i++) {
        			String columnValue = rs.getString(i);
            		System.out.print(columnValue);
            		if (i < columnCount) System.out.print(",  ");
        		}
    			System.out.print("\n");
        	}
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * Searches for all tracks by the given artist before the given date.
     * 
     * @param conn
     * 	the database connection
     * @param artist
     * 	the artist to search
     * @param year
     * 	the date object to limit the search
     */
    public static void selectTracksBeforeYear(Connection conn, int artist, String year) {
    	String query = "SELECT Title FROM Content, Owns, Contains WHERE ReleaseDate < ? "
				+ "AND OriginalAlbumID = AlbumID AND CompilerID = ? AND TrackID = ContentID "
				+ "AND Kind = \'Track\'";
		PreparedStatement stmt = null;
    	ResultSet rs = null;
    	try {
    		stmt = conn.prepareStatement(query);
    		stmt.setInt(2, artist);
    		stmt.setString(1, year);
    		rs = stmt.executeQuery();
    		System.out.println("All titles by artist #" + artist + " before " + year + ":");
    		while(rs.next()) {
    			String title = rs.getString("Title");
    			System.out.println(title);
    		}
    	} catch(SQLException e) {
    		System.out.println(e.getMessage());
    	} finally {
    		try { if (rs != null) rs.close(); } catch (Exception e) {};
    	    try { if (stmt != null) stmt.close(); } catch (Exception e) {};
    	    try { if (conn != null) conn.close(); } catch (Exception e) {};
    	}
    }
    
    public static void selectAlbumsUnderCopies(Connection conn, int count) {
    	String query = "SELECT Title, ContentID FROM Content WHERE NumCopies < ? "
    			+ "AND Kind = \'Album\'";
		PreparedStatement stmt = null;
    	ResultSet rs = null;
    	try {
    		stmt = conn.prepareStatement(query);
    		stmt.setInt(1, count);
    		rs = stmt.executeQuery();
    		System.out.println("All albums with fewer than " + count + " copies:");
    		while(rs.next()) {
    			String title = rs.getString("Title");
    			int id = rs.getInt("ContentID");
    			System.out.println(id + ": " + title);
    		}
    	} catch(SQLException e) {
    		System.out.println(e.getMessage());
    	} finally {
    		try { if (rs != null) rs.close(); } catch (Exception e) {};
    	    try { if (stmt != null) stmt.close(); } catch (Exception e) {};
    	    try { if (conn != null) conn.close(); } catch (Exception e) {};
    	}
    }
    
    public static void countAlbumsCheckedOut(Connection conn, int guestID) {
    	String query = "SELECT COUNT(Title) AS total FROM Content, ChecksOut WHERE "
    			+ "Kind = \'Album\' AND GuestID = ? AND ContentID = CheckedOutID";
		PreparedStatement stmt = null;
    	ResultSet rs = null;
    	try {
    		stmt = conn.prepareStatement(query);
    		stmt.setInt(1, guestID);
    		rs = stmt.executeQuery();
    		System.out.println("Number of albums checked out by patron " + guestID + ":");
    		while(rs.next()) {
    			String total = rs.getString("Total");
    			System.out.println(total);
    		}
    	} catch(SQLException e) {
    		System.out.println(e.getMessage());
    	} finally {
    		try { if (rs != null) rs.close(); } catch (Exception e) {};
    	    try { if (stmt != null) stmt.close(); } catch (Exception e) {};
    	    try { if (conn != null) conn.close(); } catch (Exception e) {};
    	}
    }
    
    public static void insertAudiobook(Connection conn, int contentID, Date releaseDate, String title, String type, int numCopies, String genre, int chapterCount, String isbn) {
    	String query = "INSERT INTO Content (ContentID, CheckedOut, ReleaseDate, "
    			+ "Title, Kind, Type, NumCopies, Genre, ChapterCount, ISBN) "
    			+ "VALUES (?, 0, ?, ?, \'Audiobook\', ?, ?, ?, ?, ?)";
		PreparedStatement stmt = null;
    	try {
    		stmt = conn.prepareStatement(query);
    		stmt.setInt(1, contentID);
    		stmt.setDate(2, releaseDate);
    		stmt.setString(3, title);
    		stmt.setString(4, type);
    		stmt.setInt(5, numCopies);
    		stmt.setString(6, genre);
    		stmt.setInt(7, chapterCount);
    		stmt.setString(8, isbn);
    		stmt.execute();
    		System.out.println("Successfully added to database.");
    	} catch(SQLException e) {
    		System.out.println(e.getMessage());
    	} finally {
    	    try { if (stmt != null) stmt.close(); } catch (Exception e) {};
    	    try { if (conn != null) conn.close(); } catch (Exception e) {};
    	}
    }
    
    public static void getMoviesCheckedOut(Connection conn, int guestID) {
    	String query = "SELECT Title FROM Content, ChecksOut WHERE "
    			+ "Kind = \'Movie\' AND GuestID = ? AND ContentID = CheckedOutID";
		PreparedStatement stmt = null;
    	ResultSet rs = null;
    	try {
    		stmt = conn.prepareStatement(query);
    		stmt.setInt(1, guestID);
    		rs = stmt.executeQuery();
    		System.out.println("Titles of movies checked out by patron " + guestID + ":");
    		while(rs.next()) {
    			String title = rs.getString("Title");
    			System.out.println(title);
    		}
    	} catch(SQLException e) {
    		System.out.println(e.getMessage());
    	} finally {
    		try { if (rs != null) rs.close(); } catch (Exception e) {};
    	    try { if (stmt != null) stmt.close(); } catch (Exception e) {};
    	    try { if (conn != null) conn.close(); } catch (Exception e) {};
    	}
    }
    
    public static void getCheckoutsOfMoviesByActor(Connection conn, int actorID) {
    	String query = "SELECT Name, Title FROM Content, ChecksOut, Person, Acts WHERE "
    			+ "Kind = \'Movie\' AND GuestID = PersonID AND ContentID = CheckedOutID AND MovieID = ContentID"
    			+ " AND ActorID = ?";
		PreparedStatement stmt = null;
    	ResultSet rs = null;
    	try {
    		stmt = conn.prepareStatement(query);
    		stmt.setInt(1, actorID);
    		rs = stmt.executeQuery();
    		System.out.println("Titles of movies checked out of actor " + actorID + ":");
    		while(rs.next()) {
    			String title = rs.getString("Title");
    			String name = rs.getString("Name");
    			System.out.println(name + " checked out " + title);
    		}
    	} catch(SQLException e) {
    		System.out.println(e.getMessage());
    	} finally {
    		try { if (rs != null) rs.close(); } catch (Exception e) {};
    	    try { if (stmt != null) stmt.close(); } catch (Exception e) {};
    	    try { if (conn != null) conn.close(); } catch (Exception e) {};
    	}
    }
    
    public static void getMostCheckedOutMovie(Connection conn) {
    	String query = "SELECT MAX(count) as maximum, GuestID FROM "
    			+ "(SELECT COUNT(Title) as count, GuestID FROM Content, "
    			+ "ChecksOut WHERE Kind = 'Movie' AND CheckedOutID = ContentID) "
    			+ "GROUP BY GuestID";
		PreparedStatement stmt = null;
    	ResultSet rs = null;
    	try {
    		stmt = conn.prepareStatement(query);
    		rs = stmt.executeQuery();
    		System.out.println("Most checked out movie: ");
    		while(rs.next()) {
    			String max = rs.getString("maximum");
    			int guest = rs.getInt("GuestID");
    			System.out.println("guest #" + guest + " checked out " + max + " movie(s)");
    		}
    	} catch(SQLException e) {
    		System.out.println(e.getMessage());
    	} finally {
    		try { if (rs != null) rs.close(); } catch (Exception e) {};
    	    try { if (stmt != null) stmt.close(); } catch (Exception e) {};
    	    try { if (conn != null) conn.close(); } catch (Exception e) {};
    	}
    }

    public static void main(String[] args) {
    	Connection conn = initializeDB(DATABASE);
    	
    	Scanner input = new Scanner(System.in);
    
    	System.out.println("Select a query, then press enter:"
    				+ "\n1: Find the titles of all tracks by Kendrick Lamar released before 2020"
    				+ "\n2: List all the albums and their unique identifiers with less than 3 copies held by the library"
    				+ "\n3: Find the total number of albums checked out by a single patron"
    				+ "\n4: Insert an audiobook"
    				+ "\n5: Get the titles of all movies checked out by a patron"
    				+ "\n6: Get the names of all patrons and the titles of all movies they checked out by a certain actor"
    				+ "\n7: Get the number of movies rented by the patron with the most movies checked out");
    	String response = input.nextLine();
    	switch(response) {
    		case "1": 
    			selectTracksBeforeYear(conn, 1, "2020-01-01");
    			break;
    		case "2":
    			selectAlbumsUnderCopies(conn, 3);
    			break;
    		case "3":
    			countAlbumsCheckedOut(conn, 21);
    			break;
    		case "4":
    			insertAudiobook(conn, 21, new Date(1577854800000L), "The Chronicles of Narnia", "Digital", 2, "Fantasy", 14, "123-45x-543");
    			break;
    		case "5":
    			getMoviesCheckedOut(conn, 1);
    			break;
    		case "6":
    			getCheckoutsOfMoviesByActor(conn, 5);
    			break;
    		case "7":
    			getMostCheckedOutMovie(conn);
    			break;
    		default:
    			break;
    	}
    	input.close();
    }
}
