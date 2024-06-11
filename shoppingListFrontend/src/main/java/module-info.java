module pl.project.oop2.shoppinglistgui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.json;

    opens pl.project.oop2.shoppinglistgui to javafx.fxml;
    exports pl.project.oop2.shoppinglistgui;
}