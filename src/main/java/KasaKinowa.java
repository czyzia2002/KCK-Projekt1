import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialog;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import models.Category;
import models.Movie;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import models.Seat;
import models.Ticket;

import java.awt.*;
import java.awt.BorderLayout;
import java.awt.color.ColorSpace;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class KasaKinowa {
    static Terminal terminal;
    static Screen screen;
    static MultiWindowTextGUI gui;
    static BasicWindow window;

    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm");

    static Database db = new Database();

    public static void confirming() {
        Panel panel = new Panel(new GridLayout(4));
        GridLayout gridLayout = (GridLayout)panel.getLayoutManager();
        gridLayout.setHorizontalSpacing(3);

        Label title1 = new Label("Dziękujemy za zakup biletu!");
        title1.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                2,
                1));
        Label title2 = new Label("Po wrzuceniu monet, Twój bilet zostanie wydrukowany.");
        title2.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                2,
                1));
        Label title3 = new Label("Życzymy miłego seansu! :)");
        title3.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                2,
                1));
        Label title4 = new Label("Kliknij ENTER, aby zakończyć transakcję");
        title4.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                2,
                1));

        Label picture = new Label("─────█─▄▀█──█▀▄─█─────\n" +
                "────▐▌──────────▐▌────\n" +
                "────█▌▀▄──▄▄──▄▀▐█────\n" +
                "───▐██──▀▀──▀▀──██▌───\n" +
                "──▄████▄──▐▌──▄████▄──\n");


        picture.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                4,
                1));

        Button enterButton = new Button("Enter", new Runnable() {
            @Override
            public void run() {
                Ticket ticket = new Ticket();
//                selectCategory(ticket);
                try {
                    start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        enterButton.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                2,
                1));

        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4)));
        panel.addComponent(new EmptySpace());
        panel.addComponent(title1);
        panel.addComponent(new EmptySpace());
        panel.addComponent(new EmptySpace());
        panel.addComponent(title2);
        panel.addComponent(new EmptySpace());
        panel.addComponent(new EmptySpace());
        panel.addComponent(title3);
        panel.addComponent(new EmptySpace());
        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4)));
        panel.addComponent(new EmptySpace());
        panel.addComponent(title4);
        panel.addComponent(new EmptySpace());

        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4)));

        panel.addComponent(new EmptySpace());
        panel.addComponent(enterButton);
        panel.addComponent(new EmptySpace());

        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4)));
        panel.addComponent(picture);
        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4)));

        window.setComponent(panel.withBorder(Borders.doubleLine("Sukces!")));
    }

    public static void selectTicket(Ticket ticket, Category c, Movie m, String date, String hour, Seat s){
        Panel panel = new Panel(new GridLayout(4));
        GridLayout gridLayout = (GridLayout)panel.getLayoutManager();
        gridLayout.setHorizontalSpacing(3);

        Label title = new Label("Sprawdź, czy dane na Twoim bilecie się zgadzają:");
        title.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                2,
                1));

        Label ticketData = new Label(
                "           B I L E T           \n\n" +
                "   TYTUŁ:      " + ticket.getMovie().getTitle() + "\n" +
                "   DATA:       " + ticket.getDate() + "\n" +
                "   GODZINA:    " + ticket.getHour() + " \n" +
                "   SALA        " +     ticket.getRoom() + " \n" +
                "   MIEJSCE:    " +     ticket.getSeat() + " \n" +
                "   CENA:       " +     ticket.calculatePrice() + "zł \n" +
                "\n");
        ticketData.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                2,
                1));

        Button confirmButton = new Button("Zatwierdź", new Runnable() {
            @Override
            public void run() {
                try {
                    db.confirmTicket(ticket);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                confirming();
            }
        });
        confirmButton.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                1,
                1));

        Button backButton = new Button("Cofnij", new Runnable() {
            @Override
            public void run() {
                ticket.setDiscount(false);
                selectDiscount(ticket, c, m, date, hour, s);
            }
        });
        backButton.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                1,
                1));

        Button cancelButton = new Button("Anuluj zakup biletu", new Runnable() {
            @Override
            public void run() {
                try {
                    ticket.setCategory(null);
                    ticket.setMovie(null);
                    ticket.setDate(null);
                    ticket.setHour(null);
                    ticket.setSeat(0);
                    ticket.setRoom(0);
                    ticket.setType(null);
                    ticket.setDiscount(false);
                    start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        cancelButton.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                2,
                1));

        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4)));

        panel.addComponent(new EmptySpace());
        panel.addComponent(title);
        panel.addComponent(new EmptySpace());
        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4)));
        panel.addComponent(new EmptySpace());
        panel.addComponent(ticketData.withBorder(Borders.doubleLineReverseBevel()));
        panel.addComponent(new EmptySpace());


        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4)));
        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4)));
        panel.addComponent(new EmptySpace());
        panel.addComponent(backButton);
        panel.addComponent(confirmButton);
        panel.addComponent(new EmptySpace());
        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4)));
        panel.addComponent(new EmptySpace());
        panel.addComponent(cancelButton);
        panel.addComponent(new EmptySpace());
        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4)));

        window.setComponent(panel.withBorder(Borders.doubleLine("Krok 8.")));
    }

    public static void selectDiscount(Ticket ticket, Category c, Movie m, String date, String hour, Seat s){
        Panel panel = new Panel(new GridLayout(4));
        GridLayout gridLayout = (GridLayout)panel.getLayoutManager();
        gridLayout.setHorizontalSpacing(3);

        Label title = new Label("Czy posiadasz kod rabatowy?");
        title.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                2,
                1));


        Button discountYes = new Button("Tak", new Runnable() {
            @Override
            public void run() {
                String input = TextInputDialog.showDialog(gui, "Kod rabatowy", "" +
                        "   Wprowadź tu swój kod rabatowy:  " +
                        "", "");
                if (input == null) {
//                    MessageDialog.showMessageDialog(gui, "Wiadomość", "\n   Twój kod rabatowy jest niepoprawny :(    \n");
                }
                else{
                    if (!input.equals("123")) {
                        MessageDialog.showMessageDialog(gui, "Wiadomość", "\n   Twój kod rabatowy jest niepoprawny :(    \n"
                        );
                    }
                    else {
                        ticket.setDiscount(true);
                        selectTicket(ticket, c, m, date, hour, s);
                    }
                }
            }
        });

        discountYes.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                1,
                1));

        Button discountNo = new Button("Nie", new Runnable() {
            @Override
            public void run() {
                selectTicket(ticket, c, m, date, hour, s);
            }
        });
        discountNo.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                1,
                1));

        Button backButton = new Button("Cofnij", new Runnable() {
            @Override
            public void run() {
                ticket.setType(null);
                selectPayment(ticket, c, m, date, hour, s);
            }
        });
        backButton.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                1,
                1));

        Button cancelButton = new Button("Anuluj zakup biletu", new Runnable() {
            @Override
            public void run() {
                try {
                    ticket.setCategory(null);
                    ticket.setMovie(null);
                    ticket.setDate(null);
                    ticket.setHour(null);
                    ticket.setSeat(0);
                    ticket.setType(null);
                    start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        cancelButton.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                2,
                1));

        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4)));

        panel.addComponent(new EmptySpace());
        panel.addComponent(title);
        panel.addComponent(new EmptySpace());

        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4)));
        panel.addComponent(new EmptySpace());
        panel.addComponent(discountYes);
        panel.addComponent(discountNo);
        panel.addComponent(new EmptySpace());

        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4)));
        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4)));
        panel.addComponent(new EmptySpace());
        panel.addComponent(backButton);
        panel.addComponent(cancelButton);

        window.setComponent(panel.withBorder(Borders.doubleLine("Krok 7.")));
    }

    public static void selectPayment(Ticket ticket, Category c, Movie m, String date, String hour, Seat s){
        Panel panel = new Panel(new GridLayout(4));
        GridLayout gridLayout = (GridLayout)panel.getLayoutManager();
        gridLayout.setHorizontalSpacing(3);

        Label title = new Label("Wybierz rodzaj biletu, który Cię interesuje:");
        title.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                2,
                1));

        ActionListBox actionListBox = new ActionListBox();
        actionListBox.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                2,
                1));

        actionListBox.addItem("Normalny     -   30zł", new Runnable() {
            @Override
            public void run() {
                ticket.setType("Normalny");
                selectDiscount(ticket, c, m, date, hour, s);
            }
        });
        actionListBox.addItem("Ulgowy       -   20zł", new Runnable() {
            @Override
            public void run() {
                ticket.setType("Ulgowy");
                selectDiscount(ticket, c, m, date, hour, s);
            }
        });

        Button backButton = new Button("Cofnij", new Runnable() {
            @Override
            public void run() {
                ticket.setSeat(0);
                ticket.setRoom(0);
                try {
                    selectMovieSeat(ticket, c, m, date, hour);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        backButton.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                1,
                1));

        Button cancelButton = new Button("Anuluj zakup biletu", new Runnable() {
            @Override
            public void run() {
                try {
                    ticket.setCategory(null);
                    ticket.setMovie(null);
                    ticket.setDate(null);
                    ticket.setHour(null);
                    ticket.setSeat(0);
                    ticket.setRoom(0);
                    start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        cancelButton.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                1,
                1));

        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4)));

        panel.addComponent(new EmptySpace());
        panel.addComponent(title);
        panel.addComponent(new EmptySpace());

        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4)));

        panel.addComponent(new EmptySpace());
        panel.addComponent(actionListBox);
        panel.addComponent(new EmptySpace());

        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4)));
        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4)));
        panel.addComponent(new EmptySpace());
        panel.addComponent(backButton);
        panel.addComponent(cancelButton);
        panel.addComponent(new EmptySpace());

        window.setComponent(panel.withBorder(Borders.doubleLine("Krok 6.")));
    }

    public static void selectMovieSeat(Ticket ticket, Category c, Movie m, String date, String hour) throws IOException, SQLException, ParseException {
        Panel panel = new Panel(new GridLayout(5));
        GridLayout gridLayout = (GridLayout)panel.getLayoutManager();
        gridLayout.setHorizontalSpacing(0);

        Label title = new Label("Wybierz miejsce, które Cię interesuje:");
        title.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                false,
                false,
                3,
                1));

        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(5)));

        panel.addComponent(new EmptySpace());
        panel.addComponent(title);
        panel.addComponent(new EmptySpace());

        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(5)));

        Label ekran = new Label(" ----------------------------- \n" +
                "|                             |\n" +
                "|                             |\n" +
                "|        TU JEST EKRAN        |\n" +
                "|                             |\n" +
                "|                             |\n" +
                "`-----------------------------'");
        ekran.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                5,
                1));
        ekran.setForegroundColor(TextColor.ANSI.WHITE);
        ekran.setBackgroundColor(TextColor.ANSI.BLACK);
        panel.addComponent(ekran.withBorder(Borders.doubleLineReverseBevel()));
        panel.addComponent(new EmptySpace());
        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(5)));
        panel.addComponent(new EmptySpace());
        int i = 1;

        List<Seat> seats = db.getSeats(ticket.getMovie(), ticket.getDate().toString(), ticket.getHour().toString());
        for (Seat s: seats) {
            ActionListBox actionListBox = new ActionListBox();
            actionListBox.setLayoutData(GridLayout.createLayoutData(
                    GridLayout.Alignment.CENTER,
                    GridLayout.Alignment.CENTER,
                    true,
                    false,
                    1,
                    1));
            if (s.isAvaliability() == false) {
                actionListBox.addItem(" MIEJSCE " + s.getSeat(), new Runnable() {
                    @Override
                    public void run() {
                        MessageDialog.showMessageDialog(gui, "Wiadomość", "\n   To miejsce jest już zarezerwowane :(    \n" +
                                "   Spróbuj wybrać inne!    \n", MessageDialogButton.OK);
                    }
                });
                panel.addComponent(actionListBox.withBorder(Borders.singleLineBevel()));
            }

            else {
                actionListBox.addItem(" MIEJSCE " + s.getSeat(), new Runnable() {
                    @Override
                    public void run() {
                        ticket.setSeat(s.getSeat());
                        ticket.setRoom(s.getRoom());
                        selectPayment(ticket, c, m, date, hour, s);
                    }
                });
                panel.addComponent(actionListBox.withBorder(Borders.doubleLineReverseBevel()));
            }

            if (i == 3){
                panel.addComponent(new EmptySpace());
                panel.addComponent(new EmptySpace());
            }
            else if (i == 6 ){
                panel.addComponent(new EmptySpace());
            }
            i++;
        }

        Button backButton = new Button("Cofnij", new Runnable() {
            @Override
            public void run() {
                ticket.setHour(null);
                try {
                    selectMovieHour(ticket, c, m, date);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        backButton.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                1,
                1));

        Button cancelButton = new Button("Anuluj zakup biletu", new Runnable() {
            @Override
            public void run() {
                try {
                    ticket.setCategory(null);
                    ticket.setMovie(null);
                    ticket.setDate(null);
                    ticket.setHour(null);
                    start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        cancelButton.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                2,
                1));

        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(5)));
        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(5)));
        panel.addComponent(new EmptySpace());
        panel.addComponent(backButton);
        panel.addComponent(cancelButton);
        panel.addComponent(new EmptySpace());

        window.setComponent(panel.withBorder(Borders.doubleLine("Krok 5.")));
    }

    public static void selectMovieHour(Ticket ticket, Category c, Movie m, String date) throws IOException, SQLException, ParseException {
        if (ticket.getCategory()==null){System.out.println("Kategoria null");}
        else{System.out.println(ticket.getCategory().getName().toString());}
        if (ticket.getMovie()==null){System.out.println("Film null");}
        else{System.out.println(ticket.getMovie().getTitle().toString());}
        System.out.println();

        Panel panel = new Panel(new GridLayout(4));
        GridLayout gridLayout = (GridLayout)panel.getLayoutManager();
        gridLayout.setHorizontalSpacing(3);

        Label title = new Label("Wybierz godzinę, która Cię interesuje:");
        title.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                2,
                1));

        ActionListBox actionListBox = new ActionListBox();
        actionListBox.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                2,
                1));

        List<String> hours = db.getMovieHours(ticket.getMovie(), ticket.getDate().toString());
        for (String h : hours) {
            actionListBox.addItem(h, new Runnable() {
                @Override
                public void run() {
                    ticket.setHour(h);
                    try {
                        selectMovieSeat(ticket, c, m, date, h);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        Button backButton = new Button("Cofnij", new Runnable() {
            @Override
            public void run() {
                ticket.setDate(null);
                try {
                    selectMovieDay(ticket, c, m);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        backButton.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                1,
                1));

        Button cancelButton = new Button("Anuluj zakup biletu", new Runnable() {
            @Override
            public void run() {
                try {
                    ticket.setCategory(null);
                    ticket.setMovie(null);
                    ticket.setDate(null);
                    start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        cancelButton.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                1,
                1));

        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4)));

        panel.addComponent(new EmptySpace());
        panel.addComponent(title);
        panel.addComponent(new EmptySpace());

        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4)));

        panel.addComponent(new EmptySpace());
        panel.addComponent(actionListBox);
        panel.addComponent(new EmptySpace());

        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4)));
        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4)));
        panel.addComponent(new EmptySpace());
        panel.addComponent(backButton);
        panel.addComponent(cancelButton);
        panel.addComponent(new EmptySpace());

        window.setComponent(panel.withBorder(Borders.doubleLine("Krok 4.")));
    }

    public static void selectMovieDay(Ticket ticket, Category c, Movie m) throws IOException, SQLException, ParseException {
        if (ticket.getCategory()==null){System.out.println("Kategoria null");}
        else{System.out.println(ticket.getCategory().getName().toString());}
        if (ticket.getMovie()==null){System.out.println("Film null");}
        else{System.out.println(ticket.getMovie().getTitle().toString());}
        System.out.println();

        Panel panel = new Panel(new GridLayout(4));
        GridLayout gridLayout = (GridLayout)panel.getLayoutManager();
        gridLayout.setHorizontalSpacing(3);

        Label title = new Label("Wybierz dzień, który Cię interesuje:");
        title.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                2,
                1));

        ActionListBox actionListBox = new ActionListBox();
        actionListBox.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                2,
                1));

        List<String> dates = db.getMovieDates(ticket.getMovie());
        for (String d : dates) {
            actionListBox.addItem(d, new Runnable() {
                @Override
                public void run() {
                    try {
                        ticket.setDate(d);
                        selectMovieHour(ticket, c, m, d);
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        Button backButton = new Button("Cofnij", new Runnable() {
            @Override
            public void run() {
                ticket.setMovie(null);
                selectMovie(ticket, c);
            }
        });
        backButton.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                1,
                1));

        Button cancelButton = new Button("Anuluj zakup biletu", new Runnable() {
            @Override
            public void run() {
                try {
                    ticket.setCategory(null);
                    ticket.setMovie(null);
                    start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        cancelButton.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                1,
                1));

        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4)));

        panel.addComponent(new EmptySpace());
        panel.addComponent(title);
        panel.addComponent(new EmptySpace());

        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4)));

        panel.addComponent(new EmptySpace());
        panel.addComponent(actionListBox);
        panel.addComponent(new EmptySpace());

        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4)));
        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4)));
        panel.addComponent(new EmptySpace());
        panel.addComponent(backButton);
        panel.addComponent(cancelButton);
        panel.addComponent(new EmptySpace());


        window.setComponent(panel.withBorder(Borders.doubleLine("Krok 3.")));
    }

    public static void selectMovie(Ticket ticket, Category c) {
        Panel panel = new Panel(new GridLayout(4));
        GridLayout gridLayout = (GridLayout)panel.getLayoutManager();
        gridLayout.setHorizontalSpacing(3);

        Label title = new Label("Wybierz film, który Cię interesuje:");
        title.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                2,
                1));

        ActionListBox actionListBox = new ActionListBox();
        actionListBox.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                2,
                1));

        List<Movie> movies = db.getMovies(c.getName().toString());
        for (Movie m : movies) {
            actionListBox.addItem(m.getTitle(), new Runnable() {
                @Override
                public void run() {
                    try {
                        ticket.setMovie(m);
                        selectMovieDay(ticket, c, m);
                    } catch (IOException | SQLException | ParseException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        Button backButton = new Button("Cofnij", new Runnable() {
            @Override
            public void run() {
                ticket.setCategory(null);
                selectCategory(ticket);
            }
        });
        backButton.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                1,
                1));

        Button cancelButton = new Button("Anuluj zakup biletu", new Runnable() {
            @Override
            public void run() {
                try {
                    ticket.setCategory(null);

                    start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        cancelButton.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                1,
                1));

        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4)));

        panel.addComponent(new EmptySpace());
        panel.addComponent(title);
        panel.addComponent(new EmptySpace());

        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4)));

        panel.addComponent(new EmptySpace());
        panel.addComponent(actionListBox);
        panel.addComponent(new EmptySpace());

        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4)));
        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4)));
        panel.addComponent(new EmptySpace());
        panel.addComponent(backButton);
        panel.addComponent(cancelButton);
        panel.addComponent(new EmptySpace());

        window.setComponent(panel.withBorder(Borders.doubleLine("Krok 2.")));
    }

    public static void selectCategory(Ticket ticket) {
        Panel panel = new Panel(new GridLayout(4));
        GridLayout gridLayout = (GridLayout)panel.getLayoutManager();
        gridLayout.setHorizontalSpacing(3);

        Label title = new Label("Wybierz kategorię, która Cię interesuje:");
        title.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                2,
                1));

        ActionListBox actionListBox = new ActionListBox();
        actionListBox.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                2,
                1));

        List<Category> categories = db.getCategories();
        for (Category c : categories) {
            actionListBox.addItem(c.getName(), new Runnable() {
                @Override
                public void run() {
                    ticket.setCategory(c);
                    selectMovie(ticket, c);
                }
            });
        }

        Button backButton = new Button("Cofnij", new Runnable() {
            @Override
            public void run() {
                try {
                    start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        backButton.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                1,
                1));

        Button cancelButton = new Button("Anuluj zakup biletu", new Runnable() {
            @Override
            public void run() {
                try {
                    start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        cancelButton.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                1,
                1));

        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4)));

        panel.addComponent(new EmptySpace());
        panel.addComponent(title);
        panel.addComponent(new EmptySpace());

        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4)));

        panel.addComponent(new EmptySpace());
        panel.addComponent(actionListBox);
        panel.addComponent(new EmptySpace());

        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4)));
        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4)));
        panel.addComponent(new EmptySpace());
        panel.addComponent(backButton);
        panel.addComponent(cancelButton);
        panel.addComponent(new EmptySpace());

        window.setComponent(panel.withBorder(Borders.doubleLine("Krok 1.")));
    }

    public static void start() throws IOException {
        Panel panel = new Panel(new GridLayout(4));
        GridLayout gridLayout = (GridLayout)panel.getLayoutManager();
        gridLayout.setHorizontalSpacing(3);

        Label title = new Label("Kliknij ENTER aby rozpocząć zakup biletu");
        title.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                2,
                1));

        Button enterButton = new Button("Enter", new Runnable() {
            @Override
            public void run() {
                Ticket ticket = new Ticket();
                selectCategory(ticket);
            }
        });
        enterButton.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                2,
                1));

        Label logo = new Label("▒▒▒▒▒▒▐███████▌\n" +
                "▒▒▒▒▒▒▐░▀░▀░▀░▌\n" +
                "▒▒▒▒▒▒▐▄▄▄▄▄▄▄▌\n" +
                "▄▀▀▀█▒▐░▀▀▄▀▀░▌▒█▀▀▀▄\n" +
                "▌▌▌▌▐▒▄▌░▄▄▄░▐▄▒▌▐▐▐▐\n");
        logo.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false,
                4,
                1));

        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4)));

        panel.addComponent(new EmptySpace());
        panel.addComponent(title);
        panel.addComponent(new EmptySpace());

        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4)));

        panel.addComponent(new EmptySpace());
        panel.addComponent(enterButton);
        panel.addComponent(new EmptySpace());

        panel.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4)));
        panel.addComponent(logo);

        window.setTitle("Samoobsługowa kasa kinowa");
        window.setComponent(panel.withBorder(Borders.doubleLine()));
        gui.addWindowAndWait(window);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        terminal = new DefaultTerminalFactory().createTerminal();
        screen = new TerminalScreen(terminal);
        gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLACK_BRIGHT));

        screen.startScreen();
        window = new BasicWindow();
        window.setFixedSize(terminal.getTerminalSize());
        window.setHints(Arrays.asList(Window.Hint.FULL_SCREEN));
        window.setHints(Collections.singletonList(Window.Hint.CENTERED));

        start();
    }
}



