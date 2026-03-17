module org.example.project {
    requires javafx.controls;
    requires transitive javafx.graphics;
    requires org.apache.poi.ooxml;
    requires org.apache.poi.poi;
    requires java.desktop;
    requires org.jfree.jfreechart;
    requires org.jfree.chart.fx;

    exports com.example.testfx;
    exports com.example.testfx.chart;
    exports com.example.testfx.dto;
    exports com.example.testfx.model;
    exports com.example.testfx.service;
    exports com.example.testfx.data;
}
