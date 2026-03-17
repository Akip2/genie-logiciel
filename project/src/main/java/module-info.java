module org.example.project {
    requires javafx.controls;
    requires transitive javafx.graphics;
    requires org.apache.poi.ooxml;
    requires org.apache.poi.poi;
    requires java.desktop;

    exports com.example.testfx;
}
