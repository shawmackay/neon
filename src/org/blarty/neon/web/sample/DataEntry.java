package org.jini.projects.neon.web.sample;

public class DataEntry {

        private String name;

        private String value;

        public DataEntry(String name, String value) {
                setValue(value);
                setName(name);
        }

        public DataEntry(String name, String[] value) {
                setValue(value);
                setName(name);
        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public String getValue() {
                return value;
        }

        public void setValue(String value) {
                this.value = value;
        }

        public void setValue(String[] values) {
                StringBuffer valueBuffer = new StringBuffer();
                for (String s : values)
                        valueBuffer.append("[" + s + "] ");
                value = valueBuffer.toString().trim();
        }

}
