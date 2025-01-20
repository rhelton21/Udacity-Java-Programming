module com.udacity.catpoint.security {
    requires com.udacity.catpoint.image; // Dependency on the image service
    requires miglayout; // Dependency for MigLayout library
    requires java.desktop; // Required for Swing and AWT
    requires java.logging; // Added to resolve logging package access
    requires com.google.common; // Dependency for Google Guava
    requires org.slf4j; // Dependency for SLF4J logging
    requires com.google.gson; // Dependency for Gson
    requires java.prefs; // Dependency for Preferences API

    opens com.udacity.catpoint.security.data to com.google.gson; // Open package for Gson deserialization
}
