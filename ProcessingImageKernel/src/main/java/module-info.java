module org.processing {
        requires javafx.controls;
        requires java.desktop;
        requires javafx.swing;
        opens org.processing to javafx.graphics;
        exports org.processing;
}