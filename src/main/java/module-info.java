module com.slinky.wordcheat {
    requires javafx.controls;
    requires javafx.swing;
    requires com.fasterxml.jackson.databind;
    
    exports com.slinky.wordcheat;
    exports com.slinky.wordcheat.io;
    
    opens com.slinky.wordcheat.io 
        to com.fasterxml.jackson.databind;
}
