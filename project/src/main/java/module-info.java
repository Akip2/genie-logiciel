module org.example.project {
    requires javafx.controls;
    requires transitive javafx.graphics;
    requires org.apache.poi.ooxml;
    requires org.apache.poi.poi;
    requires java.desktop;
    requires org.jfree.jfreechart;
    requires org.jfree.chart.fx;

    exports com.example.testfx;

    opens com.example.testfx.chart to javafx.graphics;
}
