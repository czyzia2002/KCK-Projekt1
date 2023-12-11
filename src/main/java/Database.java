import com.sun.jna.platform.FileUtils;
import models.Category;
import models.Movie;
import models.Seat;
import models.Ticket;
import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import java.sql.Connection;

import java.sql.DriverManager;

import java.sql.SQLException;

public class Database {
    private Connection connection = null;
    private Statement statement;

    public Database () {
        AppDirs appDirs = AppDirsFactory.getInstance();
        File file = new File(
                appDirs.getSiteDataDir(
                        "KasaKinowa",
                        "1.0.0",
                        "Paulina Czyżewska"
                ),
                "database.sqlite"
        );


        if (!file.exists()) {
            file.getParentFile().mkdirs();

            InputStream stream = getClass().getResourceAsStream("database.sqlite");
            try {
                assert stream != null;
                Files.copy(stream, Paths.get(file.getAbsolutePath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try
        {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
            statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
        }
        catch(SQLException e)
        {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        }
    }

    public void close () {
        try
        {
            if(connection != null)
                connection.close();
        }
        catch(SQLException e)
        {
            // connection close failed.
            System.err.println(e.getMessage());
        }
    }

    public List<Category> getCategories () {
        List<Category> categories = new ArrayList<>();

        try {
            ResultSet rs = statement.executeQuery("select name from Category");
//            ResultSet rs = statement.executeQuery("select * from Category c LEFT JOIN Movie m where c.categoryID = m.movieCategory");
            while (rs.next()) {
                Category category = new Category();
                category.setName(rs.getString("name"));
                categories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }

    public List<Movie> getMovies (String type) {
        List<Movie> movies = new ArrayList<>();

        try {
            ResultSet rs;
            if (type.equals("Horror")) {
                rs = statement.executeQuery("select title, name from Category c LEFT JOIN Movie m where c.categoryID = m.movieCategory and name = 'Horror'");
            }
            else if (type.equals("Komedia")) {
                rs = statement.executeQuery("select title, name from Category c LEFT JOIN Movie m where c.categoryID = m.movieCategory and name = 'Komedia'");
            }
            else if (type.equals("Sci-Fi")) {
                rs = statement.executeQuery("select title, name from Category c LEFT JOIN Movie m where c.categoryID = m.movieCategory and name = 'Sci-Fi'");
            }
            else if(type.equals("Film romantyczny")){
                rs = statement.executeQuery("select title, name from Category c LEFT JOIN Movie m where c.categoryID = m.movieCategory and name = 'Film romantyczny'");
            }
            else {
                rs = statement.executeQuery("select title, name from Category c LEFT JOIN Movie m where c.categoryID = m.movieCategory and name = 'Film animowany'");
            }
            while (rs.next()) {
                Movie movie = new Movie();
                movie.setTitle(rs.getString("title"));
//                movie.setCategoryName(rs.getString("mo"));
                movies.add(movie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return movies;
    }

    public List<String> getMovieDates (Movie movie) throws SQLException {
        List<String> dates = new ArrayList<>();
        try {
            ResultSet rs = statement.executeQuery("select distinct data from Avaliability left join Movie on Movie.movieID = Avaliability.movieID where title = \'" + movie.getTitle() + "\'group by data");

            while (rs.next()) {
                String dateString = rs.getString("data");
                dates.add(dateString);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dates;
    }

    public List<String> getMovieHours (Movie movie, String date) throws SQLException {
        List<String> hours = new ArrayList<>();
        try {
            ResultSet rs = statement.executeQuery("select distinct hour from Avaliability left join Movie on Movie.movieID = Avaliability.movieID where title = \'" + movie.getTitle() + "\' and data = \'" + date + "\'");

            while (rs.next()) {
                String hour = rs.getString("hour");
                hours.add(hour);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return hours;
    }

    public List<Seat> getSeats (Movie movie, String date, String hour) throws SQLException {
        List<Seat> seats = new ArrayList<>();
        try {
            ResultSet rs = statement.executeQuery("select room, seat, availability from Avaliability left join Movie on Movie.movieID = Avaliability.movieID where title = \'" + movie.getTitle() + "\' and data = \'" + date + "\' and hour = \'" + hour + "\'");

            while (rs.next()) {
                Seat seat = new Seat();
                seat.setRoom(rs.getInt("room"));
                seat.setSeat(rs.getInt("seat"));
                seat.setAvaliability(rs.getBoolean("availability"));
                seats.add(seat);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return seats;
    }
    public void confirmTicket (Ticket ticket) throws SQLException {
        try {
            statement.execute("update Avaliability\n" +
                    "set availability = false\n" +
                    "where data = \'" + ticket.getDate() + "\' and room = \'" + ticket.getRoom() + "\' and seat = \'"
                    + ticket.getSeat() + "\' and hour = \'" + ticket.getHour() + "\'");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
