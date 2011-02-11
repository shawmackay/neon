package org.jini.projects.neon.di.tests;

import net.jini.core.entry.Entry;
import net.jini.export.Exporter;

public class Simple {
    private String testString;

    private Entry[] initialLookupAttributes;

    private Exporter myExporter;

    private Dependent theDependent;

    public Dependent getTheDependent() {
            return theDependent;
    }

    public void setTheDependent(Dependent theDependent) {
            this.theDependent = theDependent;
    }

    public void setTheDependent2(Dependent theDependent) {
            this.theDependent = theDependent;
    }

    public void setTheDependent3(Dependent theDependent) {
            this.theDependent = theDependent;
    }

    public void setTheDependent4(Dependent theDependent) {
            this.theDependent = theDependent;
    }

    public void setTheDependent5(Dependent theDependent) {
            this.theDependent = theDependent;
    }

    public String getTestString() {
            return testString;
    }

    public void setTestString(String testString) {
            this.testString = testString;
    }

    public Entry[] getInitialLookupAttributes() {
            return initialLookupAttributes;
    }

    public void setInitialLookupAttributes(Entry[] initialLookupAttributes) {
            this.initialLookupAttributes = initialLookupAttributes;
    }

    public Exporter getMyExporter() {
            return myExporter;
    }

    public void setMyExporter(Exporter myExporter) {
            this.myExporter = myExporter;
    }

}
