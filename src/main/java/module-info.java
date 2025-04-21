module com.slinky.wordcheat {
    requires javafx.controls;
    requires com.fasterxml.jackson.databind;
    exports com.slinky.wordcheat;
    exports com.slinky.wordcheat.persistence;
    
    opens com.slinky.wordcheat.persistence 
        to com.fasterxml.jackson.databind;
}
